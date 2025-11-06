package ru.yandex.practicum.telemetry.service.handle.sensor;

import ru.yandex.practicum.telemetry.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.model.sensor.SensorEventType;

public interface SensorEventHandler {
    SensorEventType getEventType();

    void handle(SensorEvent sensorEvent);
}
