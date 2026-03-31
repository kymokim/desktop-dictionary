package com.kymokim.desktopdictionary.chat.dto;

import lombok.Builder;
import lombok.Data;

public class RequestChat {

    @Builder
    @Data
    public static class test{
        private String nickname;
    }
    @Builder
    @Data
    public static class MessageDto{
        private String userName;
        private String context;
    }

}
