package com.sohil.chatmate.service;

import com.sohil.chatmate.dto.UserMessageDTO;
import com.sohil.chatmate.entity.Message;
import com.sohil.chatmate.enums.MessageStatus;
import com.sohil.chatmate.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message saveMessage(UserMessageDTO userMessageDTO) {
        Message message = Message.builder()
                .senderId(userMessageDTO.senderId())
                .receiverId(userMessageDTO.receiverId())
                .content(userMessageDTO.content())
                .contentType(userMessageDTO.type())
                .status(MessageStatus.PENDING)
                .timestamp(userMessageDTO.timestamp())
                .build();

        return messageRepository.save(message);
    }

    public void updateMessageStatus(Long id, MessageStatus messageStatus) {
        messageRepository.updateMessageStatus(id, messageStatus);
    }

//    public void getLatestMessages() {
//        messageRepository.getLatestMessages();
//    }

    public List<UserMessageDTO> getChatMessages(String senderId, String receiverId) {
        List<Message> chatMessages = messageRepository.findChatMessages(senderId, receiverId);
        return chatMessages.stream()
                .map(message -> new UserMessageDTO(
                        message.getSenderId(),
                        message.getReceiverId(),
                        message.getContent(),
                        message.getContentType(),
                        message.getStatus(),
                        message.getTimestamp()))
                .collect(Collectors.toList());
    }
}
