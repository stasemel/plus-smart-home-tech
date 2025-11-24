package ru.yandex.practicum.telemetry.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.config.KafkaConfig;

import java.util.Properties;

@Component
@Getter
@Slf4j
public class KafkaEventConsumer implements AutoCloseable {
    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final KafkaConfig kafkaConfig;

    public KafkaEventConsumer(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
        Properties config = getConfigProperties();
        this.consumer = new KafkaConsumer<>(config);
    }

    @Override
    public void close() throws Exception {
        consumer.close();
    }

    private Properties getConfigProperties() {
        Properties config = new Properties();
        KafkaConfig.Consumer consumerConfig = kafkaConfig.getConsumer();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerConfig.getKeyDeserializer());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerConfig.getValueDeserializer());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerConfig.getGroupId());
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, consumerConfig.getClientId());
        KafkaConfig.Consumer.Properties properties = consumerConfig.getProperties();
        if (properties.getFetchMinBytes() != null) {
            config.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, properties.getFetchMinBytes());
        }
        if (properties.getMaxPollRecords() != null) {
            config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, properties.getMaxPollRecords());
        }
        if (properties.getEnableAutoCommit() != null) {
            config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, properties.getEnableAutoCommit());
        }
        if (properties.getFetchMaxWaitMs() != null) {
            config.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, properties.getFetchMaxWaitMs());
        }
        if (properties.getMaxPartitionFetchBytes() != null) {
            config.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, properties.getMaxPartitionFetchBytes());
        }
        return config;
    }
}
