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
@ConfigurationProperties(prefix = "kafka.collector")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class KafkaConfig {
    String bootstrapServers;
    String hubEventTopic;
    String sensorEventTopic;
    Producer producer = new Producer();

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
    public static class Producer {
        String keySerializer;
        String valueSerializer;
    }
}
