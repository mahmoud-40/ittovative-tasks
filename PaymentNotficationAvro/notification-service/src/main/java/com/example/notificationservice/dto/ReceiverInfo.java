package com.example.notificationservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiverInfo {
    private String type;
    private String email;
    private String phoneNumber;
    private String appUserId;
    private List<String> emailTo;
    private List<String> emailCc;
    private List<String> emailBcc;
}