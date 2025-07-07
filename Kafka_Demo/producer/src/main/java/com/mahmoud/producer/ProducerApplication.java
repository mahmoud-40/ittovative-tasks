package com.mahmoud.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mahmoud.avro.User;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

public class ProducerApplication {
    private static final Logger logger = LoggerFactory.getLogger(ProducerApplication.class);

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put("schema.registry.url", "http://localhost:8081");

        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768);  
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);   

        String topic = "users-avro";

        try (KafkaProducer<String, User> producer = new KafkaProducer<>(props)) {
            for (int i = 1; i <= 100000; i++) {
                User user = User.newBuilder()
                    .setId(i)
                    .setName("User" + i)
                    .setEmail("user" + i + "@gmail.com")
                    .build();
                producer.send(new ProducerRecord<>(topic, user), (metadata, exception) -> {
                    if (exception != null) {
                        logger.error("Error producing message for user {}: {}", user.getName(), exception.getMessage());
                    } else {
                        logger.info("Produced user {} to partition {} at offset {}", user.getName(), metadata.partition(), metadata.offset());
                    }
                });
            }
            logger.info("All messages sent. Producer closed.");
        } catch (Exception e) {
            logger.error("Producer failed: {}", e.getMessage());
        }
    }
}