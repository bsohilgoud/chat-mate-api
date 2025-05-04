package com.sohil.chatmate.service;

import com.sohil.chatmate.dto.BulkStatusUpdateRequestDTO;
import com.sohil.chatmate.dto.UserMessageDTO;
import com.sohil.chatmate.entity.Message;
import com.sohil.chatmate.entity.User;
import com.sohil.chatmate.enums.MessageStatus;
import com.sohil.chatmate.exceptions.UserNotFoundException;
import com.sohil.chatmate.helper.ChatMateHelper;
import com.sohil.chatmate.helper.WSMessagesHelper;
import com.sohil.chatmate.projection.LastConversation;
import com.sohil.chatmate.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    MessageRepository messageRepository;
    WSMessagesHelper wsMessagesHelper;


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
                message.getStatus(),
                message.getTimestamp());

        wsMessagesHelper.sendNewPrivateMessage(savedMessageDTO);

        return savedMessageDTO;
    }
}
