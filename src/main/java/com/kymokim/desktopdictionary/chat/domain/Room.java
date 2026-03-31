package com.kymokim.desktopdictionary.chat.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Room {

    private String uuid;
    private String name;

    private List<String> sessions;
    public Room(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.sessions = new ArrayList<>();
    }
    public void registerSession(String session) {
        this.sessions.add(session);
    }
    public void unregisterSession(String session) {
        this.sessions.remove(session);
    }

}
