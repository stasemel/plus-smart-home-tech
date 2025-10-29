package ru.yandex.practicum.telemetry.hub;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.hub.device.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.hub.device.DeviceRemovedEvent;
import ru.yandex.practicum.telemetry.hub.scenario.ScenarioAddedEvent;
import ru.yandex.practicum.telemetry.hub.scenario.ScenarioRemovedEvent;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = HubEvent.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = "DEVICE_ADDED"),
        @JsonSubTypes.Type(value = DeviceRemovedEvent.class, name = "DEVICE_REMOVED"),
        @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = "SCENARIO_ADDED"),
        @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = "SCENARIO_REMOVED")
})
@Getter
@Setter
@ToString
public abstract class HubEvent {
    private String hubId;
    private Instant timestamp = Instant.now();

    // абстрактный метод, который должен быть определён в конкретных реализациях
    public abstract HubEventType getType();
}