package com.sohil.chatmate.service;

import com.sohil.chatmate.dto.OnlineStatusDTO;
import com.sohil.chatmate.dto.request.UpdateOnlineStatusRequestDTO;
import com.sohil.chatmate.entity.OnlineStatus;

public interface OnlineStatusService {
    void createOnlineStatus(String userId, OnlineStatus.StatusType statusType);

    void updateOnlineStatus(String userId, OnlineStatus.StatusType statusType);

    void updateOnlineStatus( UpdateOnlineStatusRequestDTO updateOnlineStatusRequestDTO);

    OnlineStatusDTO getOnlineStatus(String userId);
}
