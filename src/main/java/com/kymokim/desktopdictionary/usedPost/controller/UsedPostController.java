package com.kymokim.desktopdictionary.usedPost.controller;

import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.common.dto.ResponseDto;
import com.kymokim.desktopdictionary.usedPost.dto.RequestUsedPost;
import com.kymokim.desktopdictionary.usedPost.dto.ResponseUsedPost;
import com.kymokim.desktopdictionary.usedPost.service.UsedPostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usedPost")
@RequiredArgsConstructor
public class UsedPostController {

    private final UsedPostService usedPostService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

//    @PostMapping("/write")
//    public ResponseEntity<ResponseDto> writeUsedPost(@RequestBody RequestUsedPost.WriteUsedPostDto requestDto, HttpServletRequest request) {
//        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
//        usedPostService.writeUsedPost(requestDto, token);
//        ResponseDto responseDto = ResponseDto.builder()
//                .message("UsedPost written successfully.")
//                .build();
//        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
//    }

    @PostMapping("/write")
    public ResponseEntity<ResponseDto> writeUsedPost(@RequestPart(value = "files", required = false) MultipartFile[] files,
                                                    @RequestPart(value = "writeUsedPostDto") RequestUsedPost.WriteUsedPostDto dto,
                                                    HttpServletRequest request) throws IOException {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        usedPostService.writeUsedPost(files, dto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedPost written successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<ResponseDto> searchUsedPost(@PathVariable("keyword") String keyword,
            @PageableDefault(size = 8, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResponseUsedPost.GetAllUsedPostDto> response = usedPostService.searchUsedPost(keyword, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedPost search list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @GetMapping("/get")
    public ResponseEntity<ResponseDto> getAllUsedPost(@PageableDefault(size = 8, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResponseUsedPost.GetAllUsedPostDto> response = usedPostService.getAllUsedPost(pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedPost list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get/{usedPostId}")
    public ResponseEntity<ResponseDto> getUsedPost(@PathVariable("usedPostId") Long usedPostId, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        ResponseUsedPost.GetUsedPostDto response;
        if (token.isPresent()){
            response = usedPostService.getUsedPost(usedPostId, token);
        }
        else {
            response = usedPostService.getUsedPostNonLogIn(usedPostId);
        }
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedPost retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @GetMapping("/getByCategory/{category}")
    public ResponseEntity<ResponseDto> getByCategory(@PathVariable("category") String category,
                                                     @PageableDefault(size = 8, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResponseUsedPost.GetAllUsedPostDto> response = usedPostService.getUsedPostByCategory(category, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedPost list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateUsedPost(@RequestBody RequestUsedPost.UpdateUsedPostDto updateUsedPostDto, HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        usedPostService.updateUsedPost(updateUsedPostDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedPost updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/getReportedUsedPosts")
    public ResponseEntity<ResponseDto> getReportedUsedPosts(HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        List<ResponseUsedPost.GetAllUsedPostDto> response = usedPostService.getReportedUsedPosts(token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedPost retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/report/{usedPostId}")
    public ResponseEntity<ResponseDto> reportUsedPost(@PathVariable("usedPostId") Long usedPostId){
        usedPostService.reportUsedPost(usedPostId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedPost reported successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/unReport/{usedPostId}")
    public ResponseEntity<ResponseDto> unReportUsedPost(@PathVariable("usedPostId") Long usedPostId, HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        usedPostService.unReportUsedPost(usedPostId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedPost unreported successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/sold/{usedPostId}")
    public ResponseEntity<ResponseDto> soldUsedPost(@PathVariable("usedPostId") Long usedPostId, HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        usedPostService.soldUsedPost(usedPostId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedPost set to sold successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/unSold/{usedPostId}")
    public ResponseEntity<ResponseDto> unSoldUsedPost(@PathVariable("usedPostId") Long usedPostId, HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        usedPostService.unSoldUsedPost(usedPostId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedPost set to unsold successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{usedPostId}")
    public ResponseEntity<ResponseDto> deleteUsedPost(@PathVariable("usedPostId") Long usedPostId, HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        usedPostService.deleteUsedPost(usedPostId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedPost deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


}
