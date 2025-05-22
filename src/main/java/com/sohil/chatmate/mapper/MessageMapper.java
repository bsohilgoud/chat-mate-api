package com.sohil.chatmate.mapper;


import com.sohil.chatmate.dto.MediaFileDTO;
import com.sohil.chatmate.dto.UserMessageDTO;
import com.sohil.chatmate.entity.Media;
import com.sohil.chatmate.entity.Message;

public class MessageMapper {
    public static UserMessageDTO toDto(Message message) {
        return new UserMessageDTO(
                message.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getContentType(),
                message.getMedia() != null ? new MediaFileDTO(message.getMedia()): null,
                message.getStatus(),
                message.getTimestamp());
    }

    public static UserMessageDTO toDto(Message message, MediaFileDTO mediaFileDTO) {
        return new UserMessageDTO(
                message.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getContentType(),
                mediaFileDTO,
                message.getStatus(),
                message.getTimestamp());
    }

    public static UserMessageDTO toDto(Message message, Media media) {
        MediaFileDTO mediaFileDTO = MediaMapper.toDto(media);

        return new UserMessageDTO(
                message.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getContentType(),
                mediaFileDTO,
                message.getStatus(),
                message.getTimestamp());
    }

    public static class MediaMapper {
        public static MediaFileDTO toDto(Media media) {
            return new MediaFileDTO(
                    media.getId(),
                    media.getUrl(),
                    media.getName(),
                    media.getSize(),
                    media.getType()
            );
        }
    }
}
