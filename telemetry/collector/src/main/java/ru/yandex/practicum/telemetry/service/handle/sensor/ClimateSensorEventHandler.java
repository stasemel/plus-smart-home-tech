package ru.yandex.practicum.telemetry.service.handle.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.telemetry.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.telemetry.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.model.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.service.KafkaEventProducer;

@Component
public class ClimateSensorEventHandler extends BaseSensorEventHandler {
    public ClimateSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected SpecificRecordBase mapToAvro(SensorEvent sensorEvent) {
        ClimateSensorEvent event = (ClimateSensorEvent) sensorEvent;
        return ClimateSensorAvro.newBuilder()
                .setCo2Level(event.getCo2Level())
                .setHumidity(event.getHumidity())
                .setTemperatureC(event.getTemperatureC())
                .build();
    }

    @Override
    public SensorEventType getEventType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
