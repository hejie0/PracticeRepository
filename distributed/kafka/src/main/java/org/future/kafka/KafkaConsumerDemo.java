package org.future.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerDemo extends Thread {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private String topic;

    private final KafkaConsumer<Integer, String> consumer;

    public KafkaConsumerDemo(String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "192.168.50.144:9091,192.168.50.144:9092,192.168.50.144:9093");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaConsumerDemo");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000"); //自动提交（批量确认）
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        this.topic = topic;
        this.consumer = new KafkaConsumer(props);
        consumer.subscribe(Collections.singletonList(topic));
    }

    @Override
    public void run() {
        ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofMillis(1000));
        for (ConsumerRecord record : records) {
            log.info("message receive: key:{}, value:{}, offset:{}", record.key(), record.value(), record.offset());
        }
    }

    public static void main(String[] args) {
        new KafkaConsumerDemo("test").run();
    }
}
