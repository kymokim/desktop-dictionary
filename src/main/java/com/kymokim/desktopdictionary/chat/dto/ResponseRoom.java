package com.kymokim.desktopdictionary.chat.dto;

import lombok.Builder;
import lombok.Data;

public class ResponseRoom {

    @Builder
    @Data
    public static class create{
        private String uuid;
        private String name;
    }

    @Builder
    @Data
    public static class delete{
        private String message;
    }

    @Builder
    @Data
    public static class info{
    }
}
