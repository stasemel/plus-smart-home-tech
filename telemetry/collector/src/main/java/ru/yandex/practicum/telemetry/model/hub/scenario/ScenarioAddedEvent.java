package ru.yandex.practicum.telemetry.model.hub.scenario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
import ru.yandex.practicum.telemetry.model.hub.device.DeviceAction;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class ScenarioAddedEvent extends HubEvent {

    ///Название добавленного сценария. Должно содержать не менее 3 символов.
    @NotBlank
    @Size(min = 3, message = "name должно содержать не менее 3 символов")
    String name;
    ///Список условий, которые связаны со сценарием. Не может быть пустым.
    @NotNull
    List<ScenarioCondition> conditions;
    ///Список действий, которые должны быть выполнены в рамках сценария. Не может быть пустым.
    @NotNull
    List<DeviceAction> actions;

    @Override
    @NotNull
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
