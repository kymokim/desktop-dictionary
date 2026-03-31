package com.kymokim.desktopdictionary.chat.eventListener;

import com.kymokim.desktopdictionary.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@RequiredArgsConstructor
@Component
public class WebSocketEventListener {
    private final ChatRoomRepository chatRoomRepository;

    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) {
        System.out.println("연결");
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = (String) accessor.getHeader("simpSessionId");
    }
    @EventListener
    private void handleSessionDisconnected(SessionDisconnectEvent event) {
        //disconnect 시 소켓세션을 방에서 빼고, 세션 카운트 수가 0이라면 해당 방을 삭제한다
        // 동기화 응답자가 해당 방을 구독한 상태라면 동기화 요청자가 disconnect해도 카운트는 1이므로 해당 방은 살아있음, 동기화 요청쟈는 다시 구독가능
        System.out.println("연결 종료");
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = (String) accessor.getHeader("simpSessionId");
    }
    @EventListener
    private void handleSessionSubscribed(SessionSubscribeEvent event) {
        // 구독을 하면 해당 방에 소켓세션을 등록한다
        System.out.println("구독");
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = (String) accessor.getHeader("simpSessionId");
        String uuid = (String) accessor.getHeader("uuid");
        chatRoomRepository.registerSessionToRoom(uuid, sessionId);
    }
    @EventListener
    private void handleSessionUnsubscribed(SessionUnsubscribeEvent event) {
        System.out.println("구독 해제");
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = (String) accessor.getHeader("simpSessionId");
        String uuid = (String) accessor.getHeader("uuid");
        chatRoomRepository.unregisterSessionFromRoom(uuid, sessionId);
    }


}