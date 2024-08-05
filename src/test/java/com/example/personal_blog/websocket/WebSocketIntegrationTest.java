package com.example.personal_blog.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.client.WebSocketConnectionManager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class WebSocketIntegrationTest {

    private WebSocketClient webSocketClient;
    private WebSocketConnectionManager connectionManager;
    private CountDownLatch latch;
    private String receivedMessage;

    @BeforeEach
    public void setup() {
        webSocketClient = new StandardWebSocketClient();
        latch = new CountDownLatch(1);
        receivedMessage = "";

        connectionManager = new WebSocketConnectionManager(webSocketClient, new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                session.sendMessage(new TextMessage("Test message"));
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                receivedMessage = ((TextMessage) message).getPayload();
                latch.countDown();
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {}

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {}

            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        }, "ws://localhost:8080/ws");

        connectionManager.start();
    }

    @Test
    public void testWebSocketConnection() throws Exception {
        boolean messageReceived = latch.await(5, TimeUnit.SECONDS);
        assertEquals("Expected message", receivedMessage);
        assertTrue(messageReceived);
    }
}
