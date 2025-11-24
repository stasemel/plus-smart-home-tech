package ru.yandex.practicum.telemetry.service.grpc.sensor;

import lombok.Getter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.service.KafkaEventProducer;

import java.time.Instant;

@Getter
public abstract class BaseSensorEventHandler implements SensorEventHandler {
    private final KafkaEventProducer producer;
    private final String topic = "telemetry.sensors.v1";


    protected BaseSensorEventHandler(KafkaEventProducer producer) {
        this.producer = producer;
    }

    protected abstract SpecificRecordBase mapToAvro(SensorEventProto sensorEvent);

    @Override
    public void handle(SensorEventProto sensorEvent) {
        SensorEventAvro sensorEventAvro = SensorEventAvro.newBuilder()
                .setHubId(sensorEvent.getHubId())
                .setId(sensorEvent.getId())
                .setPayload(mapToAvro(sensorEvent))
                .setTimestamp(
                        Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(),
                                sensorEvent.getTimestamp().getNanos())
                )
                .build();
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(getTopic(), sensorEventAvro);
        producer.getProducer().send(record);
    }
}
