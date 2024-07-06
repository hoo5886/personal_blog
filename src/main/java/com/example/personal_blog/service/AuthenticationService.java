package com.example.personal_blog.service;

import com.example.personal_blog.dto.request.SignInRequest;
import com.example.personal_blog.dto.request.SignUpRequest;
import com.example.personal_blog.dto.response.JwtAuthenticationResponse;
import com.example.personal_blog.entity.Authority;
import com.example.personal_blog.entity.Role;
import com.example.personal_blog.entity.User;
import com.example.personal_blog.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse signUp(SignUpRequest signUpRequest) {
        List<Authority> authorities = List.of(Authority.builder().role(Role.ROLE_USER).build());

        var user = User.builder()
            .loginId(signUpRequest.loginId())
            .username(signUpRequest.username())
            .password(passwordEncoder.encode(signUpRequest.password()))
            .authorities(authorities).build();

        var user2 = userRepository.findByLoginId(signUpRequest.loginId());
        if (user2.isPresent()) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        } else {
            userRepository.save(user);
        }

        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    public JwtAuthenticationResponse signIn(SignInRequest signInRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequest.loginId(), signInRequest.password())
            );
            log.info("로그인 성공");
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 유효하지 않습니다.");
        }

        var user = userRepository.findByLoginId(signInRequest.loginId())
            .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 유효하지 않습니다."));

        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        String jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }
}