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
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final KafkaHubEventConsumer kafkaHubEventConsumer;
    private final ScenarioService scenarioService;
    private final SensorService sensorService;
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @Override
    public void run() {
        KafkaConsumer<String, HubEventAvro> consumer = kafkaHubEventConsumer.getConsumer();
        String topic = kafkaHubEventConsumer.getKafkaConfig().getSnapshotsTopic();
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            log.info("Subscribe to topic: {} ", topic);
            consumer.subscribe(List.of(topic));
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                if (records.isEmpty()) continue;
                int count = 0;
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    processEvent(record.value());
                    manageOffsets(record, count, kafkaHubEventConsumer);
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
        } catch (RuntimeException e) {
            log.error("Error {}", e);
        } finally {
            try {
                kafkaHubEventConsumer.getConsumer().commitSync(currentOffsets);
            } finally {
                log.info("Will close consumer");
                try {
                    kafkaHubEventConsumer.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void manageOffsets(ConsumerRecord<String, HubEventAvro> record, int count, KafkaHubEventConsumer consumer) {
        log.info("Manage hub event offsets topic {}, partition {}, count {}, currentOffset {}", record.topic(), record.partition(), count, record.offset());
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

    private void processEvent(HubEventAvro hubEventAvro) {
        log.info("Process hub event {}", hubEventAvro);
        String hubId = hubEventAvro.getHubId();
        switch (hubEventAvro.getPayload()) {
            case DeviceAddedEventAvro deviceAddedEventAvro -> processEvent(hubId, deviceAddedEventAvro);
            case DeviceRemovedEventAvro deviceRemovedEventAvro -> processEvent(hubId, deviceRemovedEventAvro);
            case ScenarioAddedEventAvro scenarioAddedEventAvro -> processEvent(hubId, scenarioAddedEventAvro);
            case ScenarioRemovedEventAvro scenarioRemovedEventAvro -> processEvent(hubId, scenarioRemovedEventAvro);
            default -> log.warn("Unknown hub event {}", hubEventAvro);
        }
    }

    private void processEvent(String hubId, ScenarioRemovedEventAvro scenarioRemovedEventAvro) {
        scenarioService.deleteScenario(hubId, scenarioRemovedEventAvro.getName());
    }

    private void processEvent(String hubId, ScenarioAddedEventAvro scenarioAddedEventAvro) {
        scenarioService.addScenario(hubId, scenarioAddedEventAvro);
    }

    private void processEvent(String hubId, DeviceRemovedEventAvro deviceRemovedEventAvro) {
        sensorService.deviceRemoved(hubId, deviceRemovedEventAvro.getId());

    }

    private void processEvent(String hubId, DeviceAddedEventAvro deviceAddedEventAvro) {
        sensorService.addDevice(hubId, deviceAddedEventAvro);

    }

}