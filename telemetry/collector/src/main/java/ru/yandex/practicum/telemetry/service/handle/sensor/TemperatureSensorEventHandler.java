package ru.yandex.practicum.telemetry.service.handle.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.model.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.model.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.telemetry.service.KafkaEventProducer;

@Component
public class TemperatureSensorEventHandler extends BaseSensorEventHandler {
    public TemperatureSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected SpecificRecordBase mapToAvro(SensorEvent sensorEvent) {
        TemperatureSensorEvent event = (TemperatureSensorEvent) sensorEvent;
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .setTimestamp(event.getTimestamp())
                .setHubId(event.getHubId())
                .setId(event.getId())
                .build();
    }

    @Override
    public SensorEventType getEventType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
