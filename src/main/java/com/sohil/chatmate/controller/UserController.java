package com.sohil.chatmate.controller;


import com.sohil.chatmate.dto.OnlineStatusDTO;
import com.sohil.chatmate.dto.request.UpdateOnlineStatusRequestDTO;
import com.sohil.chatmate.dto.UserDTO;
import com.sohil.chatmate.dto.response.ApiResponse;
import com.sohil.chatmate.entity.Media;
import com.sohil.chatmate.entity.User;
import com.sohil.chatmate.enums.ContentType;
import com.sohil.chatmate.service.MediaService;
import com.sohil.chatmate.service.OnlineStatusService;
import com.sohil.chatmate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    OnlineStatusService onlineStatusService;

    @Autowired
    MediaService mediaService;

    @GetMapping("/")
    ResponseEntity<ApiResponse<List<UserDTO>>> getUsersList(HttpServletRequest httpServletRequest){
        List<UserDTO> allUsers = userService.findAllUsers();
        return ApiResponse.success(HttpStatus.OK.value(), allUsers, httpServletRequest.getRequestURI());
    }

    @GetMapping("/{userId}")
    ResponseEntity<ApiResponse<UserDTO>> getUsersList(@PathVariable String userId, HttpServletRequest httpServletRequest){
        UserDTO userDTO = userService.findUserById(userId);
        return ApiResponse.success(HttpStatus.OK.value(), userDTO, httpServletRequest.getRequestURI());
    }

    @GetMapping("/me")
    ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(HttpServletRequest request){
        UserDTO currentUser = userService.getCurrentUser();
       return ApiResponse.success(HttpStatus.OK.value(), currentUser, "Current logged in user", request.getRequestURI());
    }

    @GetMapping("/all")
    ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers(HttpServletRequest httpServletRequest){
        List<UserDTO> allUsers = userService.findAllUsers();
        return ApiResponse.success(HttpStatus.OK.value(), allUsers, httpServletRequest.getRequestURI());
    }

    @GetMapping("/status/{userId}")
    ResponseEntity<ApiResponse<OnlineStatusDTO>> getUserStatus(@PathVariable String userId, HttpServletRequest httpServletRequest){
        OnlineStatusDTO onlineStatus = onlineStatusService.getOnlineStatus(userId);
        return ApiResponse.success(HttpStatus.OK.value(), onlineStatus, httpServletRequest.getRequestURI());
    }

    @PatchMapping("/status/")
    //TIP: Don't forgot @RequestBody
    public ResponseEntity<ApiResponse<Object>> updateOnlineStatus(@RequestBody UpdateOnlineStatusRequestDTO updateOnlineStatusRequestDTO, HttpServletRequest httpServletRequest){
        onlineStatusService.updateOnlineStatus(updateOnlineStatusRequestDTO);
        return ApiResponse.success(HttpStatus.OK.value(), null, httpServletRequest.getRequestURI());
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable("userId") String userId) throws IOException {
        User user = userService.getUser(userId);
//        log.info("Fetching user profile for user : " + user.getFullName() + ", profileUrl :" + user.getProfileUrl());
        String profileUrl = user.getProfileUrl();
        if (profileUrl == null || profileUrl.isEmpty()) {
            // Returning 204 with no Content with an empty body
            return ResponseEntity.noContent().build();
        }

        byte[] mediaFile = mediaService.getMediaFromUrl(profileUrl);
        String contentType = Files.probeContentType(Path.of(profileUrl));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + profileUrl + "\"")
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .body(mediaFile);
    }

    @PostMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateProfileImage(@PathVariable("userId") String userId, @RequestParam("file") MultipartFile multipartFile, HttpServletRequest httpServletRequest) throws IOException {

        String originalFilename = multipartFile.getOriginalFilename();

        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmssSSS");
        String newFilename = "profile_" + userId + "_" + LocalDateTime.now().format(formatter) + extension;

        Media media = mediaService.saveMediaFile(multipartFile, ContentType.IMAGE, newFilename);
        userService.updateProfileUrl(userId, media.getUrl());

        return ApiResponse.success(HttpStatus.OK.value(), Map.of("profileUrl", media.getUrl()), httpServletRequest.getRequestURI());
    }

    @DeleteMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUserProfileImage(@PathVariable("userId") String userId, HttpServletRequest httpServletRequest) throws IOException {
        userService.updateProfileUrl(userId, null);

        return ApiResponse.success(HttpStatus.OK.value(), "Successfully removed the profile for user: " + userId, httpServletRequest.getRequestURI());
    }

}
