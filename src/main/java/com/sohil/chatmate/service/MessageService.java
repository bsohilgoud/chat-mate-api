package com.sohil.chatmate.service;

import com.sohil.chatmate.dto.BulkStatusUpdateRequestDTO;
import com.sohil.chatmate.dto.MediaFileDTO;
import com.sohil.chatmate.dto.MessageWithMediaFileDTO;
import com.sohil.chatmate.dto.UserMessageDTO;
import com.sohil.chatmate.entity.Media;
import com.sohil.chatmate.entity.Message;
import com.sohil.chatmate.entity.User;
import com.sohil.chatmate.enums.ContentType;
import com.sohil.chatmate.enums.MessageStatus;
import com.sohil.chatmate.exceptions.UserNotFoundException;
import com.sohil.chatmate.helper.ChatMateHelper;
import com.sohil.chatmate.helper.WSMessagesHelper;
import com.sohil.chatmate.projection.LastConversation;
import com.sohil.chatmate.repository.MediaRepository;
import com.sohil.chatmate.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageService {

    MessageRepository messageRepository;
    WSMessagesHelper wsMessagesHelper;
    private static final String MEDIA_FILE_UPLOAD_DIR = "uploads/";

    @Autowired
    MediaRepository mediaRepository;


    public MessageService(MessageRepository messageRepository, WSMessagesHelper wsMessagesHelper) {
        this.messageRepository = messageRepository;
        this.wsMessagesHelper = wsMessagesHelper;
    }

    private Message saveMessage(UserMessageDTO userMessageDTO) {
        Message message = Message.builder()
                .senderId(userMessageDTO.senderId())
                .receiverId(userMessageDTO.receiverId())
                .content(userMessageDTO.content())
                .contentType(userMessageDTO.type())
                .status(MessageStatus.PENDING)
                .timestamp(userMessageDTO.timestamp())
                .media(null)
                .build();

        System.out.println("message = " + message);
        return messageRepository.save(message);
    }

    private Message saveMessageWithMedia(MessageWithMediaFileDTO messageWithMediaFileDTO, Media media) {
        Message message = Message.builder()
                .senderId(messageWithMediaFileDTO.senderId())
                .receiverId(messageWithMediaFileDTO.receiverId())
                .content(null)
                .contentType(messageWithMediaFileDTO.type())
                .status(MessageStatus.PENDING)
                .timestamp(messageWithMediaFileDTO.timestamp())
                .media(media)
                .build();

        System.out.println("message = " + message);
        return messageRepository.save(message);
    }

    public void updateMessageStatus(Long id, MessageStatus messageStatus) {
        messageRepository.updateMessageStatus(id, messageStatus);
    }

    public void bulkMessageStatusUpdate(BulkStatusUpdateRequestDTO bulkStatusUpdateRequestDTO) throws Exception {
        MessageStatus fromStatus = bulkStatusUpdateRequestDTO.fromStatus();
        MessageStatus toStatus = bulkStatusUpdateRequestDTO.toStatus();
        String partnerId = bulkStatusUpdateRequestDTO.partnerId();
        User loggedInUser = ChatMateHelper.getLoggedInUser();
        if (loggedInUser != null) {
            messageRepository.bulkStatusUpdate(loggedInUser.getUserID(), partnerId, fromStatus, toStatus);
        } else {
            throw new Exception("Invalid scenario, didn't find logged-in user");
        }
    }

    public List<UserMessageDTO> getChatMessages(String receiverId) throws Exception {
        User loggedInUser = ChatMateHelper.getLoggedInUser();
        if (loggedInUser != null) {
            List<Message> chatMessages = messageRepository.findChatMessages(loggedInUser.getUserID(), receiverId);

            return chatMessages.stream()
                    .map(message -> new UserMessageDTO(
                            message.getId(),
                            message.getSenderId(),
                            message.getReceiverId(),
                            message.getContent(),
                            message.getContentType(),
                            message.getMedia() != null ? new MediaFileDTO(message.getMedia()): null,
                            message.getStatus(),
                            message.getTimestamp()))
                    .collect(Collectors.toList());
        } else {
            throw new Exception("Invalid scenario, didn't find logged-in user");
        }
    }

    public List<UserMessageDTO> getUnreadMessage() throws Exception {
        User loggedInUser = ChatMateHelper.getLoggedInUser();
        if (loggedInUser != null) {
            List<Message> unreadMessages = messageRepository.getUnreadMessages(loggedInUser.getUserID());
            return unreadMessages.stream()
                    .map(message -> new UserMessageDTO(
                            message.getId(),
                            message.getSenderId(),
                            message.getReceiverId(),
                            message.getContent(),
                            message.getContentType(),
                            message.getMedia() != null ? new MediaFileDTO(message.getMedia()): null,
                            message.getStatus(),
                            message.getTimestamp()))
                    .collect(Collectors.toList());
        } else {
            throw new Exception("Invalid scenario, didn't find logged-in user");
        }
    }

    public List<LastConversation> getLastMessages() throws Exception {
        User loggedInUser = ChatMateHelper.getLoggedInUser();
        if (loggedInUser != null) {
            return messageRepository.getLastConversations(loggedInUser.getUserID());
        } else {
            throw new Exception("Invalid scenario, didn't find logged-in user");
        }
    }

    public UserMessageDTO newMessage(UserMessageDTO newMessage) {
        // Adding new message to DB
        Message message = saveMessage(newMessage);
        UserMessageDTO savedMessageDTO = new UserMessageDTO(
                message.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getContentType(),
                null,
                message.getStatus(),
                message.getTimestamp());

        wsMessagesHelper.sendNewPrivateMessage(savedMessageDTO);

        return savedMessageDTO;
    }

    private Media saveMediaFile(MultipartFile file, ContentType contentType) throws IOException {

        File uploadsFolder = new File(MEDIA_FILE_UPLOAD_DIR);
        if (!uploadsFolder.exists()) {
            uploadsFolder.mkdirs();
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File uploadPath = new File(uploadsFolder, filename);

        file.transferTo(uploadPath);

        Media media = Media.builder()
                .url(MEDIA_FILE_UPLOAD_DIR + filename)
                .size(file.getSize())
                .name(filename)
                .type(contentType.toString())
                .build();

        Media savedMedia = mediaRepository.save(media);

        return savedMedia;
    }

    public UserMessageDTO newMessageWithMediaFile(MessageWithMediaFileDTO messageWithMediaFileDTO) throws IOException {
        Media media = saveMediaFile(messageWithMediaFileDTO.file(), messageWithMediaFileDTO.type());
        Message message = saveMessageWithMedia(messageWithMediaFileDTO, media);

        MediaFileDTO mediaFileDTO = new MediaFileDTO(
                media.getId(),
                media.getUrl(),
                media.getName(),
                media.getSize(),
                media.getType()
        );

        UserMessageDTO savedMessageDTO = new UserMessageDTO(
                message.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getContentType(),
                mediaFileDTO,
                message.getStatus(),
                message.getTimestamp());

        wsMessagesHelper.sendNewPrivateMessage(savedMessageDTO);

        return savedMessageDTO;

    }

    public byte[] getMediaFile(String fileUrl) throws IOException {
        File file = new File(MEDIA_FILE_UPLOAD_DIR + fileUrl);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + fileUrl);
        }

        return Files.readAllBytes(file.toPath());
    }
}
