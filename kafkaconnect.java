package com.example;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class KafkaConnectivityChecker {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092"; // change if needed
    private static final String TOPIC_NAME = "connectivity-test";
    private static final String TEST_MESSAGE = "HelloKafka-" + UUID.randomUUID();

    public static void main(String[] args) {
        System.out.println("🔍 Let's check if everything is working behind the scenes...");

        try {
            sendMessage(TEST_MESSAGE);
            String received = receiveMessage();

            if (TEST_MESSAGE.equals(received)) {
                System.out.println("✅ Everything is working fine! 📬 A message was successfully sent and received!");
            } else {
                System.out.println("⚠️ Something went wrong! Sent and received messages don't match.");
            }
        } catch (Exception e) {
            System.out.println("🚨 Uh oh! We couldn't complete the message exchange. Please check the setup.");
            System.out.println("🛠️ Technical hint (for support only): " + e.getMessage());
        }
    }

    private static void sendMessage(String message) throws Exception {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, message);
            producer.send(record).get(); // synchronous send
        }
    }

    private static String receiveMessage() throws Exception {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + UUID.randomUUID());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC_NAME));
            long start = System.currentTimeMillis();

            while (System.currentTimeMillis() - start < 5000) { // wait up to 5 seconds
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    if (record.value().startsWith("HelloKafka-")) {
                        return record.value();
                    }
                }
            }
        }

        throw new RuntimeException("No expected message received within timeout.");
    }
}