package com.mahmoud.consumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Scanner;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.mahmoud.avro.User;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;

public class ConsumerApplication {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press 1 to start consuming messages from Kafka:");
        String input = scanner.nextLine();
        if ("1".equals(input)) {
            Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "console-consumer-group");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put("schema.registry.url", "http://localhost:8081");
            props.put("specific.avro.reader", "true");

            KafkaConsumer<String, User> consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Collections.singletonList("users-avro"));
            System.out.println("Consuming messages. Press Ctrl+C to exit.");
            while (true) {
                ConsumerRecords<String, User> records = consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, User> record : records) {
                    User user = record.value();
                    System.out.printf("Received User: ID=%d, Name=%s, Email=%s%n", 
                        user.getId(), user.getName(), user.getEmail());
                }
            }
        } else {
            System.out.println("Exiting.");
        }
        scanner.close();
    }
}
