package ru.yandex.practicum.telemetry.model.hub.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAction {
    ///Идентификатор датчика, связанного с действием.
    @NotBlank
    private String sensorId;
    ///Перечисление возможных типов действий при срабатывании условия активации сценария.
    @NotNull
    private DeviceActionType type;
    ///Необязательное значение, связанное с действием.
    private int value;
}
