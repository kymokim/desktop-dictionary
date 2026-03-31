package com.kymokim.desktopdictionary.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageService {
    @Value("${app.image.upload-url:http://localhost:8081/img/upload}")
    private String uploadUrl;

    @Value("${app.image.display-url:http://localhost:8081/img/display}")
    private String displayUrl;

    public List<String> uploadImage(MultipartFile[] files,
                                         String directory) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("directory", directory);
        for (MultipartFile file : files) {
            byte[] fileBytes = file.getBytes();
            ByteArrayResource resource = new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() throws IllegalStateException {
                    return file.getOriginalFilename();
                }
            };
            body.add("files", resource);
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity, String.class);

        List<String> imageLocations = new ArrayList<>();

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // JSON 문자열을 JsonNode로 파싱
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());

                // imageLocations 값을 추출
                JsonNode imageLocationsNode = jsonResponse.get("imageLocations");
                List<String> locations = objectMapper.convertValue(imageLocationsNode, new TypeReference<List<String>>() {});

                // hasSuccess 값을 추출
                boolean hasSuccess = jsonResponse.get("hasSuccess").asBoolean();

                System.out.println("Image uploaded successfully.");
                System.out.println("Image Locations:");
                for (String location : locations) {
                    location = displayUrl + location;
                    imageLocations.add(location);
                    System.out.println(location);
                }
                System.out.println("Has Success: " + hasSuccess);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Image upload failed.");
        }
        return imageLocations;
    }
}
