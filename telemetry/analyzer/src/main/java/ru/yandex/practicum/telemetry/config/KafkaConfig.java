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
@ConfigurationProperties(prefix = "kafka.analyzer")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class KafkaConfig {
    String bootstrapServers;
    String hubsEventTopic;
    String snapshotsTopic;

    HubConsumer hubConsumer = new HubConsumer();
    SnapshotConsumer snapshotConsumer = new SnapshotConsumer();

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class HubConsumer {
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

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SnapshotConsumer {
        String keyDeserializer;
        String valueDeserializer;
        String groupId;
        String clientId;
        SnapshotConsumer.Properties properties = new SnapshotConsumer.Properties();

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
