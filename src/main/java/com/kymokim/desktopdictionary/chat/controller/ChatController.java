package com.kymokim.desktopdictionary.chat.controller;

import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.chat.dto.RequestChat;
import com.kymokim.desktopdictionary.chat.service.ChatService;
import com.kymokim.desktopdictionary.common.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @Autowired
    private final SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private final ChatService chatService;

    @MessageMapping("/send/{id}")
        public void sendMessage(@DestinationVariable("id") String roomId, @Payload RequestChat.MessageDto messageDto) {
        chatService.saveMessage(roomId, messageDto);
        System.out.println(messageDto);
        simpMessagingTemplate.convertAndSend("/sub/room/" + roomId, messageDto);
    }

    @PostMapping("/start")
    public ResponseEntity<ResponseDto> registerRoom(@RequestBody Map<String, String> user){
        String roomUUID = chatService.registerRoom(user.get("name"));

        Map<String, String> responseData = new HashMap<>();
        responseData.put("roomUUID", roomUUID);

        return new ResponseEntity<>(ResponseDto.builder()
                .message("방 생성 성공")
                .data(responseData)
                .build(), HttpStatus.OK);
    }

    @GetMapping("/getAllRoom")
    public ResponseEntity<ResponseDto> getAllRoom(HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        return new ResponseEntity<>(ResponseDto.builder()
                .message("방 조회 성공")
                .data(chatService.getAllRoom(token))
                .build(), HttpStatus.OK);
    }

    @GetMapping("/getMessages/{roomId}")
    public ResponseEntity<ResponseDto> getRoomMessages(@PathVariable String roomId) {
        List<RequestChat.MessageDto> messages = chatService.getMessages(roomId);
        return new ResponseEntity<>(ResponseDto.builder()
                .message("메시지 조회 성공")
                .data(messages)
                .build(), HttpStatus.OK);
    }

    @DeleteMapping("/end/{roomUUID}")
    public ResponseEntity<ResponseDto> deleteChatRoom(@PathVariable String roomUUID) {
        boolean isDeleted = chatService.deleteRoom(roomUUID);

        if (isDeleted) {
            return new ResponseEntity<>(ResponseDto.builder()
                    .message("채팅방 삭제 성공")
                    .build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResponseDto.builder()
                    .message("채팅방 삭제 실패")
                    .build(), HttpStatus.NOT_FOUND);
        }
    }
}

