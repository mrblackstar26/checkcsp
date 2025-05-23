package com.example.kafkademo;

import com.fasterxml.jackson.databind.*;
import org.apache.kafka.clients.consumer.*;

import java.util.*;

public class KafkaConsumerVuln {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "vuln-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("exploit-topic"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL); // UNSAFE

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                try {
                    Object obj = mapper.readValue(record.value(), Object.class);
                    System.out.println("[INFO] Deserialized: " + obj.getClass().getName());
                } catch (Exception e) {
                    System.out.println("[ERROR] Deserialization failed: " + e.getMessage());
                }
            }
        }
    }
}