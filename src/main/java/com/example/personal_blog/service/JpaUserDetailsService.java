package com.example.personal_blog.service;

import com.example.personal_blog.entity.User;
import com.example.personal_blog.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByLoginId(loginId);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        return user.get();
    }
}
