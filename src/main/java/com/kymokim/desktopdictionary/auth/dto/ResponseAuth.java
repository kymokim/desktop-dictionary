package com.kymokim.desktopdictionary.auth.dto;

import com.kymokim.desktopdictionary.auth.entity.Auth;
import lombok.Builder;
import lombok.Data;

public class ResponseAuth {
    @Data
    @Builder
    public static class LoginUserRsDto{
        private String accessToken;
        private Boolean isAdmin;

        public static LoginUserRsDto toDto(String accessToken, Boolean isAdmin){
            return LoginUserRsDto.builder()
                    .accessToken(accessToken)
                    .isAdmin(isAdmin)
                    .build();
        }
    }

    @Data
    @Builder
    public static class GetUserDto{
        private String email;
        private String name;

        public static GetUserDto toDto(Auth user){
            return GetUserDto.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .build();
        }
    }

    @Data
    @Builder
    public static class GetTradeAndBankInfoDto{
        private String tel;
        private String addr;
        private String postcode;
        private String bankAccount;
        private String bankName;

        public static GetTradeAndBankInfoDto toDto(Auth user){
            return GetTradeAndBankInfoDto.builder()
                    .tel(user.getTel())
                    .addr(user.getAddr())
                    .postcode(user.getPostcode())
                    .bankAccount(user.getBankAccount())
                    .bankName(user.getBankName())
                    .build();
        }
    }
}
