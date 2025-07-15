package com.example.paymentservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private String eventId;
    private String paymentId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String merchantName;
    private String customerEmail;
    private String customerPhone;
    private LocalDateTime timestamp;
    private String description;
    
    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        CANCELLED,
        REFUNDED
    }
} 