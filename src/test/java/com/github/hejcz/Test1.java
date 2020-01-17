package com.github.hejcz;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.KafkaContainer;

public class Test1 {

    @Rule
    public KafkaContainer kafka = new KafkaContainer().withEmbeddedZookeeper();

    @Test
    public void test1() throws InterruptedException {
        Properties producerConfig = new Properties();
        producerConfig.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        producerConfig.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerConfig.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> producer = new KafkaProducer<>(producerConfig);
        producer.send(new ProducerRecord<>("topic-name", 0, "k1", "hello"), (metadata, ex) -> System.out.println(metadata));
        producer.send(new ProducerRecord<>("topic-name", 0, "k1", "hello2"), (metadata, ex) -> System.out.println(metadata));
        producer.send(new ProducerRecord<>("topic-name", 0, "k1", "hello3"), (metadata, ex) -> System.out.println(metadata));

        Properties consumerConfig = new Properties();
        consumerConfig.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        consumerConfig.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group-0");
//        consumerConfig.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
//        consumerConfig.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "20000");
//        consumerConfig.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerConfig);
        consumer.assign(Collections.singleton(new TopicPartition("topic-name", 0)));

        consumer.seek(new TopicPartition("topic-name", 0), 0);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> record : records)

                // print the offset,key and value for the consumer records.
                System.out.printf("offset = %d, key = %s, value = %s\n",
                        record.offset(), record.key(), record.value());
        }
    }
}
