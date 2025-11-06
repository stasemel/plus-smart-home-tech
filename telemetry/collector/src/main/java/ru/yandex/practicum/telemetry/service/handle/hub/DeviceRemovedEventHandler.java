package ru.yandex.practicum.telemetry.service.handle.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.model.hub.HubEventType;
import ru.yandex.practicum.telemetry.model.hub.device.DeviceRemovedEvent;
import ru.yandex.practicum.telemetry.service.KafkaEventProducer;

@Component
public class DeviceRemovedEventHandler extends BaseHubEventHandler<DeviceRemovedEventAvro> {
    public DeviceRemovedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected DeviceRemovedEventAvro mapToAvro(HubEvent hubEvent) {
        DeviceRemovedEvent event = (DeviceRemovedEvent) hubEvent;
        return DeviceRemovedEventAvro.newBuilder().setId(event.getId()).build();
    }

    @Override
    public HubEventType getEventType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
