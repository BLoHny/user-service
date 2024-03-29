package com.example.userservice.service.impl;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.ResponseOrder;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;
    private final Environment env;
    private final OrderServiceClient client;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Override
    public void createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        User user = modelMapper.map(userDto, User.class);
        user.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));

        userRepository.save(user);
    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new UsernameNotFoundException("User Not Found");
        }

        UserDto userDto = new ModelMapper().map(user, UserDto.class);

/*        String orderUrl = String.format(Objects.requireNonNull(env.getProperty("order_service.url")), userId);

        ResponseEntity<List<ResponseOrder>> orderList =
                restTemplate.exchange(
                        orderUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                });*/

/*        List<ResponseOrder> list = null;
        try {
            list = client.getOrders(userId);
        } catch (FeignException ex) {
            log.error(ex.getMessage());
        }*/

        /* Error Decoder */
        /*List<ResponseOrder> list = client.getOrders(userId);*/

        log.info("Before call orders microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        List<ResponseOrder> list = circuitBreaker.run(() -> client.getOrders(userId),
                throwable -> new ArrayList<>());
        log.info("After call orders microservice");

        userDto.setOrders(list);

        return userDto;
    }

    @Override
    public Iterable<User> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String userName) {
        User user = userRepository.findByEmail(userName);
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }

        return new ModelMapper().map(user, UserDto.class);
    }
}
