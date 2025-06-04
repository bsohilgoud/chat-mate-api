package com.sohil.chatmate.service.impl;

import com.sohil.chatmate.dto.OnlineStatusDTO;
import com.sohil.chatmate.dto.request.UpdateOnlineStatusRequestDTO;
import com.sohil.chatmate.entity.OnlineStatus;
import com.sohil.chatmate.entity.User;
import com.sohil.chatmate.exceptions.UserNotFoundException;
import com.sohil.chatmate.repository.OnlineStatusRepository;
import com.sohil.chatmate.service.OnlineStatusService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OnlineStatusServiceImpl implements OnlineStatusService {

    OnlineStatusRepository onlineStatusRepository;

    public OnlineStatusServiceImpl(OnlineStatusRepository onlineStatusRepository) {
        this.onlineStatusRepository = onlineStatusRepository;
    }

    @Override
    public void createOnlineStatus(User user, OnlineStatus.StatusType statusType) {
        OnlineStatus userOnlineStatus = OnlineStatus.builder()
                .user(user)
                .status(OnlineStatus.StatusType.ONLINE)
                .lastSeen(LocalDateTime.now())
                .build();
        onlineStatusRepository.save(userOnlineStatus);
    }

    @Override
    @Transactional
    public void updateOnlineStatus(String userId, OnlineStatus.StatusType statusType) {
        onlineStatusRepository.updateStatus(userId, statusType, LocalDateTime.now());
    }

    @Override
    public void updateOnlineStatus(UpdateOnlineStatusRequestDTO updateOnlineStatusRequestDTO) {
        updateOnlineStatus(updateOnlineStatusRequestDTO.userId(), updateOnlineStatusRequestDTO.status());
    }

    // TIP: Use DTO's else we are getting Serialization exception
    // ERROR : The error you're encountering, InvalidDefinitionException: No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor, typically arises when attempting to serialize a Hibernate proxy object that is in a lazy-loaded state. This situation often occurs in Spring applications using JPA with Hibernate when the entity relationships are set to FetchType.LAZY, leading to proxy objects that Jackson cannot serialize directly.
    @Override
    public OnlineStatusDTO getOnlineStatus(String userId) {
        // TIP: When using getReferenceById in JPA, it’s important to understand its behavior. This method returns a reference to the entity with the given identifier, but it does not immediately hit the database to check if the entity exists. Instead, it creates a proxy object that will be initialized when you access any of its properties.
        if(!onlineStatusRepository.existsById(userId)){
            throw new UserNotFoundException("Unable to find user with id: " + userId);
        }

        OnlineStatus onlineStatus = onlineStatusRepository.getReferenceById(userId);
        return new OnlineStatusDTO(onlineStatus.getUser().getUserId(), onlineStatus.getLastSeen(), onlineStatus.getStatus().toString());
    }

}
