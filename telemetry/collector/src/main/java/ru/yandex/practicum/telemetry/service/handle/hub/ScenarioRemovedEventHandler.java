package ru.yandex.practicum.telemetry.service.handle.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.model.hub.HubEventType;
import ru.yandex.practicum.telemetry.model.hub.scenario.ScenarioRemovedEvent;
import ru.yandex.practicum.telemetry.service.KafkaEventProducer;

@Component
public class ScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {
    public ScenarioRemovedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected ScenarioRemovedEventAvro mapToAvro(HubEvent hubEvent) {
        ScenarioRemovedEvent event = (ScenarioRemovedEvent) hubEvent;
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
    }

    @Override
    public HubEventType getEventType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
