package com.example.userservice.service.impl;

import com.example.userservice.dto.ResponseOrder;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;
    private final Environment env;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RestTemplate restTemplate,
            Environment env
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
        this.env = env;
    }

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

        String orderUrl = String.format(Objects.requireNonNull(env.getProperty("order_service.url")), userId);

        ResponseEntity<List<ResponseOrder>> orderList =
                restTemplate.exchange(
                        orderUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                });

        userDto.setOrders(orderList.getBody());

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
