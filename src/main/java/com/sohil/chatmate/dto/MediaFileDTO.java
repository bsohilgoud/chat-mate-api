package com.sohil.chatmate.dto;

import com.sohil.chatmate.entity.Media;
import jakarta.persistence.Column;

public record MediaFileDTO( Long id, String url, String name, Long size, String type) {
    public MediaFileDTO(Media media) {
        this(
                media.getId(),
                media.getUrl(),
                media.getName(),
                media.getSize(),
                media.getType()
        );
    }
}
