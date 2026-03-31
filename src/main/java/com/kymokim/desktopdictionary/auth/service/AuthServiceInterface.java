package com.kymokim.desktopdictionary.auth.service;

import com.kymokim.desktopdictionary.auth.dto.RequestAuth;
import com.kymokim.desktopdictionary.auth.dto.ResponseAuth;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface AuthServiceInterface {
    void registerUser(RequestAuth.RegisterUserDto registerUserDto);

    @Transactional
    void registerAdmin(RequestAuth.RegisterUserDto registerUserDto, Optional<String> token);

    Optional<ResponseAuth.LoginUserRsDto> loginUser(RequestAuth.LoginUserRqDto loginUserDto);

//    String uploadImg(MultipartFile file, Optional<String> token);

    String createAccessToken(String userid);

    ResponseAuth.GetTradeAndBankInfoDto getTradeAndBankInfo(Optional<String> token);

    void updateUser(Optional<String> token, RequestAuth.UpdateUserDto updateUserDto);

    void addTradeInfo(Optional<String> token, RequestAuth.AddTradeInfoDto addTradeInfoDto);

    void addBankInfo(Optional<String> token, RequestAuth.AddBankInfoDto addBankInfoDto);

    ResponseAuth.GetUserDto getUser(Optional<String> token);

    //String createRefreshToken(String userid);
    //Optional<ResponseAuth.Token> updateAccessToken(String token);
}
