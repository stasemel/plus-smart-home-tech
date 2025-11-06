package ru.yandex.practicum.telemetry.service.handle.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.telemetry.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.telemetry.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.model.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.service.KafkaEventProducer;

@Component
public class MotionSensorEventHandler extends BaseSensorEventHandler {
    public MotionSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected SpecificRecordBase mapToAvro(SensorEvent sensorEvent) {
        MotionSensorEvent event = (MotionSensorEvent) sensorEvent;
        return MotionSensorAvro.newBuilder()
                .setMotion(event.isMotion())
                .setLinkQuality(event.getLinkQuality())
                .setVoltage(event.getVoltage())
                .build();
    }

    @Override
    public SensorEventType getEventType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
