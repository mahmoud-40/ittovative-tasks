package com.example.paymentservice.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.paymentservice.dto.PaymentEvent;
import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.service.NotificationProducerService;
import com.example.shared.avro.EmailReceiver;
import com.example.shared.avro.NotificationMessage;
import com.example.shared.avro.Receiver;
import com.example.shared.avro.SmsReceiver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final NotificationProducerService notificationProducerService;

    @PostMapping("/process")
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest request) {
        try {
            PaymentEvent paymentEvent = PaymentEvent.builder()
                    .eventId(request.getEventId())
                    .paymentId(UUID.randomUUID().toString())
                    .userId(request.getUserId())
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .status(PaymentEvent.PaymentStatus.COMPLETED)
                    .merchantName(request.getMerchantName())
                    .customerEmail(request.getCustomerEmail())
                    .customerPhone(request.getCustomerPhone())
                    .timestamp(LocalDateTime.now())
                    .description(request.getDescription())
                    .build();

            log.info("Processing payment: {}", paymentEvent.getPaymentId());
            
            notificationProducerService.sendPaymentNotification(paymentEvent);
            
            return ResponseEntity.ok("Payment processed successfully. ID: " + paymentEvent.getPaymentId());
        } catch (Exception e) {
            log.error("Error processing payment", e);
            return ResponseEntity.internalServerError().body("Payment processing failed: " + e.getMessage());
        }
    }

    @PostMapping("/simulate/sample")
    public ResponseEntity<String> simulateSampleNotification() {
        try {
            NotificationMessage notificationMessage = NotificationMessage.newBuilder()
                .setEventId("550e8400-e29b-41d4-a716-446655440002")
                .setTimestamp("2025-06-22T10:00:002")
                .setEventSource("user-service")
                .setPriority("HIGH")
                .setCategory("authentication")
                .setScheduledSendTime(null)
                .setClientId(null)
                .setChannels(java.util.List.of("EMAIL", "SMS", "WHATSAPP", "PUSH_NOTIFICATION"))
                .setHref("https://example.com/tmf")
                .setLanguage("ENGLISH")
                .setUseCommonContent(true)
                .setContent("Common body. If not overridden in notificationChannel, it would be effective")
                .setMessageType("")
                .setReceiver(java.util.List.of(
                    Receiver.newBuilder()
                        .setEmailReceiver(EmailReceiver.newBuilder()
                            .setTo(java.util.List.of("mostafadfrg@gmail.com"))
                            .setCc(java.util.List.of("mostafa.hussien@ittovative.com", "mostafahass314@gmail.com"))
                            .setBcc(java.util.List.of("jobs@ittovative.com"))
                            .build())
                        .setSmsReceiver(SmsReceiver.newBuilder().setPhoneNumber("").build())
                        .setWhatsappReceiver(com.example.shared.avro.WhatsappReceiver.newBuilder().setPhoneNumber("").build())
                        .setPushReceiver(com.example.shared.avro.PushReceiver.newBuilder().setAppUserId("").build())
                        .build(),
                    Receiver.newBuilder()
                        .setEmailReceiver(EmailReceiver.newBuilder().setTo(new java.util.ArrayList<>()).setCc(new java.util.ArrayList<>()).setBcc(new java.util.ArrayList<>()).build())
                        .setSmsReceiver(SmsReceiver.newBuilder().setPhoneNumber("+201006332994").build())
                        .setWhatsappReceiver(com.example.shared.avro.WhatsappReceiver.newBuilder().setPhoneNumber("").build())
                        .setPushReceiver(com.example.shared.avro.PushReceiver.newBuilder().setAppUserId("").build())
                        .build(),
                    Receiver.newBuilder()
                        .setEmailReceiver(EmailReceiver.newBuilder().setTo(new java.util.ArrayList<>()).setCc(new java.util.ArrayList<>()).setBcc(new java.util.ArrayList<>()).build())
                        .setSmsReceiver(SmsReceiver.newBuilder().setPhoneNumber("").build())
                        .setWhatsappReceiver(com.example.shared.avro.WhatsappReceiver.newBuilder().setPhoneNumber("+201006332994").build())
                        .setPushReceiver(com.example.shared.avro.PushReceiver.newBuilder().setAppUserId("").build())
                        .build(),
                    Receiver.newBuilder()
                        .setEmailReceiver(EmailReceiver.newBuilder().setTo(new java.util.ArrayList<>()).setCc(new java.util.ArrayList<>()).setBcc(new java.util.ArrayList<>()).build())
                        .setSmsReceiver(SmsReceiver.newBuilder().setPhoneNumber("").build())
                        .setWhatsappReceiver(com.example.shared.avro.WhatsappReceiver.newBuilder().setPhoneNumber("").build())
                        .setPushReceiver(com.example.shared.avro.PushReceiver.newBuilder().setAppUserId("fcm_token_or_apns_token_here").build())
                        .build()
                ))
                .setSender(com.example.shared.avro.Sender.newBuilder().build())
                .setAttachment(java.util.List.of(
                    com.example.shared.avro.Attachment.newBuilder()
                        .setUrl("https://s3.amazonaws.com/bucket-name/invoice-123.pdf")
                        .setName("invoice-123.pdf")
                        .build()
                ))
                .setNotificationChannels(java.util.List.of(
                    com.example.shared.avro.NotificationChannel.newBuilder()
                        .setChannel("EMAIL")
                        .setSender("noreply@example.com")
                        .setOverridingBody("Hello Mostafa, welcome to our platform!")
                        .setBody(null)
                        .setProperties(java.util.Map.of(
                            "subject", "Test Code",
                            "designTemplateURL", "https://s3.amazonaws.com/bucket-name/email-template.html",
                            "attachmentURL", "https://s3.amazonaws.com/bucket-name/invoice-123.pdf"
                        ))
                        .build(),
                    com.example.shared.avro.NotificationChannel.newBuilder()
                        .setChannel("SMS")
                        .setSender("+2010000000000")
                        .setOverridingBody("Welcome Samy! Your account is ready.")
                        .setBody(null)
                        .setProperties(java.util.Map.of())
                        .build(),
                    com.example.shared.avro.NotificationChannel.newBuilder()
                        .setChannel("WHATSAPP")
                        .setSender("+2010000000000")
                        .setOverridingBody("Welcome Mostafa! Your account has been successfully created. You can now access all our platform features. Thank you for joining us!")
                        .setBody(null)
                        .setProperties(java.util.Map.of(
                            "attachmentURL", "https://s3.amazonaws.com/bucket-name/welcome-image.jpg"
                        ))
                        .build(),
                    com.example.shared.avro.NotificationChannel.newBuilder()
                        .setChannel("PUSH_NOTIFICATION")
                        .setSender(null)
                        .setOverridingBody(null)
                        .setBody("Hey Mostafa! You're all set to explore the app.")
                        .setProperties(java.util.Map.of(
                            "deepLink", "app://welcome-screen",
                            "sound_name", "default"
                        ))
                        .build()
                ))
                .setExternalFields(java.util.Map.of(
                    "externalField", "extra-data"
                ))
                .build();

            notificationProducerService.sendRawNotification(notificationMessage);
            return ResponseEntity.ok("Sample notification simulation sent.");
        } catch (Exception e) {
            log.error("Error simulating sample notification", e);
            return ResponseEntity.internalServerError().body("Simulation failed: " + e.getMessage());
        }
    }

    @PostMapping("/test/extra-fields")
    public ResponseEntity<String> testExtraFields(@RequestBody Map<String, Object> rawPayload) {
        try {
            String eventId = (String) rawPayload.get("eventId");
            String userId = (String) rawPayload.get("userId");
            
            Map<String, String> externalFields = new HashMap<>();
            externalFields.put("paymentId", "test-payment-123");
            externalFields.put("extraField1", "This field is not in Avro schema");
            externalFields.put("nonSchemaField", "Should be ignored by consumer");
            
            rawPayload.forEach((key, value) -> {
                if (!key.equals("eventId") && !key.equals("userId") && value != null) {
                    externalFields.put(key, value.toString());
                }
            });

            NotificationMessage message = NotificationMessage.newBuilder()
                .setEventId(eventId != null ? eventId : "")
                .setTimestamp(java.time.Instant.now().toString())
                .setEventSource("payment-service-test")
                .setPriority("HIGH")
                .setCategory("TEST")
                .setScheduledSendTime(null)
                .setClientId(userId)
                .setChannels(java.util.List.of("EMAIL"))
                .setHref("https://test.example.com")
                .setLanguage("en")
                .setUseCommonContent(true)
                .setContent("Test message with extra fields")
                .setMessageType("EXTRA_FIELDS_TEST")
                .setReceiver(java.util.List.of(
                    Receiver.newBuilder()
                        .setEmailReceiver(EmailReceiver.newBuilder()
                            .setTo(java.util.List.of("test@email.com"))
                            .setCc(new ArrayList<>())
                            .setBcc(new ArrayList<>())
                            .build())
                        .build()
                ))
                .setSender(com.example.shared.avro.Sender.newBuilder().build())
                .setAttachment(new ArrayList<>())
                .setNotificationChannels(java.util.List.of(
                    com.example.shared.avro.NotificationChannel.newBuilder()
                        .setChannel("EMAIL")
                        .setSender("test@sender.com")
                        .setBody("Test message body")
                        .setProperties(java.util.Map.of("subject", "Extra Fields Test"))
                        .build()
                ))
                .setExternalFields(externalFields)
                .build();

            notificationProducerService.sendRawNotification(message);
            return ResponseEntity.ok("Test message with extra fields sent. EventId: " + eventId);
        } catch (Exception e) {
            log.error("Error sending test message with extra fields", e);
            return ResponseEntity.internalServerError().body("Test failed: " + e.getMessage());
        }
    }

    @PostMapping("/test/schema-compatibility")
    public ResponseEntity<String> testSchemaCompatibility(@RequestBody Map<String, Object> payload) {
        try {
            String eventId = (String) payload.get("eventId");
            
            NotificationMessage message = NotificationMessage.newBuilder()
                .setEventId(eventId != null ? eventId : "schema-test-" + UUID.randomUUID())
                .setTimestamp(java.time.Instant.now().toString())
                .setEventSource("schema-compatibility-test")
                .setPriority("MEDIUM")
                .setCategory("SCHEMA_TEST")
                .setScheduledSendTime(null)
                .setClientId("test-client")
                .setChannels(java.util.List.of("EMAIL"))
                .setHref("https://schema-test.example.com")
                .setLanguage("en")
                .setUseCommonContent(false)
                .setContent("Schema compatibility test message")
                .setMessageType("SCHEMA_COMPATIBILITY_TEST")
                .setReceiver(java.util.List.of(
                    Receiver.newBuilder()
                        .setEmailReceiver(EmailReceiver.newBuilder()
                            .setTo(java.util.List.of("schema-test@email.com"))
                            .setCc(new ArrayList<>())
                            .setBcc(new ArrayList<>())
                            .build())
                        .build()
                ))
                .setSender(com.example.shared.avro.Sender.newBuilder().build())
                .setAttachment(new ArrayList<>())
                .setNotificationChannels(java.util.List.of(
                    com.example.shared.avro.NotificationChannel.newBuilder()
                        .setChannel("EMAIL")
                        .setSender("schema-test@sender.com")
                        .setBody("Schema compatibility test body")
                        .setProperties(java.util.Map.of(
                            "subject", "Schema Compatibility Test",
                            "test-property", "Testing if schema accepts this"
                        ))
                        .build()
                ))
                .setExternalFields(java.util.Map.of(
                    "schemaTestField", "Testing schema compatibility",
                    "timestamp", java.time.Instant.now().toString()
                ))
                .build();

            notificationProducerService.sendRawNotification(message);
            
            return ResponseEntity.ok("Schema compatibility test passed. Message sent with eventId: " + eventId);
        } catch (Exception e) {
            log.error("Schema compatibility test failed", e);
            if (e.getMessage().contains("schema") || e.getMessage().contains("compatibility") || 
                e.getMessage().contains("registry") || e.getMessage().contains("avro")) {
                return ResponseEntity.badRequest().body("SCHEMA COMPATIBILITY ERROR: " + e.getMessage());
            }
            return ResponseEntity.internalServerError().body("Schema test failed: " + e.getMessage());
        }
    }
} 