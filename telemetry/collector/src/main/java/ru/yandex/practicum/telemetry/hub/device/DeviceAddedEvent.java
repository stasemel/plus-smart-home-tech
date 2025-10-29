package ru.yandex.practicum.telemetry.hub.device;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.hub.HubEvent;
import ru.yandex.practicum.telemetry.hub.HubEventType;
import ru.yandex.practicum.telemetry.sensor.SensorEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubEvent {
    private String id;
    private SensorEventType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
