package ru.yandex.practicum.telemetry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class SnapshotProcessor {
    private final KafkaSnapshotConsumer kafkaSnapshotConsumer;
    private final SnapshotService snapshotService;
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public void start() throws Exception {
        KafkaConsumer<String, SensorsSnapshotAvro> consumer = kafkaSnapshotConsumer.getConsumer();
        String topic = kafkaSnapshotConsumer.getKafkaConfig().getSnapshotsTopic();
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            log.info("Subscribe to topic: {} ", topic);
            consumer.subscribe(List.of(topic));
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                if (records.isEmpty()) continue;
                int count = 0;
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    processSnapshot(record.value());
                    manageOffsets(record, count, kafkaSnapshotConsumer);
                    count++;
                }
                consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                    if (exception != null) {
                        log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                    }
                });
            }
        } catch (WakeupException ignored) {
            log.info("Received WakeupException");
        } finally {
            try {
                kafkaSnapshotConsumer.getConsumer().commitSync(currentOffsets);
            } finally {
                log.info("Will close consumer");
                kafkaSnapshotConsumer.close();
            }
        }
    }

    private void manageOffsets(ConsumerRecord<String, SensorsSnapshotAvro> record, int count, KafkaSnapshotConsumer consumer) {
        log.info("Manage snapshots offsets topic {}, partition {}, count {}, currentOffset {}", record.topic(), record.partition(), count, record.offset());
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.getConsumer().commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }

    private void processSnapshot(SensorsSnapshotAvro snapshotAvro) {
        snapshotService.analyze(snapshotAvro);
    }
}