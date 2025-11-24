package ru.yandex.practicum.telemetry.service.grpc.hub;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.service.KafkaEventProducer;

import java.time.Instant;

@Getter
@Slf4j
public abstract class BaseHubEventHandler implements HubEventHandler {
    protected final KafkaEventProducer producer;
    private final String topic = "telemetry.hubs.v1";

    protected abstract SpecificRecordBase mapToAvro(HubEventProto hubEvent);

    protected BaseHubEventHandler(KafkaEventProducer producer) {
        this.producer = producer;
    }

    @Override
    public void handle(HubEventProto event) {
        HubEventAvro hubEventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(
                        Instant.ofEpochSecond(event.getTimestamp().getSeconds(),
                                event.getTimestamp().getNanos())
                )
                .setPayload(mapToAvro(event))
                .build();
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(getTopic(), hubEventAvro);
        producer.getProducer().send(record);

    }
}
