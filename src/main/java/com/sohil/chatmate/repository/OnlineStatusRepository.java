package com.sohil.chatmate.repository;

import com.sohil.chatmate.entity.OnlineStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OnlineStatusRepository extends JpaRepository<OnlineStatus, String> {

    @Transactional
    @Modifying
    // TIP: If you do not use the @Modifying annotation for a query method in Spring Data JPA that performs a modifying operation (such as UPDATE, DELETE, or INSERT), the following issues can occur:
    @Query(value = "UPDATE OnlineStatus os SET os.status= :status, os.lastSeen = :lastSeen where os.user.userId= :userId")
    void updateStatus(@Param("userId") String userId, @Param("status") OnlineStatus.StatusType status, @Param("lastSeen") LocalDateTime lastSeen);

}
