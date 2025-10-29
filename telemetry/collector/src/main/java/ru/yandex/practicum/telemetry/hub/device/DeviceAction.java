package ru.yandex.practicum.telemetry.hub.device;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAction {
    ///Идентификатор датчика, связанного с действием.
    private String sensorId;
    ///Перечисление возможных типов действий при срабатывании условия активации сценария.
    private DeviceActionType type;
    ///Необязательное значение, связанное с действием.
    private int value;
}
