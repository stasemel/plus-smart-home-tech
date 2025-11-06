package ru.yandex.practicum.telemetry.service.handle.hub;

import ru.yandex.practicum.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.model.hub.HubEventType;

public interface HubEventHandler {
    HubEventType getEventType();

    void handle(HubEvent hubEvent);

}
