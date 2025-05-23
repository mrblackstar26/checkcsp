package com.example.kafkademo;

import org.apache.kafka.clients.producer.*;
import java.util.*;

public class KafkaProducerWeb {
    public static void send(String payload) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            ProducerRecord<String, String> record = new ProducerRecord<>("exploit-topic", payload);
            producer.send(record);
        }
    }
}