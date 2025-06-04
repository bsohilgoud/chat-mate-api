package com.sohil.chatmate.service;

import com.sohil.chatmate.dto.OnlineStatusDTO;
import com.sohil.chatmate.dto.request.UpdateOnlineStatusRequestDTO;
import com.sohil.chatmate.entity.OnlineStatus;
import com.sohil.chatmate.entity.User;

public interface OnlineStatusService {
    void createOnlineStatus(User user, OnlineStatus.StatusType statusType);

    void updateOnlineStatus(String userId, OnlineStatus.StatusType statusType);

    void updateOnlineStatus( UpdateOnlineStatusRequestDTO updateOnlineStatusRequestDTO);

    OnlineStatusDTO getOnlineStatus(String userId);
}
