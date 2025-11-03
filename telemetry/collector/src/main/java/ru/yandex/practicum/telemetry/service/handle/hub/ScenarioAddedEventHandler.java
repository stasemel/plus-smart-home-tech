package ru.yandex.practicum.telemetry.service.handle.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.model.hub.HubEventType;
import ru.yandex.practicum.telemetry.model.hub.scenario.ScenarioAddedEvent;
import ru.yandex.practicum.telemetry.service.KafkaEventProducer;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEvent hubEvent) {
        ScenarioAddedEvent event = (ScenarioAddedEvent) hubEvent;
        List<DeviceActionAvro> actionAvroList = event.getActions().stream()
                .map(deviceAction -> {
                    return DeviceActionAvro.newBuilder()
                            .setSensorId(deviceAction.getSensorId())
                            .setType(ActionTypeAvro.valueOf(String.valueOf(deviceAction.getType())))
                            .setValue(deviceAction.getValue())
                            .build();
                }).collect(Collectors.toList());
        List<ScenarioConditionAvro> scenarioConditionAvroList = event.getConditions().stream()
                .map(scenarioCondition -> {
                    return ScenarioConditionAvro.newBuilder()
                            .setSensorId(scenarioCondition.getSensorId())
                            .setValue(scenarioCondition.getValue())
                            .setOperation(ConditionOperationAvro.valueOf(String.valueOf(scenarioCondition.getOperation())))
                            .setType(ConditionTypeAvro.valueOf(String.valueOf(scenarioCondition.getType())))
                            .build();
                })
                .collect(Collectors.toList());
        return ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setActions(actionAvroList)
                .setConditions(scenarioConditionAvroList)
                .build();
    }

    @Override
    public HubEventType getEventType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
