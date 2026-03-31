package com.kymokim.desktopdictionary.chat.repository;

import com.kymokim.desktopdictionary.chat.domain.Room;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ChatRoomRepository {
    private List<Room> list;

    @PostConstruct
    private void init() {
        this.list = new ArrayList<>();
    }

    @PreDestroy
    private void destroy() {

    }

    public String createRoom(String name) {
        Room newRoom = new Room(UUID.randomUUID().toString(), name);
        this.list.add(newRoom);
        return newRoom.getUuid();
    }
    public void registerSessionToRoom(String uuid, String session) {
        for(int i = 0 ; i < list.size() ; i++) {
            if(list.get(i).getUuid().equals(uuid)) {
                list.get(i).registerSession(session);
                break;
            }
        }
    }
    public void unregisterSessionFromRoom(String uuid, String session){
        for(int i = 0 ; i < list.size() ; i++) {
            if(list.get(i).getUuid().equals(uuid)) {
                list.get(i).unregisterSession(session);
                break;
            }
        }
    }

    public boolean deleteRoom(String roomUUID) {
        return list.removeIf(room -> room.getUuid().equals(roomUUID));
    }

    public List<Room> getAllRoom() {
        return this.list;
    }
}
