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

        String topic = "users-avro";

        User user1 = User.newBuilder()
                .setId(3)
                .setName("Omar22")
                .setEmail("Omar22@gmail.com")
                .build();
        
        User user2 = User.newBuilder()
                .setId(4)
                .setName("Mahmoud22")
                .setEmail("Mahmoud22@gmail.com")
                .build();

        try (KafkaProducer<String, User> producer = new KafkaProducer<>(props)) {
            sendUser(producer, topic, user1);
            sendUser(producer, topic, user2);
            producer.flush();
            logger.info("Producer closed.");
        } catch (Exception e) {
            logger.error("Producer failed: {}", e.getMessage());
        }
    }

    private static void sendUser(KafkaProducer<String, User> producer, String topic, User user) {
        try {
            producer.send(new ProducerRecord<>(topic, user));
            logger.info("Produced: {}", user);
        } catch (Exception e) {
            logger.error("Error producing message: {}", e.getMessage());
        }
    }
}
