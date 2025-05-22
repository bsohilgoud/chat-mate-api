package com.sohil.chatmate.service;

import com.sohil.chatmate.dto.OnlineStatusDTO;
import com.sohil.chatmate.dto.UpdateOnlineStatusRequestDTO;
import com.sohil.chatmate.entity.OnlineStatus;
import org.springframework.web.bind.annotation.RequestBody;

public interface OnlineStatusService {
    void createOnlineStatus(String userId, OnlineStatus.StatusType statusType);

    void updateOnlineStatus(String userId, OnlineStatus.StatusType statusType);

    void updateOnlineStatus( UpdateOnlineStatusRequestDTO updateOnlineStatusRequestDTO);

    OnlineStatusDTO getOnlineStatus(String userId);
}
