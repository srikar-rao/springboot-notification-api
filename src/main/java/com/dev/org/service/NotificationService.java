package com.dev.org.service;

import com.dev.org.entity.NotificationEntity;
import com.dev.org.model.NotificationResponse;
import com.dev.org.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponse> findAllNotifications() {
        return notificationRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    private NotificationResponse mapToResponse(NotificationEntity entity) {
        NotificationResponse response = new NotificationResponse();
        response.setId(entity.getId());
        response.setMessage(entity.getMessage());
        response.setReadStatus(entity.getReadStatus());
        return response;
    }
}
