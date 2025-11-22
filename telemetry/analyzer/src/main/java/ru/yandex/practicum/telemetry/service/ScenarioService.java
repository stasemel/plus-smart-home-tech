package ru.yandex.practicum.telemetry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.model.Action;
import ru.yandex.practicum.telemetry.model.Condition;
import ru.yandex.practicum.telemetry.model.Scenario;
import ru.yandex.practicum.telemetry.repository.ActionRepository;
import ru.yandex.practicum.telemetry.repository.ConditionRepository;
import ru.yandex.practicum.telemetry.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.repository.SensorRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioService {
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;

    @Transactional
    public void deleteScenario(String hubId, String name) {
        log.info("Delete scenario {} from hub {}", name, hubId);
        Optional<Scenario> optionalScenario = scenarioRepository.findByHubIdAndName(hubId, name);
        if (optionalScenario.isPresent()) {
            Scenario scenario = optionalScenario.get();
            conditionRepository.deleteAll(scenario.getConditions().values());
            actionRepository.deleteAll(scenario.getActions().values());
            scenarioRepository.delete(scenario);
        }
    }

    @Transactional
    public Scenario addScenario(String hubId, ScenarioAddedEventAvro scenarioAddedEventAvro) {
        log.info("Add scenario {} in hub {}", scenarioAddedEventAvro, hubId);
        if (!validateSensors(hubId, scenarioAddedEventAvro)) {
            throw new IllegalArgumentException("Unknown sensors in scenario");
        }
        Scenario scenario = findOrCreateScenario(hubId, scenarioAddedEventAvro.getName());
        clearScenario(scenario);

        addActionsToScenario(scenario, scenarioAddedEventAvro.getActions());
        addConditionsToScenario(scenario, scenarioAddedEventAvro.getConditions());
        actionRepository.saveAll(scenario.getActions().values());
        conditionRepository.saveAll(scenario.getConditions().values());
        return scenarioRepository.save(scenario);

    }

    private void addConditionsToScenario(Scenario scenario, Iterable<ScenarioConditionAvro> eventConditions) {
        for (ScenarioConditionAvro eventCondition : eventConditions) {
            Condition condition = new Condition();
            condition.setType(eventCondition.getType());
            condition.setOperation(eventCondition.getOperation());
            condition.setValue(mapValue(eventCondition.getValue()));
            scenario.addCondition(eventCondition.getSensorId(), condition);
        }
    }

    private Integer mapValue(Object value) {
        if (value != null) {
            if (value instanceof Integer i) return i;
            if (value instanceof Boolean b) return b ? 1 : 0;
        }
        return null;
    }

    private void addActionsToScenario(Scenario scenario, Iterable<DeviceActionAvro> eventActions) {
        for (DeviceActionAvro eventAction : eventActions) {
            Action action = new Action();
            action.setType(eventAction.getType());
            if (eventAction.getType().equals(ActionTypeAvro.SET_VALUE)) {
                action.setValue(mapValue(eventAction.getValue()));
            }
            scenario.addAction(eventAction.getSensorId(), action);
        }
    }

    private void clearScenario(Scenario scenario) {
        Map<String, Condition> conditions = scenario.getConditions();
        Map<String, Action> actions = scenario.getActions();

        if (!conditions.isEmpty()) {
            conditionRepository.deleteAll(conditions.values());
            scenario.getConditions().clear();
        }

        if (!actions.isEmpty()) {
            actionRepository.deleteAll(actions.values());
            scenario.getActions().clear();
        }
    }

    private Scenario findOrCreateScenario(String hubId, String name) {
        return scenarioRepository.findByHubIdAndName(hubId, name).orElseGet(() -> {
            Scenario scenario = new Scenario();
            scenario.setName(name);
            scenario.setHubId(hubId);
            return scenario;
        });
    }

    private boolean validateSensors(String hubId, ScenarioAddedEventAvro scenarioEvent) {
        final Set<String> sensors = new HashSet<>();
        scenarioEvent.getActions().forEach(action -> sensors.add(action.getSensorId()));
        scenarioEvent.getConditions().forEach(condition -> sensors.add(condition.getSensorId()));
        return sensorRepository.existsByIdInAndHubId(sensors, hubId);
    }
}
