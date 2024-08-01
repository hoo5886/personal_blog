package com.example.personal_blog.controller;

import com.example.personal_blog.dto.request.SignInRequest;
import com.example.personal_blog.dto.request.SignUpRequest;
import com.example.personal_blog.dto.response.JwtAuthenticationResponse;
import com.example.personal_blog.service.AuthenticationService;
import com.example.personal_blog.service.JwtBlacklistService;
import com.example.personal_blog.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class LoginController {

    private final JwtBlacklistService jwtBlacklistService;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signup(@RequestBody SignUpRequest signUpRequest) {
        return new ResponseEntity<>(authenticationService.signUp(signUpRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SignInRequest signInRequest) {
        log.info("로그인 요청: {}", signInRequest.loginId());
        return ResponseEntity.ok(authenticationService.signIn(signInRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        log.info("로그아웃 요청: {}", request.getHeader("Authorization"));
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            long expiration = jwtService.extractExpiration(jwt).getTime() - System.currentTimeMillis();
            jwtBlacklistService.blacklistToken(jwt, expiration);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
