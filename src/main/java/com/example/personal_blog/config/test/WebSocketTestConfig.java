package com.example.personal_blog.config.test;

import com.example.personal_blog.event.websocket.test.WebSocketTestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
public class WebSocketTestConfig {

    @Bean
    public WebSocketClient webSocketClient() {
        return new StandardWebSocketClient(); // 또는 필요한 WebSocketClient의 구현체
    }

    @Bean
    public WebSocketTestClient webSocketTestClient(WebSocketClient webSocketClient) {
        return new WebSocketTestClient(webSocketClient);
    }
}
