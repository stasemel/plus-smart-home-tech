package ru.yandex.practicum.telemetry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationStarter {
    private final KafkaSnapshotProducer kafkaSnapshotProducer;
    private final KafkaEventConsumer kafkaEventConsumer;
    private final SnapshotService snapshotService;
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public void start() throws Exception {
        log.info("Start aggregation");
        KafkaConsumer<String, SensorEventAvro> consumer = kafkaEventConsumer.getConsumer();
        String topic = kafkaEventConsumer.getKafkaConfig().getSensorEventTopic();
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            log.info("Subscribe to topic: {} ", topic);
            consumer.subscribe(List.of(topic));
            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                if (records.isEmpty()) continue;
                int count = 0;
                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    handleRecord(record);
                    manageOffsets(record, count, kafkaEventConsumer);
                    count++;
                }
            }
        } catch (WakeupException ignored) {
            log.info("Received WakeupException");
        } finally {
            try {
                kafkaSnapshotProducer.getProducer().flush();
                kafkaEventConsumer.getConsumer().commitSync(currentOffsets);
            } finally {
                log.info("Will close consumer");
                kafkaEventConsumer.close();
                log.info("Will close producer");
                kafkaSnapshotProducer.close();
            }
        }
    }

    private void handleRecord(ConsumerRecord<String, SensorEventAvro> record) {
        log.info("Handle record {}", record);
        Optional<SensorsSnapshotAvro> optSnapshot = snapshotService.updateSnapshot(record.value());
        if (optSnapshot.isEmpty()) return;
        SensorsSnapshotAvro snapshot = optSnapshot.get();
        String topic = kafkaSnapshotProducer.getKafkaConfig().getSnapshotsTopic();
        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(
                topic,
                null,
                snapshot.getTimestamp().toEpochMilli(),
                snapshot.getHubId(),
                snapshot);
        kafkaSnapshotProducer.getProducer().send(producerRecord, (recordMetadata, e) -> {
            if (e != null) {
                log.error("Ошибка при отправке snapshot в топик {}: {}", topic, e.getMessage());
            } else {
                log.info("Снапшот успешно отправлен в топик {}, партицию {}, смещение {}, event {}",
                        recordMetadata.topic(),
                        recordMetadata.partition(),
                        recordMetadata.offset(),
                        snapshot);
            }
        });
    }

    private static void manageOffsets(ConsumerRecord<String, SensorEventAvro> record, int count, KafkaEventConsumer consumer) {
        // обновляем текущий оффсет для топика-партиции
        log.info("Manage offsets topic {}, partition {}, count {}, currentOffset {}", record.topic(), record.partition(), count, record.offset());
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
}
