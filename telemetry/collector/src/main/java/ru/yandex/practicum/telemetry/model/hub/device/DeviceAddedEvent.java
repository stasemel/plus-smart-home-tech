package ru.yandex.practicum.telemetry.model.hub.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.model.hub.HubEventType;

@Builder
@Getter
@Setter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class DeviceAddedEvent extends HubEvent {
    ///Идентификатор добавленного устройства.
    @NotBlank
    String id;
    ///Тип устройства
    @NotNull
    DeviceType deviceType;

    @Override
    @NotNull
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
