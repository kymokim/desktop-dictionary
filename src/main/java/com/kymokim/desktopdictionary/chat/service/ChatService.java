package com.kymokim.desktopdictionary.chat.service;

import com.kymokim.desktopdictionary.auth.entity.Auth;
import com.kymokim.desktopdictionary.auth.repository.AuthRepository;
import com.kymokim.desktopdictionary.auth.security.JwtAuthToken;
import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.auth.security.role.Role;
import com.kymokim.desktopdictionary.chat.domain.Room;
import com.kymokim.desktopdictionary.chat.dto.RequestChat;
import com.kymokim.desktopdictionary.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final AuthRepository authRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    private final ChatRoomRepository chatRoomRepository;

    private final Map<String, List<RequestChat.MessageDto>> chatRoomMessages = new HashMap<>();

    public void saveMessage(String roomId, RequestChat.MessageDto message) {
        chatRoomMessages.computeIfAbsent(roomId, k -> new ArrayList<>()).add(message);
    }

    public List<RequestChat.MessageDto> getMessages(String roomId) {
        return chatRoomMessages.getOrDefault(roomId, new ArrayList<>());
    }

    //
    public String registerRoom(String name){
        String roomUUID = chatRoomRepository.createRoom(name);
        return roomUUID;
    }

    public List<Room> getAllRoom(Optional<String> token){
        String tokenEmail = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            tokenEmail = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByEmail(tokenEmail);
        if (admin.getRole() != Role.ADMIN)
            throw new RuntimeException("No permission.");
        return chatRoomRepository.getAllRoom();
    }

    public boolean deleteRoom(String roomUUID) {
        boolean isDeleted = chatRoomRepository.deleteRoom(roomUUID);
        if (isDeleted) {
            chatRoomMessages.remove(roomUUID); // 삭제된 방의 메시지 기록도 함께 삭제
        }
        return isDeleted;
    }
}


