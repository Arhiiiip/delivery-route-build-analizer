package itmo.diploma.notification.controller;

import itmo.diploma.notification.dto.NotificationRequest;
import itmo.diploma.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/notify")
    public String sendNotification(@RequestBody NotificationRequest request) {
        notificationService.processAndSendNotification(request.getData(), request.getEmail());
        return "Notification sent successfully";
    }
}