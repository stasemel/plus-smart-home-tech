package ru.yandex.practicum.telemetry.service;

import lombok.Getter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.config.KafkaConfig;

import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Getter
public class KafkaSnapshotProducer implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(KafkaSnapshotProducer.class);
    private static final ThreadLocalRandom rnd = ThreadLocalRandom.current();
    private final KafkaConfig kafkaConfig;

    protected final KafkaProducer<String, SpecificRecordBase> producer;

    public KafkaSnapshotProducer(KafkaConfig kafkaConfig) {
        Properties config = new Properties();
        this.kafkaConfig = kafkaConfig;
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducer().getKeySerializer());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducer().getValueSerializer());
        this.producer = new KafkaProducer<>(config);
    }

    @Override
    public void close() throws Exception {
        producer.close();
    }
}
