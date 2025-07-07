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
            
            props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);      
            props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 32768);     
            props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 10);   
            
            KafkaConsumer<String, User> consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Collections.singletonList("users-avro"));
            System.out.println("Consuming messages in batches. Press Ctrl+C to exit.");
            
            long totalProcessed = 0;
            long startTime = System.currentTimeMillis();
            
            while (true) {
                ConsumerRecords<String, User> records = consumer.poll(Duration.ofMillis(1000));
                
                if (!records.isEmpty()) {
                    System.out.printf("Processing batch of %d records...%n", records.count());
                    
                    for (ConsumerRecord<String, User> record : records) {
                        User user = record.value();
                        System.out.printf("  User ID=%d, Name=%s, Email=%s (partition=%d, offset=%d)%n", 
            user.getId(), user.getName(), user.getEmail(), record.partition(), record.offset());
                        totalProcessed++;
                    }
                    
                    long elapsedTime = System.currentTimeMillis() - startTime; 
                    double throughput = totalProcessed / (elapsedTime / 1000.0);
                    System.out.printf("Batch processed! Total: %d records, Throughput: %.2f records/sec%n", 
                        totalProcessed, throughput);
                    
                    consumer.commitSync();
                }
            }
        } else {
            System.out.println("Exiting.");
        }
        scanner.close();
    }
}
