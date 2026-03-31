package com.kymokim.desktopdictionary.auth.controller;

import com.kymokim.desktopdictionary.auth.dto.RequestAuth;
import com.kymokim.desktopdictionary.auth.dto.ResponseAuth;
import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.auth.service.AuthService;
import com.kymokim.desktopdictionary.auth.service.EmailService;
import com.kymokim.desktopdictionary.common.dto.ResponseDto;
import com.kymokim.desktopdictionary.common.exception.error.LoginFailedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> registerUser(@Valid @RequestBody RequestAuth.RegisterUserDto registerUserDto) {
        authService.registerUser(registerUserDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("User registered successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/registerAdmin")
    public ResponseEntity<ResponseDto> registerAdmin(@RequestBody RequestAuth.RegisterUserDto registerUserDto, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        authService.registerAdmin(registerUserDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("User registered successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> loginUser(@Valid @RequestBody RequestAuth.LoginUserRqDto loginUserRqDto) {
        ResponseAuth.LoginUserRsDto response = authService.loginUser(loginUserRqDto).orElseThrow(() -> new LoginFailedException());
        ResponseDto responseDto = ResponseDto.builder()
                .message("User logged in successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

//    @PostMapping("/uploadImg")
//    public ResponseEntity<ResponseDto> uploadUserImg(@RequestPart(value = "file", required = false) MultipartFile file, HttpServletRequest request){
//        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
//        String url = authService.uploadImg(file, token);
//        ResponseDto responseDto = ResponseDto.builder()
//                .message("Image uploaded successfully.")
//                .data(url)
//                .build();
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateUser(HttpServletRequest request, @Valid @RequestBody RequestAuth.UpdateUserDto updateUserDto) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        authService.updateUser(token, updateUserDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("User information updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/addTradeInfo")
    public ResponseEntity<ResponseDto> addTradeInfo(HttpServletRequest request, @Valid @RequestBody RequestAuth.AddTradeInfoDto addTradeInfoDto){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        authService.addTradeInfo(token, addTradeInfoDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Trade information added successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/addBankInfo")
    public ResponseEntity<ResponseDto> addBankInfo(HttpServletRequest request, @Valid @RequestBody RequestAuth.AddBankInfoDto addBankInfoDto){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        authService.addBankInfo(token, addBankInfoDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Bank information added successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/getTradeAndBankInfo")
    public ResponseEntity<ResponseDto> getTradeAndBankInfo(HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        ResponseAuth.GetTradeAndBankInfoDto dto = authService.getTradeAndBankInfo(token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Info retrieved successfully.")
                .data(dto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseDto> getUser(HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        ResponseAuth.GetUserDto response = authService.getUser(token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("User information retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @PostMapping ("/sendEmail")
    public String sendEmail(@RequestBody @Valid RequestAuth.SendEmailDto sendEmailDto){
        System.out.println("이메일 인증 이메일 :"+sendEmailDto.getEmail());
        return emailService.writeEmail(sendEmailDto.getEmail());
    }

    @PostMapping("/verifyEmail")
    public ResponseEntity<ResponseDto> verifyEmail(@RequestBody @Valid RequestAuth.VerifyEmailDto verifyEmailDto){
        Boolean Checked=emailService.verifyEmail(verifyEmailDto.getEmail(),verifyEmailDto.getVerificationCode());
        if(Checked){
            ResponseDto responseDto = ResponseDto.builder()
                    .message("Email verified successfully.")
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        }
        else{
            throw new NullPointerException("Verification failed.");
        }
    }
}