package com.example.personal_blog.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.example.personal_blog.security.JwtAuthenticationFilter;
import com.example.personal_blog.service.JpaUserDetailsService;
import com.example.personal_blog.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JpaUserDetailsService jpaUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(
                request -> request.requestMatchers("/articles/**", "/hello", "/auth/**")
                .permitAll().anyRequest().authenticated())
            .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(
                jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * AuthenticationManager는 AuthenticationProvider에게 인증 책임을 위임하고 인증을 수행합니다.
     * 이 authCofig.getAuthenticationManager()는 AuthenticationConfiguration을 사용하여
     * Spring Security가 자체 구성한 AuthenticationManager를 반환합니다.
     * 이 AuthenticationManager는 등록된 AuthenticationProvider를 사용하여 인증을 수행합니다.
     * @param authConfig
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * 사용자 인증을 위한 `DaoAuthenticationProvider` 인스턴스를 생성하고 구성합니다.
     *
     * 이 메소드는 새로운 `DaoAuthenticationProvider` 객체를 초기화하고 'UserDetailsService'와
     * 'PasswordEncoder'를 인증을 위해 설정합니다. 'UserDetailsService'는 `SecurityConfig` 클래스의
     * 'userDetailsService' 필드 값으로 설정되며, 'PasswordEncoder'는 'SecurityConfig' 클래스의
     * 'passwordEncoder()' 메소드를 통해 설정됩니다.
     *
     * @return 사용자 인증이 구성된 `DaoAuthenticationProvider` 인스턴스가 반환됩니다.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(jpaUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * 애플리케이션에서 Cross-Origin Resource Sharing (CORS)를 활성화 하는 CorsFilter 빈을 반환합니다.
     *
     * 이 메소드는 UrlBasedCorsConfigurationSource와 CorsConfiguration 객체를 생성하고,
     * 필요한 CORS 설정으로 이들을 구성하며, 이 구성을 src에 등록합니다.
     * 이 메소드는 구성된 source를 사용하여 새로운 CorsFilter 인스턴스를 생성하고 반환합니다.
     *
     * @return 지정된 CORS 설정으로 구성된 CorsFilter 빈을 반환합니다.
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOriginPattern("*"); //모든 출처를 허용
        config.addAllowedHeader("*"); //모든 헤더 허용
        config.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        config.setAllowCredentials(true);
        src.registerCorsConfiguration("/**", config);

        return new CorsFilter(src);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
