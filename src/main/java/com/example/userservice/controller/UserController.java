package com.example.userservice.controller;

import com.example.userservice.dto.RequestUser;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final Environment env;
    private final UserService userService;

    @Value("${greeting.message}")
    private String message;

    @GetMapping("/user-service/health_check")
    public String status() {
        return String.format("It's Working in User Service on PORT %s", env.getProperty("local.server.port"));
    }

    @GetMapping("/user-service/welcome")
    public String welcome() {
        return message;
    }

    @PostMapping("/users")
    public ResponseEntity<HttpStatus> createUser(@RequestBody @Valid RequestUser user) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userService.createUser(userDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
