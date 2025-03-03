package com.sohil.chatmate.repository;

import com.sohil.chatmate.entity.OnlineStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnlineStatusRepository extends JpaRepository<OnlineStatus, String> {

}
