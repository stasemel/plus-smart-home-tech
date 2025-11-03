package ru.yandex.practicum.telemetry;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.model.hub.HubEventType;
import ru.yandex.practicum.telemetry.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.model.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.service.handle.hub.HubEventHandler;
import ru.yandex.practicum.telemetry.service.handle.sensor.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/events")
@Slf4j
@Validated
public class EventController {

    private final Map<HubEventType, HubEventHandler> hubEventHandlers;
    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;

    @Autowired
    public EventController(Set<HubEventHandler> hubEventHandlers, Set<SensorEventHandler> sensorEventHandlers) {
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getEventType, Function.identity()));
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getEventType, Function.identity()));
    }

    @PostMapping(path = "/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        log.info("Device add request. type = {}, hubId = {}", hubEvent.getType(),
                hubEvent.getHubId());
        HubEventHandler hubEventHandler = hubEventHandlers.get(hubEvent.getType());
        if (hubEventHandler == null) {
            throw new IllegalArgumentException(String.format("Не могу найти обработчик события %s", hubEvent.getType()));
        }
        hubEventHandler.handle(hubEvent);
    }

    @PostMapping(path = "/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        log.info("Sensor event request. type={}, hubId ={}, id = {}",
                sensorEvent.getType(),
                sensorEvent.getHubId(),
                sensorEvent.getId());
        SensorEventHandler sensorEventHandler = sensorEventHandlers.get(sensorEvent.getType());
        if (sensorEventHandler == null) {
            throw new IllegalArgumentException(String.format("Не могу найти обработчик события %s", sensorEvent.getType()));
        }
        sensorEventHandler.handle(sensorEvent);
    }

}
