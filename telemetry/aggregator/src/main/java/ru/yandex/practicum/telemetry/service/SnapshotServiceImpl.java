package ru.yandex.practicum.telemetry.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class SnapshotServiceImpl implements SnapshotService {
    private final Map<String, SensorsSnapshotAvro> sensorsSnapshot = new HashMap<>();

    @Override
    public Optional<SensorsSnapshotAvro> updateSnapshot(SensorEventAvro event) {
        log.info("Start updateSnapshot by event {}", event);
        SensorsSnapshotAvro snapshot = sensorsSnapshot.get(event.getHubId());
        if (snapshot == null) {
            snapshot = SensorsSnapshotAvro.newBuilder()
                    .setHubId(event.getHubId())
                    .setSensorsState(new HashMap<>())
                    .setTimestamp(event.getTimestamp())
                    .build();
            sensorsSnapshot.put(event.getHubId(), snapshot);
        }
        SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());
        if ((oldState != null) &&
                ((oldState.getTimestamp().isAfter(event.getTimestamp()) ||
                        oldState.getData().equals(event.getPayload())))) {
            return Optional.empty();
        }
        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
        snapshot.getSensorsState().put(event.getId(), newState);
        snapshot.setTimestamp(event.getTimestamp());
        log.info("Done updateSnapshot {}", snapshot);
        return Optional.of(snapshot);
    }
}
