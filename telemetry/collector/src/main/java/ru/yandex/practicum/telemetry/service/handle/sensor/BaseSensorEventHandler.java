package ru.yandex.practicum.telemetry.service.handle.sensor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.service.KafkaEventProducer;

@AllArgsConstructor
@Getter
public abstract class BaseSensorEventHandler implements SensorEventHandler {
    private final KafkaEventProducer producer;
    private final String topic = "telemetry.sensors.v1";

    protected abstract SpecificRecordBase mapToAvro(SensorEvent sensorEvent);

    @Override
    public void handle(SensorEvent sensorEvent) {
        SensorEventAvro sensorEventAvro = SensorEventAvro.newBuilder()
                .setHubId(sensorEvent.getHubId())
                .setId(sensorEvent.getId())
                .setPayload(mapToAvro(sensorEvent))
                .setTimestamp(sensorEvent.getTimestamp())
                .build();
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(getTopic(), sensorEventAvro);
        producer.getProducer().send(record);
    }
}
