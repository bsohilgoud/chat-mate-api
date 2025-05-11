package com.sohil.chatmate.helper;

import com.sohil.chatmate.dto.NotificationDTO;
import com.sohil.chatmate.enums.NotificationType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    NotificationService(SimpMessagingTemplate simpMessagingTemplate){
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

//    public void sendPublicNotification(NotificationDTO notification){
//        simpMessagingTemplate.convertAndSend("/queue/notification/public", notification);
//    }
//
//    public void sendPrivateNotification(String toUser, NotificationDTO notification){
//        simpMessagingTemplate.convertAndSend("/queue/notification/private" + toUser, notification);
//    }

    public Notification notification(NotificationType type){
        return new Notification(this.simpMessagingTemplate, type);
    }

    public static class Notification {
        private final SimpMessagingTemplate messagingTemplate;

        private String toUser = null;
        private String fromUser;
        private Object body;
        private final NotificationType type;
        private boolean isPublic = false;

        Notification(SimpMessagingTemplate messagingTemplate, NotificationType type){
            this.messagingTemplate = messagingTemplate;
            this.type = type;
        }

        public Notification toUser(String toUser){
            this.toUser = toUser;
            return this;
        }

        public Notification fromUser(String fromUser){
            this.fromUser = fromUser;
            return this;
        }

        public Notification withBody(Object body){
            this.body = body;
            return this;
        }

        public Notification isPublic(boolean isPublic){
            this.isPublic = isPublic;
            return this;
        }

        public void send(){
            NotificationDTO notification = new NotificationDTO(type, fromUser, toUser, body);

            if(toUser != null && !isPublic)
                messagingTemplate.convertAndSend("/queue/notification/private/" + toUser, notification);
            else
                messagingTemplate.convertAndSend("/queue/notification/public", notification);


        }
    }

}
