package com.example.personal_blog;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.personal_blog.dto.NotificationDto;
import com.example.personal_blog.entity.Article;
import com.example.personal_blog.entity.Notification;
import com.example.personal_blog.entity.User;
import com.example.personal_blog.event.websocket.WebSocketHandler;
import com.example.personal_blog.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class WebSocketIntegrationTest {

    Logger logger = org.slf4j.LoggerFactory.getLogger(WebSocketIntegrationTest.class);

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String url;

    private WebSocketStompClient stompClient;
    private WebSocketHandler webSocketHandler;
    private CompletableFuture<NotificationDto> completableFuture = new CompletableFuture<>();

    @Autowired
    private NotificationService notificationService;

    private static User user;

    @BeforeAll
    public static void setUpEntities() {
        user = User.builder()
            .userId(1L)
            .username("test")
            .loginId("testId")
            .password("12345")
            .build();
    }

    @BeforeEach
    public void setUp() {
        this.stompClient = new WebSocketStompClient(
            new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))
            )
        );
        var messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(objectMapper); // 등록된 ObjectMapper 사용
        this.stompClient.setMessageConverter(messageConverter);
        this.url = "ws://localhost:" + port + "/ws";
        this.webSocketHandler = new WebSocketHandler(new CompletableFuture<NotificationDto>());
    }

    @Test
    @Transactional
    @DisplayName("게시글 좋아요 이벤트 발생 시 알림을 보내는지 확인")
    public void likeArticle_shouldSendNotification() throws Exception {
        logger.info("포트번호 포함 URL : {}",url);
        //Client를 대체할 session을 생성한다.
        StompSession session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    logger.info("세션이 연결되었습니다. : sessionID: {}, header: {}", session.getSessionId(),
                        connectedHeaders);

                    session.subscribe("/user/testId/topic/notification",
                        new StompFrameHandler() {
                            @Override
                            public Type getPayloadType(StompHeaders headers) {
                                logger.info("Received STOMP Headers: {}", headers);
                                return NotificationDto.class;
                            }

                            @Override
                            public void handleFrame(StompHeaders headers, Object o) {
                                logger.info("Received notification: {}", o);
                                if (o instanceof NotificationDto) {
                                    completableFuture.complete((NotificationDto) o);
                                } else {
                                    logger.error("Unexpected payload type: {}", o.getClass());
                                }
                            }
                        }
                    );
                }
            }
        ).get(5, TimeUnit.SECONDS);
        logger.info("서버에 연결되었습니다. sessionId : {}", session.getSessionId());

        //when
        notificationService.sendNotification(
            Notification.builder().message("test").user(user).build(), user);

        TimeUnit.SECONDS.sleep(1);

        //then
        var notificationDto = completableFuture.get(20, TimeUnit.SECONDS);
        assertThat(notificationDto).isNotNull();
        assertThat(notificationDto.message()).contains("게시글이 좋아요를 받았습니다.");
    }
}
