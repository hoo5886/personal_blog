package com.example.personal_blog.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //클라이언트로부터 구독 요청을 받을 endpoint 설정 memory에 저장하고 구독한 Client에게 broadcasting한다.
        config.enableSimpleBroker("/topic", "/queue");
        // @Controller의 @MessageMapping으로 routing되는 엔드포인트 설정
        config.setApplicationDestinationPrefixes("/app");
        // 사용자별 목적지 접두사 설정
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // "/ws" is the HTTP URL for the endpoint to which a WebSocket (or SockJS) client needs to connect for the WebSocket handshake.
        registry.addEndpoint("/ws")
            /* addInterceptors(new HttpSessionHandshakeInterceptor())
            * HTTP 세션 인터셉터 추가, Client가 Server에 접속할 때
            * 웹소켓 세션도 함께 연결하기 위해 WebSocket handshake 과정에서
            * 기존 HTTP 세션을 사용하도록 설정.
            * */
//            .addInterceptors(new HttpSessionHandshakeInterceptor())
            .setAllowedOrigins("*")
            .withSockJS();
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> converters) {
        converters.add(new MappingJackson2MessageConverter());  // JSON 변환기 추가
        return true;
    }
}
