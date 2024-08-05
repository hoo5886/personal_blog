package com.example.personal_blog;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.personal_blog.dto.NotificationDto;
import com.example.personal_blog.entity.Notification;
import com.example.personal_blog.entity.User;
import com.example.personal_blog.service.JwtService;
import com.example.personal_blog.service.NotificationService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.lang.reflect.Type;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class WebSocketIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketIntegrationTest.class);

    @LocalServerPort
    private int port;

    @Value("${spring.token.signing.key}")
    private String jwtSigningKey;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private NotificationService notificationService;

    private WebSocketStompClient stompClient;
    private String websocketUri;
    private final String WEBSOCKET_QUEUE = "/user/queue/notification";

    private final CompletableFuture<NotificationDto> completableFuture = new CompletableFuture<>();

    @BeforeEach
    public void setUp() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);

        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        this.websocketUri = "ws://localhost:" + port + "/ws";

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(
                "testuser",
                "12345",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            )
        );
    }

    @Test
    @DisplayName("JWT 인증 후 소켓 연결 확인")
    public void testSecuredWebSocketConnection() throws ExecutionException, InterruptedException, TimeoutException {
        User user = User.builder()
            .userId(1L)
            .username("test")
            .loginId("testId")
            .password("12345")
            .enabled(true)
            .build();

        String token = jwtService.generateToken(user);
        logger.info("Generated JWT token: {}", token);

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);

        WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);

        logger.info("Attempting to connect to WebSocket at: {}", websocketUri);
        StompSession session = stompClient
            .connect(websocketUri, httpHeaders, connectHeaders, new StompSessionHandlerAdapter() {})
            .get(5, SECONDS);  // Increased timeout to 5 seconds

        assertNotNull(session, "StompSession should not be null");
        assertTrue(jwtService.isTokenValid(token, user), "Token should be valid");
        assertTrue(session.isConnected(), "StompSession should be connected");
    }

    @Test
    @DisplayName("게시글 좋아요 이벤트 발생 시 알림을 보내는지 확인")
    public void likeArticle_shouldSendNotification() throws Exception {
        logger.info("URL : {}", websocketUri);
        User user = User.builder()
            .userId(1L)
            .username("test")
            .loginId("testId")
            .password("12345")
            .build();

        String jwtToken = generateTestJwtToken(user);
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        StompSession session = stompClient.connect(websocketUri, headers, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                logger.info("세션이 연결되었습니다. : sessionID: {}, header: {}", session.getSessionId(), connectedHeaders);

                session.subscribe(WEBSOCKET_QUEUE, new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        logger.info("수신된 STOMP Headers: {}", headers);
                        return NotificationDto.class;
                    }
                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        logger.info("수신된 notificationDto: {}", payload);
                        if (payload instanceof NotificationDto) {
                            completableFuture.complete((NotificationDto) payload);
                        } else {
                            logger.error("예상치 못한 payload type: {}", payload.getClass());
                        }
                    }
                });
            }
            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                logger.error("STOMP 메시지 처리 중 예외 발생: ", exception);
                completableFuture.completeExceptionally(exception);
            }
        }).get(5, SECONDS);

        logger.info("서버에 연결되었습니다. sessionId : {}", session.getSessionId());

        // given
        notificationService.sendNotification(
            Notification.builder().message("게시글이 좋아요를 받았습니다.").user(user).build(), user
        );

        // when & then
        try {
            var notificationDto = completableFuture.get(10, SECONDS);
            assertNotNull(notificationDto);
            assertTrue(notificationDto.message().contains("게시글이 좋아요를 받았습니다."));
        } catch (TimeoutException e) {
            logger.error("TimeoutException 발생: ", e);
            fail("Notification not received in time.");
        }
    }

    private String generateTestJwtToken(User user) {
        long now = System.currentTimeMillis();
        Date validity = new Date(now + 3600000); // 1 hour validity

        return Jwts.builder()
            .setSubject(user.getLoginId())
            .claim("userId", user.getUserId())
            .claim("username", user.getUsername())
            .setIssuedAt(new Date(now))
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, getSigningKey())
            .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey); // 실제 프로젝트의 비밀 키로 대체해야 합니다
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
