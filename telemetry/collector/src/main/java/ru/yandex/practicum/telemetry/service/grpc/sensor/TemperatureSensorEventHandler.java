package ru.yandex.practicum.telemetry.service.grpc.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.service.KafkaEventProducer;

import java.time.Instant;

@Component
public class TemperatureSensorEventHandler extends BaseSensorEventHandler {
    protected TemperatureSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected SpecificRecordBase mapToAvro(SensorEventProto sensorEvent) {
        TemperatureSensorProto event = sensorEvent.getTemperatureSensor();
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .setTimestamp(
                        Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(),
                                sensorEvent.getTimestamp().getNanos())
                )
                .setHubId(sensorEvent.getHubId())
                .setId(sensorEvent.getId())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }
}
