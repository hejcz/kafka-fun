package com.github.hejcz;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

public class Test1 {

    // Lepiej podnieść sobie klaster lokalnie. Ten testwy obraz ma tylko jedną partycję
    // https://github.com/wurstmeister/kafka-docker
    // kafka-topics.sh --create --zookeeper zookeeper --replication-factor 2 --partitions 4 --topic topic-name
    // kafka-topics.sh --describe topic-name --zookeeper zookeeper

    @Test
    public void test1() throws InterruptedException {
//        Properties producerConfig = new Properties();
//        producerConfig.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.17.0.1:32979,172.17.0.1:32978,172.17.0.1:32980");
//        producerConfig.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        producerConfig.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        KafkaProducer<String, String> producer = new KafkaProducer<>(producerConfig);
//
//        for (int i = 0; i < 100; i++) {
//            int partition = new Random().nextInt(4);
//            producer.send(new ProducerRecord<>("topic-name", partition, "k" + partition, "hello" + i));
//        }

        Properties consumerConfig = new Properties();
        consumerConfig.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.17.0.1:32981,172.17.0.1:32979,172.17.0.1:32978");
        consumerConfig.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group-0");
        consumerConfig.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        consumerConfig.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
//        consumerConfig.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "20000");
        consumerConfig.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerConfig);
        TopicPartition topicZeroPartition = new TopicPartition("topic-name", 0);
        consumer.assign(Collections.singleton(topicZeroPartition));
        consumer.seek(topicZeroPartition, 0);

        while (true) {
            loop(consumer);
        }
    }

    private void loop(KafkaConsumer<String, String> consumer) {
        System.out.println("consume loop " + consumer);
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
        for (ConsumerRecord<String, String> record : records) {
            System.out.printf("offset = %d, key = %s, value = %s\n",
                    record.offset(), record.key(), record.value());
        }
//        consumer.commitSync(Duration.ofSeconds(1));
    }
}
