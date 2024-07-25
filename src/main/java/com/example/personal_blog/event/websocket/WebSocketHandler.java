package com.example.personal_blog.event.websocket;

import com.example.personal_blog.dto.NotificationDto;
import com.example.personal_blog.entity.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

@Slf4j
@RequiredArgsConstructor
@Getter
public class WebSocketHandler extends StompSessionHandlerAdapter {

    private final CompletableFuture<NotificationDto> completableFuture;
    private StompSession session;

    /**
     * WebSocket 연결이 성공적으로 이루어진 후 호출
     * @param session the client STOMP session
     * @param connectedHeaders the STOMP CONNECTED frame headers
     */
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("New session established: {}", session.getSessionId());
        this.session = session;
    }

    /**
     * 구독된 토픽으로부터 오는 메시지 유형 지정
     * @param headers the headers of a message
     * @return
     */
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return NotificationDto.class;
    }

    /**
     * 서버로부터 메시지를 받을 때 호출, Notification 객체를 CompletableFuture에 저장
     * @param headers the headers of the frame
     * @param payload the payload, or {@code null} if there was no payload
     */
    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        log.info("Received notification: {}", payload);
        if (payload instanceof NotificationDto) {
            this.completableFuture.complete((NotificationDto) payload);
        } else {
            log.error("Unexpected payload type: {}", payload.getClass());
        }
    }

    /**
     * STOMP 명령 처리 중 예외가 발생할 때 호출
     * @param session the client STOMP session
     * @param command the STOMP command of the frame
     * @param headers the headers
     * @param payload the raw payload
     * @param exception the exception
     */
    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        log.error("에러가 발생했습니다. : {}", exception.getMessage(), exception);
    }

    /**
     * WebSocket 전송 오류가 발생할 때 호출
     * @param session the client STOMP session
     * @param exception the exception that occurred
     */
    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        log.error("전송 오류가 발생했습니다. : {}", exception.getMessage(), exception);
    }
}
