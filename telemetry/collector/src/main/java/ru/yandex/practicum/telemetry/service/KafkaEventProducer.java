package ru.yandex.practicum.telemetry.service;

import lombok.Getter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.serializer.GeneralAvroSerializer;

import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Getter
public class KafkaEventProducer implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(KafkaEventProducer.class);
    private static final ThreadLocalRandom rnd = ThreadLocalRandom.current();

    protected final KafkaProducer<String, SpecificRecordBase> producer;

    public KafkaEventProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);

        this.producer = new KafkaProducer<>(config);
    }

    @Override
    public void close() throws Exception {
        producer.close();
    }
}
