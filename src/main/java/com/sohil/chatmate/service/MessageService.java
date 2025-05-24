package com.sohil.chatmate.service;

import com.sohil.chatmate.dto.*;
import com.sohil.chatmate.entity.Media;
import com.sohil.chatmate.entity.Message;
import com.sohil.chatmate.enums.ContentType;
import com.sohil.chatmate.enums.MessageStatus;
import com.sohil.chatmate.enums.NotificationType;
import com.sohil.chatmate.helper.ChatMateHelper;
import com.sohil.chatmate.helper.NotificationService;
import com.sohil.chatmate.mapper.MessageMapper;
import com.sohil.chatmate.projection.ConversationSummary;
import com.sohil.chatmate.repository.MediaRepository;
import com.sohil.chatmate.repository.MessageRepository;
import com.sohil.chatmate.security.UserPrinciple;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {

    MessageRepository messageRepository;
    NotificationService notificationService;
    private static final String MEDIA_FILE_UPLOAD_DIR = "uploads/";

    @Autowired
    MediaRepository mediaRepository;


    public MessageService(MessageRepository messageRepository, NotificationService notificationService) {
        this.messageRepository = messageRepository;
        this.notificationService = notificationService;
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

        return messageRepository.save(message);
    }

    public void updateMessageStatus(Long id, MessageStatus messageStatus) {
        messageRepository.updateMessageStatus(id, messageStatus);

        Message message = messageRepository.findById(id).orElseThrow();
        notificationService.notification(NotificationType.MESSAGE_STATUS_UPDATED)
                .toUser(message.getReceiverId())
                .withBody(Map.of("messageId", id, "status", messageStatus))
                .send();

    }

    public void batchMessageStatusUpdate(BatchStatusUpdateRequestDTO bulkStatusUpdateRequestDTO) throws Exception {
        MessageStatus fromStatus = bulkStatusUpdateRequestDTO.fromStatus();
        MessageStatus toStatus = bulkStatusUpdateRequestDTO.toStatus();
        String partnerId = bulkStatusUpdateRequestDTO.partnerId();
        UserPrinciple loggedInUser = ChatMateHelper.getLoggedInUserPrinciple();
        messageRepository.bulkStatusUpdate(loggedInUser.getUserID(), partnerId, fromStatus, toStatus);

        notificationService.notification(NotificationType.BULK_MESSAGE_STATUS_UPDATED)
                .toUser(partnerId)
                .withBody(Map.of("status", toStatus))
                .send();
    }

    public List<UserMessageDTO> getChatMessages(String receiverId) throws Exception {
        UserPrinciple loggedInUser = ChatMateHelper.getLoggedInUserPrinciple();
        List<Message> chatMessages = messageRepository.findChatMessages(loggedInUser.getUserID(), receiverId);

        return chatMessages.stream()
                .map(MessageMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UserMessageDTO> getUnreadMessage() throws Exception {
        UserPrinciple loggedInUser = ChatMateHelper.getLoggedInUserPrinciple();
        List<Message> unreadMessages = messageRepository.getUnreadMessages(loggedInUser.getUserID());
        return unreadMessages.stream()
                .map(MessageMapper::toDto)
                .collect(Collectors.toList());

    }

    public List<ConversationSummary> getConversationSummary() throws Exception {
        UserPrinciple loggedInUser = ChatMateHelper.getLoggedInUserPrinciple();
        return messageRepository.getLastConversations(loggedInUser.getUserID());

    }

    @Transactional
    public UserMessageDTO newMessage(UserMessageDTO newMessage) {
        // Adding new message to DB
        Message message = saveMessage(newMessage);
        message.setStatus(MessageStatus.DELIVERED);
        UserMessageDTO savedMessageDTO = MessageMapper.toDto(message);

        notificationService.notification(NotificationType.NEW_MESSAGE)
                .fromUser(savedMessageDTO.senderId())
                .toUser(savedMessageDTO.receiverId())
                .withBody(savedMessageDTO)
                .send();

        messageRepository.save(message);

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

        return mediaRepository.save(media);
    }

    public UserMessageDTO newMessageWithMediaFile(MessageWithMediaFileDTO messageWithMediaFileDTO) throws IOException {
        Media media = saveMediaFile(messageWithMediaFileDTO.file(), messageWithMediaFileDTO.type());
        Message message = saveMessageWithMedia(messageWithMediaFileDTO, media);

        UserMessageDTO savedMessageDTO = MessageMapper.toDto(message, media);

        notificationService.notification(NotificationType.NEW_MESSAGE)
                .fromUser(savedMessageDTO.senderId())
                .toUser(savedMessageDTO.receiverId())
                .withBody(savedMessageDTO)
                .send();

        return savedMessageDTO;

    }

    public byte[] getMediaFile(String fileUrl) throws IOException {
        File file = new File(MEDIA_FILE_UPLOAD_DIR + fileUrl);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + fileUrl);
        }

        return Files.readAllBytes(file.toPath());
    }

    public void sendUserTypingNotification(String receiverId) {
        UserPrinciple loggedInUser = ChatMateHelper.getLoggedInUserPrinciple();
        notificationService.notification(NotificationType.USER_TYPING)
                .toUser(receiverId)
                .fromUser(loggedInUser.getUserID())
                .send();
    }
}
