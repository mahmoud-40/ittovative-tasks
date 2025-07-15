package com.example.notificationservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.example.notificationservice.dto.ChannelInfo;
import com.example.notificationservice.dto.NotificationDto;
import com.example.notificationservice.dto.ReceiverInfo;
import com.example.shared.avro.EmailReceiver;
import com.example.shared.avro.NotificationMessage;
import com.example.shared.avro.Receiver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumerService {

    @KafkaListener(topics = "${notification.topic.name}")
    public void consumeNotification(
            ConsumerRecord<String, Object> record, 
            @Payload NotificationMessage notificationMessage,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        log.info("=== RAW MESSAGE RECEIVED (BEFORE AVRO DESERIALIZATION) ===");
        log.info("Raw Record - Key: {}, Value: {}, Topic: {}, Partition: {}, Offset: {}", 
                key, record.value(), topic, partition, offset);
        log.info("Raw Record Headers: {}", record.headers());
        
        try {
            NotificationDto notificationDto = convertToDto(notificationMessage);
            
            log.info("=== PROCESSED MESSAGE (AFTER AVRO DESERIALIZATION) ===");

            // log.info("NotificationDto: {}", notificationDto);

            log.info("Event ID: {}", notificationDto.getEventId());
            log.info("Timestamp: {}", notificationDto.getTimestamp());
            log.info("Event Source: {}", notificationDto.getEventSource());
            log.info("Priority: {}", notificationDto.getPriority());
            log.info("Category: {}", notificationDto.getCategory());
            log.info("Client ID: {}", notificationDto.getClientId());
            log.info("Channels: {}", notificationDto.getChannels());
            log.info("Message Type: {}", notificationDto.getMessageType());
            log.info("Content: {}", notificationDto.getContent());

            log.info("We will ignore external fields, but we will print them for testing");
            log.info("External Fields: {}", notificationDto.getExternalFields());
            
            log.info("=== RECEIVER INFORMATION ===");
            notificationDto.getReceivers().forEach(receiver -> {
                log.info("Receiver Type: {}", receiver.getType());
                if (receiver.getEmail() != null) {
                    log.info("  Email: {}", receiver.getEmail());
                }
                if (receiver.getPhoneNumber() != null) {
                    log.info("  Phone: {}", receiver.getPhoneNumber());
                }
                if (receiver.getEmailTo() != null && !receiver.getEmailTo().isEmpty()) {
                    log.info("  Email To: {}", receiver.getEmailTo());
                }
                if (receiver.getEmailCc() != null && !receiver.getEmailCc().isEmpty()) {
                    log.info("  Email CC: {}", receiver.getEmailCc());
                }
                if (receiver.getEmailBcc() != null && !receiver.getEmailBcc().isEmpty()) {
                    log.info("  Email BCC: {}", receiver.getEmailBcc());
                }
            });
            
            log.info("=== NOTIFICATION CHANNELS ===");
            notificationDto.getNotificationChannels().forEach(channel -> {
                log.info("Channel: {}", channel.getChannel());
                log.info("  Sender: {}", channel.getSender());
                log.info("  Body: {}", channel.getBody());
                log.info("  Properties: {}", channel.getProperties());
            });
            
            simulateNotificationProcessing(notificationDto);
            
            acknowledgment.acknowledge();
            
            log.info("Successfully processed notification with event ID: {}", notificationDto.getEventId());
            
        } catch (Exception e) {
            log.error("Error processing notification - {}", e.getMessage(), e);
            acknowledgment.acknowledge();
        }
    }

    private NotificationDto convertToDto(NotificationMessage avroMessage) {
        List<ReceiverInfo> receivers = new ArrayList<>();
        if (avroMessage.getReceiver() != null) {
            for (Receiver receiver : avroMessage.getReceiver()) {
                if (receiver.getEmailReceiver() != null) {
                    EmailReceiver emailReceiver = receiver.getEmailReceiver();
                    receivers.add(ReceiverInfo.builder()
                            .type("EMAIL")
                            .emailTo(emailReceiver.getTo().stream().map(Object::toString).collect(Collectors.toList()))
                            .emailCc(emailReceiver.getCc().stream().map(Object::toString).collect(Collectors.toList()))
                            .emailBcc(emailReceiver.getBcc().stream().map(Object::toString).collect(Collectors.toList()))
                            .build());
                }
                
                if (receiver.getSmsReceiver() != null) {
                    receivers.add(ReceiverInfo.builder()
                            .type("SMS")
                            .phoneNumber(receiver.getSmsReceiver().getPhoneNumber().toString())
                            .build());
                }
                
                if (receiver.getWhatsappReceiver() != null) {
                    receivers.add(ReceiverInfo.builder()
                            .type("WHATSAPP")
                            .phoneNumber(receiver.getWhatsappReceiver().getPhoneNumber().toString())
                            .build());
                }
                
                if (receiver.getPushReceiver() != null) {
                    receivers.add(ReceiverInfo.builder()
                            .type("PUSH")
                            .appUserId(receiver.getPushReceiver().getAppUserId().toString())
                            .build());
                }
            }
        }
        
        List<ChannelInfo> channels = new ArrayList<>();
        if (avroMessage.getNotificationChannels() != null) {
            channels = avroMessage.getNotificationChannels().stream()
                    .map(channel -> ChannelInfo.builder()
                            .channel(channel.getChannel().toString())
                            .sender(channel.getSender() != null ? channel.getSender().toString() : null)
                            .body(channel.getBody() != null ? channel.getBody().toString() : null)
                            .overridingBody(channel.getOverridingBody() != null ? channel.getOverridingBody().toString() : null)
                            .properties(new HashMap<>(channel.getProperties()))
                            .build())
                    .collect(Collectors.toList());
        }
        
        return NotificationDto.builder()
                .eventId(avroMessage.getEventId().toString())
                .timestamp(avroMessage.getTimestamp().toString())
                .eventSource(avroMessage.getEventSource().toString())
                .priority(avroMessage.getPriority().toString())
                .category(avroMessage.getCategory().toString())
                .scheduledSendTime(avroMessage.getScheduledSendTime() != null ? avroMessage.getScheduledSendTime().toString() : null)
                .clientId(avroMessage.getClientId() != null ? avroMessage.getClientId().toString() : null)
                .channels(avroMessage.getChannels().stream().map(Object::toString).collect(Collectors.toList()))
                .href(avroMessage.getHref().toString())
                .language(avroMessage.getLanguage().toString())
                .useCommonContent(avroMessage.getUseCommonContent())
                .content(avroMessage.getContent().toString())
                .messageType(avroMessage.getMessageType().toString())
                .externalFields(new HashMap<>(avroMessage.getExternalFields()))
                .receivers(receivers)
                .notificationChannels(channels)
                .build();
    }

    private void simulateNotificationProcessing(NotificationDto notification) {
        try {
            log.info("Simulating notification processing for event ID: {}", notification.getEventId());
            
            Thread.sleep(100);
            
            notification.getNotificationChannels().forEach(channel -> {
                switch (channel.getChannel().toUpperCase()) {
                    case "EMAIL":
                        log.info("Simulating EMAIL sending via channel: {}", channel.getChannel());
                        break;
                    case "SMS":
                        log.info("Simulating SMS sending via channel: {}", channel.getChannel());
                        break;
                    case "WHATSAPP":
                        log.info("Simulating WhatsApp sending via channel: {}", channel.getChannel());
                        break;
                    case "PUSH":
                        log.info("Simulating PUSH notification sending via channel: {}", channel.getChannel());
                        break;
                    default:
                        log.info("Simulating notification sending via channel: {}", channel.getChannel());
                }
            });
            
            log.info("Notification processing simulation completed for event ID: {}", notification.getEventId());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Notification processing interrupted", e);
        }
    }
} 