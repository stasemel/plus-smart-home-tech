package ru.yandex.practicum.telemetry.service.grpc.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.service.KafkaEventProducer;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler {
    protected ScenarioAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected SpecificRecordBase mapToAvro(HubEventProto hubEvent) {
        ScenarioAddedEventProto event = hubEvent.getScenarioAdded();
        List<DeviceActionAvro> actionAvroList = event.getActionList().stream()
                .map(deviceAction -> {
                    return DeviceActionAvro.newBuilder()
                            .setSensorId(deviceAction.getSensorId())
                            .setType(ActionTypeAvro.valueOf(String.valueOf(deviceAction.getType())))
                            .setValue(deviceAction.getValue())
                            .build();
                }).collect(Collectors.toList());
        List<ScenarioConditionAvro> scenarioConditionAvroList = event.getConditionList().stream()
                .map(scenarioCondition -> {
                    Object value = null;
                    switch (scenarioCondition.getValueCase()) {
                        case INT_VALUE -> value = scenarioCondition.getIntValue();
                        case BOOL_VALUE -> value = scenarioCondition.getBoolValue();
                        case VALUE_NOT_SET -> value = null;
                        default -> value = null;
                    }
                    return ScenarioConditionAvro.newBuilder()
                            .setSensorId(scenarioCondition.getSensorId())
                            .setValue(value)
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
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }
}
