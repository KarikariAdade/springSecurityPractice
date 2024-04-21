package com.example.springsecurity.service;

import com.example.springsecurity.models.AuthenticationResponse;
import com.example.springsecurity.models.Token;
import com.example.springsecurity.models.User;
import com.example.springsecurity.repository.TokenRepository;
import com.example.springsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final TokenRepository tokenRepository;


    public User register(User request) {

        User user = new User();

        user.setUsername(request.getUsername());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(request.getRole());

        user = userRepository.saveAndFlush(user);

        return user;
//        String token = jwtService.generateToken(user);

//        return new AuthenticationResponse(token);

    }

    public AuthenticationResponse authenticate (User request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUsername()));

        String jwt =  jwtService.generateToken(user);

        revokeExistingTokens(user);

        saveUserToken(jwt, user);

        return new AuthenticationResponse(jwt);
    }


    private void revokeExistingTokens(User user) {
        List<Token> validTokens = tokenRepository.findAllTokenByUser(user.getId());

        if (!validTokens.isEmpty()) {
            validTokens.forEach(token -> {
                token.setLoggedOut(true);
            });
        }

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String jwt, User user) {
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setUser(user);

        tokenRepository.save(token);
    }

}
