package com.example.notificationservice.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelInfo {
    private String channel;
    private String sender;
    private String body;
    private String overridingBody;
    private Map<String, String> properties;
}