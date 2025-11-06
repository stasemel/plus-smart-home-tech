package ru.yandex.practicum.telemetry.service.handle.hub;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.model.hub.HubEventType;
import ru.yandex.practicum.telemetry.model.hub.device.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.service.KafkaEventProducer;

@Component
@Getter
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {

    public DeviceAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }


    @Override
    public HubEventType getEventType() {
        return HubEventType.DEVICE_ADDED;
    }


    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEvent hubEvent) {
        DeviceAddedEvent event = (DeviceAddedEvent) hubEvent;
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(DeviceTypeAvro.valueOf(String.valueOf(event.getDeviceType())))
                .build();
    }
}
