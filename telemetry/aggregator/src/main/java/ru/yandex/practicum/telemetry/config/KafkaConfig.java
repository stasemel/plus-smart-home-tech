package ru.yandex.practicum.telemetry.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "kafka.aggregator")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class KafkaConfig {
    String bootstrapServers;
    String sensorEventTopic;
    String snapshotsTopic;

    Producer producer = new Producer();
    Consumer consumer = new Consumer();

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
    public static class Producer {
        String keySerializer;
        String valueSerializer;
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Consumer {
        String keyDeserializer;
        String valueDeserializer;
        String groupId;
        String clientId;
        Properties properties = new Properties();

        @Getter
        @Setter
        @FieldDefaults(level = AccessLevel.PRIVATE)
        public static class Properties {
            Integer fetchMinBytes;
            Integer maxPollRecords;
            Boolean enableAutoCommit;
            Integer fetchMaxWaitMs;
            Integer maxPartitionFetchBytes;
        }
    }
}
