package com.kymokim.desktopdictionary.auth.service;

import com.kymokim.desktopdictionary.auth.dto.RequestAuth;
import com.kymokim.desktopdictionary.auth.dto.ResponseAuth;
import com.kymokim.desktopdictionary.auth.entity.Auth;
import com.kymokim.desktopdictionary.auth.repository.AuthRepository;
import com.kymokim.desktopdictionary.auth.security.JwtAuthToken;
import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.auth.security.role.Role;
import com.kymokim.desktopdictionary.auth.util.SHA256Util;
import com.kymokim.desktopdictionary.common.config.AdminConfig;
import com.kymokim.desktopdictionary.common.exception.error.LoginFailedException;
import com.kymokim.desktopdictionary.common.exception.error.NotFoundUserException;
import com.kymokim.desktopdictionary.common.exception.error.RegisterFailedException;
//import com.kymokim.desktopdictionary.common.service.S3Service;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthServiceInterface {

    private final AuthRepository authRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AdminConfig adminConfig;

    private Boolean isAdmin;

//    private final S3Service s3Service;

//    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {

        if (!authRepository.existsByEmail(adminConfig.getAdminEmail())) {
            Auth admin = new Auth();

            String salt = SHA256Util.generateSalt();
            String encryptedPassword = SHA256Util.getEncrypt(adminConfig.getAdminPassword(),salt);

            admin.setEmail(adminConfig.getAdminEmail());
//            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setPassword(encryptedPassword);
            admin.setName("admin");
            admin.setSalt(salt);
            admin.setRole(Role.ADMIN);
            authRepository.save(admin);
        }
    }

    @Transactional
    @Override
    public void registerUser(RequestAuth.RegisterUserDto registerUserDto) {

        Auth user = authRepository.findByEmail(registerUserDto.getEmail());
        if(user != null){
            throw new RegisterFailedException();
        }

        String salt = SHA256Util.generateSalt();
        String encryptedPassword = SHA256Util.getEncrypt(registerUserDto.getPassword(),salt);
        user = RequestAuth.RegisterUserDto.toEntity(registerUserDto, salt, encryptedPassword, Role.USER);
        authRepository.save(user);
    }

    @Transactional
    @Override
    public void registerAdmin(RequestAuth.RegisterUserDto registerUserDto, Optional<String> token){

        String tokenEmail = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            tokenEmail = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByEmail(tokenEmail);
        if (admin.getRole() != Role.ADMIN)
            throw new RuntimeException("No permission.");

        Auth user = authRepository.findByEmail(registerUserDto.getEmail());
        if (user != null) {
            throw new RegisterFailedException();
        }

        String salt = SHA256Util.generateSalt();
        String encryptedPassword = SHA256Util.getEncrypt(registerUserDto.getPassword(), salt);
        user = RequestAuth.RegisterUserDto.toEntity(registerUserDto, salt, encryptedPassword, Role.ADMIN);
        authRepository.save(user);
    }

    @Override
    @Transactional
    public Optional<ResponseAuth.LoginUserRsDto> loginUser(RequestAuth.LoginUserRqDto loginUserRqDto) {
        Auth user = authRepository.findByEmail(loginUserRqDto.getEmail());
        if(user == null)
            throw new LoginFailedException();

        String salt = user.getSalt();
        user = authRepository.findByEmailAndPassword(loginUserRqDto.getEmail(), SHA256Util.getEncrypt(loginUserRqDto.getPassword(),salt));
        if(user == null)
            throw new LoginFailedException();

        String accessToken = createAccessToken(user.getEmail());
        return Optional.ofNullable(ResponseAuth.LoginUserRsDto.toDto(accessToken, isAdmin));
    }

//    @Override
//    @Transactional
//    public String uploadImg(MultipartFile file, Optional<String> token) {
//        String email = null;
//        if(token.isPresent()){
//            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
//            email = jwtAuthToken.getClaims().getSubject();
//        }
//        Auth user = authRepository.findByEmail(email);
//        String url = "";
//        try {
//            url = s3Service.upload(file,"user");
//        }
//        catch (IOException e){
//            System.out.println("S3 upload failed.");
//        }
//
//        user.setUserImg(url);
//        authRepository.save(user);
//        return url;
//    }

    @Override
    public String createAccessToken(String userEmail) {
        Date expiredDate = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());

        Auth user = authRepository.findByEmail(userEmail);
        String roleCode;
        if (user.getRole() == Role.ADMIN) {
            roleCode = Role.ADMIN.getCode();
            isAdmin = true;
        } else if (user.getRole() == Role.USER) {
            roleCode = Role.USER.getCode();
            isAdmin = false;
        } else {
            roleCode = Role.UNKNOWN.getCode();
            isAdmin = false;
        }

        JwtAuthToken accessToken = jwtAuthTokenProvider.createAuthToken(userEmail, roleCode, expiredDate);
        return accessToken.getToken();
    }

    @Override
    @Transactional
    public void updateUser(Optional<String> token, RequestAuth.UpdateUserDto updateUserDto) {

        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }

        Auth originalUser = authRepository.findByEmail(email);
        if(originalUser == null)
            throw new NotFoundUserException();

        String salt = SHA256Util.generateSalt();
        String encryptedPassword = SHA256Util.getEncrypt(updateUserDto.getPassword(), salt);
        Auth updatedUser = RequestAuth.UpdateUserDto.toEntity(originalUser, updateUserDto, salt, encryptedPassword);
        authRepository.save(updatedUser);
    }

    @Override
    public void addTradeInfo(Optional<String> token, RequestAuth.AddTradeInfoDto addTradeInfoDto){
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }

        Auth user = authRepository.findByEmail(email);
        if(user == null)
            throw new NotFoundUserException();

        user.addTradeInfo(addTradeInfoDto.getTel(), addTradeInfoDto.getAddr(), addTradeInfoDto.getPostcode());
        authRepository.save(user);
    }

    @Override
    public void addBankInfo(Optional<String> token, RequestAuth.AddBankInfoDto addBankInfoDto){
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }

        Auth user = authRepository.findByEmail(email);
        if(user == null)
            throw new NotFoundUserException();

        user.addBankInfo(addBankInfoDto.getBankAccount(), addBankInfoDto.getBankName());
        authRepository.save(user);
    }

    @Override
    @Transactional
    public ResponseAuth.GetTradeAndBankInfoDto getTradeAndBankInfo(Optional<String> token){
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }

        Auth user = authRepository.findByEmail(email);
        if(user == null)
            throw new NotFoundUserException();

        return ResponseAuth.GetTradeAndBankInfoDto.toDto(user);
    }

    @Override
    @Transactional
    public ResponseAuth.GetUserDto getUser(Optional<String> token) {

        System.out.println(token);

        String email = null;
        if(token.isPresent()){
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByEmail(email);
        if (user == null)
            throw new NotFoundUserException();

        return ResponseAuth.GetUserDto.toDto(user);
    }
}