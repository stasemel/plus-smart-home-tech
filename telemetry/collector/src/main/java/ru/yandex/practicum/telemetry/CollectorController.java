package ru.yandex.practicum.telemetry;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.practicum.telemetry.hub.HubEvent;
import ru.yandex.practicum.telemetry.sensor.SensorEvent;

@Controller
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CollectorController {
    @PostMapping(path = "/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        log.info("Device add request. type = {}, hubId = {}", hubEvent.getType(),
                hubEvent.getHubId());
        System.out.println(hubEvent);
    }

    @PostMapping(path = "/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        log.info("Sensor event request. type={}, hubId ={}, id = {}",
                sensorEvent.getType(),
                sensorEvent.getHubId(),
                sensorEvent.getId());
        System.out.println(sensorEvent);
    }

}
