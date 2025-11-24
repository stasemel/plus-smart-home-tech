package ru.yandex.practicum.telemetry.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.model.Action;
import ru.yandex.practicum.telemetry.model.Condition;
import ru.yandex.practicum.telemetry.model.Scenario;
import ru.yandex.practicum.telemetry.repository.ScenarioRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro.LUMINOSITY;
import static ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro.MOTION;
import static ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro.SWITCH;
import static ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro.TEMPERATURE;

@Service
@Slf4j
@Transactional(readOnly = true)
public class SnapshotService {
    private final ScenarioRepository scenarioRepository;
    private final HubRouterControllerBlockingStub hubRouterClient;

    public SnapshotService(ScenarioRepository scenarioRepository,
                           @GrpcClient("hub-router")
                           HubRouterControllerBlockingStub hubRouterClient) {
        this.scenarioRepository = scenarioRepository;
        this.hubRouterClient = hubRouterClient;
    }

    public void analyze(SensorsSnapshotAvro snapshotAvro) {
        log.info("Analyze snapshot {}", snapshotAvro);
        String hubId = snapshotAvro.getHubId();
        List<Scenario> list = scenarioRepository.findByHubId(hubId);
        if (list.isEmpty()) return;
        list.forEach(scenario -> {
            if (checkAllConditions(scenario.getConditions(), snapshotAvro)) {
                executeActions(scenario);
            }
        });
    }

    private boolean checkAllConditions(Map<String, Condition> conditions, SensorsSnapshotAvro snapshotAvro) {
        return conditions.entrySet().
                stream().
                allMatch(entry -> checkCondition(entry.getKey(), entry.getValue(), snapshotAvro));
    }

    private boolean checkCondition(String sensorId, Condition condition, SensorsSnapshotAvro snapshotAvro) {
        SensorStateAvro sensorStateAvro = snapshotAvro.getSensorsState().get(sensorId);
        if (sensorStateAvro == null) {
            return false;
        }
        return checkSensorCondition(condition, sensorStateAvro.getData());
    }

    private boolean checkSensorCondition(Condition condition, Object stateData) {
        if ((condition == null) || (stateData == null)) return false;
        return switch (stateData) {
            case ClimateSensorAvro data -> checkClimateCondition(condition, data);
            case LightSensorAvro data -> checkLightCondition(condition, data);
            case MotionSensorAvro data -> checkMotionCondition(condition, data);
            case TemperatureSensorAvro data -> checkTemperatureCondition(condition, data);
            case SwitchSensorAvro data -> checkSwitchCondition(condition, data);
            default -> false;
        };
    }

    private boolean checkClimateCondition(Condition condition, ClimateSensorAvro data) {
        return switch (condition.getType()) {
            case TEMPERATURE -> condition.check(data.getTemperatureC());
            case CO2LEVEL -> condition.check(data.getCo2Level());
            case HUMIDITY -> condition.check(data.getHumidity());
            default -> false;
        };
    }

    private boolean checkLightCondition(Condition condition, LightSensorAvro data) {
        return condition.getType().equals(LUMINOSITY) && condition.check(data.getLuminosity());
    }

    private boolean checkMotionCondition(Condition condition, MotionSensorAvro data) {
        return condition.getType().equals(MOTION) && condition.check(data.getMotion() ? 1 : 0);
    }

    private boolean checkTemperatureCondition(Condition condition, TemperatureSensorAvro data) {
        return condition.getType().equals(TEMPERATURE) && condition.check(data.getTemperatureC());
    }

    private boolean checkSwitchCondition(Condition condition, SwitchSensorAvro data) {
        return condition.getType().equals(SWITCH) && condition.check(data.getState() ? 1 : 0);
    }

    private void executeActions(Scenario scenario) {

        Timestamp timestamp = createTimestamp();
        scenario.getActions().entrySet()
                .forEach(entry -> executeAction(entry.getValue(), entry.getKey(), scenario, timestamp));

    }

    private void executeAction(Action action, String sensorId, Scenario scenario, Timestamp timestamp) {
        try {
            log.debug("executeAction 0 !!!!!! {} sensorId {} scenario {} timestamp {}", action, sensorId, scenario, timestamp);
            var deviceAction = buildDeviceAction(action, sensorId);
            log.debug("executeAction 1 !!!!!! deviceAction = {}", deviceAction);
            var request = DeviceActionRequest.newBuilder()
                    .setHubId(scenario.getHubId())
                    .setScenarioName(scenario.getName())
                    .setAction(deviceAction)
                    .setTimestamp(timestamp)
                    .build();
            log.debug("executeAction 2 !!!!!! request {}", request);
            hubRouterClient.handleDeviceAction(request);
            log.debug("executeAction 3 !!!!!! done");
        } catch (Exception e) {
            log.error("executeAction action {}, sensorId {}, hub {}"
                    , action.getType(), sensorId, scenario.getHubId(), e);
        }
    }

    private DeviceActionProto buildDeviceAction(Action action, String sensorId) {
        var builder = DeviceActionProto.newBuilder()
                .setSensorId(sensorId)
                .setType(mapActionType(action.getType()));

        if (action.getType().equals(ActionTypeAvro.SET_VALUE)) {
            builder.setValue(action.getValue());
        }

        return builder.build();
    }

    private ActionTypeProto mapActionType(ActionTypeAvro avro) {
        return ActionTypeProto.valueOf(avro.name());
    }

    private Timestamp createTimestamp() {
        Instant ts = Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(ts.getEpochSecond())
                .setNanos(ts.getNano())
                .build();
    }
}
