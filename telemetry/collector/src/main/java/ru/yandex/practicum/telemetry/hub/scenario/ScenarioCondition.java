package ru.yandex.practicum.telemetry.hub.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioCondition {
    private String sensorId;
    private ScenarioConditionType type;
    private ScenarioOperation operation;
    private int value;
}
