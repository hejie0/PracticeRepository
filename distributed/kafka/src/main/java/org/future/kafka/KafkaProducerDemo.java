package org.future.kafka;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaProducerDemo extends Thread {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String topic;

    private final KafkaProducer<Integer, String> producer;

    public KafkaProducerDemo(String topic) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "192.168.50.144:9091");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaProducerDemo");
        props.put(ProducerConfig.ACKS_CONFIG, "-1");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<Integer, String>(props);
        this.topic = topic;
    }

    @Override
    public void run() {
        int num = 0;
        while (num < 50) {
            String message = "message" + num;
            log.info("begin send message: {}", message);
            producer.send(new ProducerRecord<Integer, String>(topic, message));
            num++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("{}", ExceptionUtils.getStackTrace(e));
            }
        }
    }

    public static void main(String[] args) {
        new KafkaProducerDemo("test").start();
    }
}
