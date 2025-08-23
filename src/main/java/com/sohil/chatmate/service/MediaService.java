package com.sohil.chatmate.service;

import com.sohil.chatmate.entity.Media;
import com.sohil.chatmate.enums.ContentType;
import com.sohil.chatmate.enums.NotificationType;
import com.sohil.chatmate.helper.ChatMateHelper;
import com.sohil.chatmate.repository.MediaRepository;
import com.sohil.chatmate.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Service
public class MediaService {

    private static final String MEDIA_FILE_UPLOAD_DIR = "uploads/";

    MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public Media saveMediaFile(MultipartFile multipartFile, ContentType contentType) throws IOException {
        return  saveMediaFile( multipartFile,  contentType, null);
    }


    public Media saveMediaFile(MultipartFile multipartFile, ContentType contentType, String fileName) throws IOException {

        File uploadsFolder = new File(MEDIA_FILE_UPLOAD_DIR);
        if (!uploadsFolder.exists()) {
            uploadsFolder.mkdirs();
        }

        if(fileName == null)
            fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

        File uploadPath = new File(uploadsFolder, fileName);

        multipartFile.transferTo(uploadPath);

        Media media = Media.builder()
                .url(MEDIA_FILE_UPLOAD_DIR + fileName)
                .size(multipartFile.getSize())
                .name(fileName)
                .type(contentType.toString())
                .build();

        return mediaRepository.save(media);
    }

    public byte[] getMediaFile(String fileName) throws IOException {
        File file = new File(MEDIA_FILE_UPLOAD_DIR + fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + fileName);
        }

        return Files.readAllBytes(file.toPath());
    }

    public byte[] getMediaFromUrl(String fileUrl) throws IOException {
        System.out.println("Media file Url -> " + fileUrl);
        File file = new File(fileUrl);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + fileUrl);
        }

        return Files.readAllBytes(file.toPath());
    }
}
