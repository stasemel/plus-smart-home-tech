package ru.yandex.practicum.telemetry.model.hub.scenario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.model.hub.HubEventType;
import ru.yandex.practicum.telemetry.model.hub.device.DeviceAction;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {

    ///Название добавленного сценария. Должно содержать не менее 3 символов.
    @NotBlank
    @Size(min = 3, message = "name должно содержать не менее 3 символов")
    private String name;
    ///Список условий, которые связаны со сценарием. Не может быть пустым.
    @NotNull
    private List<ScenarioCondition> conditions;
    ///Список действий, которые должны быть выполнены в рамках сценария. Не может быть пустым.
    @NotNull
    private List<DeviceAction> actions;

    @Override
    @NotNull
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
