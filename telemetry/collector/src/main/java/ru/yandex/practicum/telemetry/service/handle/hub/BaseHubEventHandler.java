package ru.yandex.practicum.telemetry.service.handle.hub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.service.KafkaEventProducer;

@RequiredArgsConstructor
@Getter
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    protected final KafkaEventProducer producer;
    private final String topic = "telemetry.hubs.v1";

    protected abstract T mapToAvro(HubEvent hubEvent);

    @Override
    public void handle(HubEvent hubEvent) {
        HubEventAvro hubEventAvro = HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(mapToAvro(hubEvent))
                .build();
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(getTopic(), hubEventAvro);
        producer.getProducer().send(record);
    }
}
