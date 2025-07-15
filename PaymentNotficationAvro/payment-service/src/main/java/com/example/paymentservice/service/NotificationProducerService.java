package com.example.paymentservice.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.example.paymentservice.dto.PaymentEvent;
import com.example.shared.avro.EmailReceiver;
import com.example.shared.avro.NotificationChannel;
import com.example.shared.avro.NotificationMessage;
import com.example.shared.avro.Receiver;
import com.example.shared.avro.Sender;
import com.example.shared.avro.SmsReceiver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${notification.topic.name}")
    private String notificationTopic;

    public CompletableFuture<SendResult<String, Object>> sendPaymentNotification(PaymentEvent paymentEvent) {
        try {
            NotificationMessage message = buildNotificationMessage(paymentEvent);
            log.info("Notification message: {}", message);
            return kafkaTemplate.send(notificationTopic, paymentEvent.getPaymentId(), message)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            log.error("Failed to send notification for payment: {}", paymentEvent.getPaymentId(), throwable);
                        } else {
                            log.info("Successfully sent notification for payment: {}", paymentEvent.getPaymentId());
                        }
                    });
        } catch (Exception e) {
            log.error("Error creating notification for payment: {}", paymentEvent.getPaymentId(), e);
            throw e;
        }
    }

    public CompletableFuture<SendResult<String, Object>> sendRawNotification(NotificationMessage message) {
        try {
            log.info("Sending raw NotificationMessage: {}", message);
            return kafkaTemplate.send(notificationTopic, message.getEventId(), message)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            log.error("Failed to send raw notification for event: {}", message.getEventId(), throwable);
                        } else {
                            log.info("Successfully sent raw notification for event: {}", message.getEventId());
                        }
                    });
        } catch (Exception e) {
            log.error("Error sending raw notification for event: {}", message.getEventId(), e);
            throw e;
        }
    }

    private NotificationMessage buildNotificationMessage(PaymentEvent payment) {
        return NotificationMessage.newBuilder()
                .setEventId(payment.getEventId())
                .setTimestamp(Instant.now().toString())
                .setEventSource("payment-service")
                .setPriority(getPriority(payment.getStatus()))
                .setCategory("PAYMENT")
                .setScheduledSendTime(null)
                .setClientId(payment.getUserId())
                .setChannels(List.of("EMAIL", "SMS"))
                .setHref("https://payment.example.com/payment/" + payment.getPaymentId())
                .setLanguage("en")
                .setUseCommonContent(false)
                .setContent(buildContent(payment))
                .setMessageType("PAYMENT_" + payment.getStatus())
                .setReceiver(buildReceivers(payment))
                .setSender(Sender.newBuilder().build())
                .setAttachment(new ArrayList<>())
                .setNotificationChannels(buildChannels(payment))
                .setExternalFields(buildExternalFields(payment))
                .build();
    }

    private List<Receiver> buildReceivers(PaymentEvent payment) {
        List<Receiver> receivers = new ArrayList<>();
        
        if (payment.getCustomerEmail() != null && !payment.getCustomerEmail().isEmpty()) {
            receivers.add(Receiver.newBuilder()
                    .setEmailReceiver(EmailReceiver.newBuilder()
                            .setTo(List.of(payment.getCustomerEmail()))
                            .setCc(new ArrayList<>())
                            .setBcc(new ArrayList<>())
                            .build())
                    .build());
        }

        if (payment.getCustomerPhone() != null && !payment.getCustomerPhone().isEmpty()) {
            receivers.add(Receiver.newBuilder()
                    .setSmsReceiver(SmsReceiver.newBuilder()
                            .setPhoneNumber(payment.getCustomerPhone())
                            .build())
                    .build());
        }
        
        return receivers;
    }

    private List<NotificationChannel> buildChannels(PaymentEvent payment) {
        List<NotificationChannel> channels = new ArrayList<>();
        
        if (payment.getCustomerEmail() != null) {
            channels.add(NotificationChannel.newBuilder()
                    .setChannel("EMAIL")
                    .setSender("noreply@payment.com")
                    .setBody(buildEmailBody(payment))
                    .setProperties(Map.of(
                            "subject", buildEmailSubject(payment),
                            "template", "payment-notification"
                    ))
                    .build());
        }

        if (payment.getCustomerPhone() != null) {
            channels.add(NotificationChannel.newBuilder()
                    .setChannel("SMS")
                    .setBody(buildSmsBody(payment))
                    .setProperties(Map.of())
                    .build());
        }
        
        return channels;
    }

    private Map<String, String> buildExternalFields(PaymentEvent payment) {
        return Map.of(
                "paymentId", payment.getPaymentId(),
                "amount", payment.getAmount().toString(),
                "currency", payment.getCurrency(),
                "merchantName", payment.getMerchantName() != null ? payment.getMerchantName() : ""
        );
    }

    private String getPriority(PaymentEvent.PaymentStatus status) {
        return switch (status) {
            case FAILED, CANCELLED -> "HIGH";
            case COMPLETED, REFUNDED -> "MEDIUM";
            case PENDING -> "LOW";
        };
    }

    private String buildEmailSubject(PaymentEvent payment) {
        return "Payment " + payment.getStatus().toString().toLowerCase() + " - " + 
               payment.getAmount() + " " + payment.getCurrency();
    }

    private String buildEmailBody(PaymentEvent payment) {
        String merchant = payment.getMerchantName() != null ? payment.getMerchantName() : "Merchant";
        String baseMessage = String.format("Your payment of %s %s to %s", 
                payment.getAmount(), payment.getCurrency(), merchant);
        
        return switch (payment.getStatus()) {
            case COMPLETED -> baseMessage + " has been successfully processed.";
            case FAILED -> baseMessage + " has failed. Please try again or contact support.";
            case PENDING -> baseMessage + " is being processed. You will receive confirmation shortly.";
            case CANCELLED -> baseMessage + " has been cancelled.";
            case REFUNDED -> baseMessage + " has been refunded. Amount will be credited within 3-5 business days.";
        } + " Payment ID: " + payment.getPaymentId();
    }

    private String buildSmsBody(PaymentEvent payment) {
        return String.format("Payment %s: %s %s. ID: %s", 
                payment.getStatus().toString().toLowerCase(),
                payment.getAmount(), 
                payment.getCurrency(), 
                payment.getPaymentId());
    }

    private String buildContent(PaymentEvent payment) {
        return String.format("Payment %s: %s %s for %s", 
                payment.getStatus().toString().toLowerCase(),
                payment.getAmount(), 
                payment.getCurrency(),
                payment.getMerchantName() != null ? payment.getMerchantName() : "merchant");
    }
} 