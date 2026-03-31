package com.kymokim.desktopdictionary.auth.dto;

import com.kymokim.desktopdictionary.auth.entity.Auth;
import com.kymokim.desktopdictionary.auth.security.role.Role;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class RequestAuth {
    @Builder
    @Data
    public static class RegisterUserDto{
        private String email;
        private String password;
        private String name;

        public static Auth toEntity(RegisterUserDto registerUserDto, String salt, String encryptedPassword, Role role){
            return Auth.builder()
                    .email(registerUserDto.getEmail())
                    .password(encryptedPassword)
                    .name(registerUserDto.getName())
                    .salt(salt)
                    .role(role)
                    .build();
        }
    }

    @Builder
    @Data
    public static class LoginUserRqDto{
        private String email;
        private String password;
    }

    @Builder
    @Data
    public static class UpdateUserDto{
        private String password;
        private String name;

        public static Auth toEntity(Auth user, UpdateUserDto updateUserDto, String salt, String encryptedPassword){
            user.update(encryptedPassword, updateUserDto.getName(), salt);
            return user;
        }
    }

    @Builder
    @Data
    public static class AddTradeInfoDto{
        private String tel;
        private String addr;
        private String postcode;
    }

    @Builder
    @Data
    public static class AddBankInfoDto{
        private String bankAccount;
        private String bankName;
    }

    @Builder
    @Data
    public static class SendEmailDto{
        @Email
        @NotEmpty(message = "Enter email.")
        private String email;
        private String temp;
    }

    @Builder
    @Data
    public static class VerifyEmailDto{
        @Email
        @NotEmpty(message = "Enter email.")
        private String email;
        private String verificationCode;
    }
}
