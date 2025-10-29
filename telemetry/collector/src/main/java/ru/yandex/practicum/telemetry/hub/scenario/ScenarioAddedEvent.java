package ru.yandex.practicum.telemetry.hub.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.hub.HubEvent;
import ru.yandex.practicum.telemetry.hub.HubEventType;
import ru.yandex.practicum.telemetry.hub.device.DeviceAction;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {

    private int name;
    private List<ScenarioCondition> conditions;
    private List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
