package ru.yandex.practicum.telemetry.model.hub.scenario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.model.hub.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class ScenarioRemovedEvent extends HubEvent {
    ///Название удаленного сценария. Должно содержать не менее 3 символов.
    @NotBlank
    @Size(min = 3, message = "name должно содержать не менее 3 символов")
    String name;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
