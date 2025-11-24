package ru.yandex.practicum.telemetry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.telemetry.model.Sensor;
import ru.yandex.practicum.telemetry.repository.SensorRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorService {
    public final SensorRepository sensorRepository;

    @Transactional
    public void deviceRemoved(String hubId, String id) {
        log.info("Delete sensor {} from hub {}", id, hubId);
        Optional<Sensor> optionalSensor = sensorRepository.findByIdAndHubId(id, hubId);
        if (optionalSensor.isPresent()) {
            sensorRepository.delete(optionalSensor.get());
        }
    }

    @Transactional
    public void addDevice(String hubId, DeviceAddedEventAvro deviceAddedEventAvro) {
        log.info("Add device {} to hub {}", deviceAddedEventAvro.getId(), hubId);
        if (sensorRepository.existsByIdInAndHubId(List.of(deviceAddedEventAvro.getId()), hubId)) {
            log.info("Device {} is exist in hub {}", deviceAddedEventAvro.getId(), hubId);
        }
        Sensor sensor = new Sensor();
        sensor.setHubId(hubId);
        sensor.setId(deviceAddedEventAvro.getId());
        sensorRepository.save(sensor);
    }
}
