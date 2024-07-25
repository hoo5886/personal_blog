package com.example.personal_blog.event.websocket.test;

import com.example.personal_blog.entity.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;

@Component
public class WebSocketTestClient {

    private final WebSocketClient webSocketClient;
    private WebSocketSession session;
    private CountDownLatch latch = new CountDownLatch(1);
    private String receivedMessage;

    public WebSocketTestClient(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    public void connect(String url) throws Exception {
        webSocketClient.doHandshake(new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                WebSocketTestClient.this.session = session;
            }

            @Override
            public void handleMessage(WebSocketSession session, org.springframework.web.socket.WebSocketMessage<?> message) {
                receivedMessage = ((TextMessage) message).getPayload();
                latch.countDown();
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) {
                latch.countDown();
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus closeStatus) {
            }

            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        }, url);
    }

    public void send(String destination, Object payload) throws Exception {
        if (session == null) {
            throw new IllegalStateException("WebSocket session is not established");
        }

        String jsonPayload = new ObjectMapper().writeValueAsString(payload);
        session.sendMessage(new TextMessage(jsonPayload));
    }

    public String receive() throws InterruptedException {
        latch.await(5, TimeUnit.SECONDS);
        return receivedMessage;
    }

    public void verifyReceivedMessage(Notification n) throws InterruptedException {
        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("Message not received in time");
        }
        if (!n.getMessage().equals(receivedMessage)) {
            throw new AssertionError("Expected message: " + n + " but received: " + receivedMessage);
        }
    }
}
