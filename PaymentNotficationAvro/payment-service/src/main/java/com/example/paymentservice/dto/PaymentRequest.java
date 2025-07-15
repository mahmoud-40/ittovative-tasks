package com.example.paymentservice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String eventId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String merchantName;
    private String customerEmail;
    private String customerPhone;
    private String description;
}