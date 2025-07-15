package com.example.notificationservice.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String eventId;
    private String timestamp;
    private String eventSource;
    private String priority;
    private String category;
    private String scheduledSendTime;
    private String clientId;
    private List<String> channels;
    private String href;
    private String language;
    private Boolean useCommonContent;
    private String content;
    private String messageType;
    
    private Map<String, String> externalFields;
    
    private List<ReceiverInfo> receivers;
    private List<ChannelInfo> notificationChannels;
} 