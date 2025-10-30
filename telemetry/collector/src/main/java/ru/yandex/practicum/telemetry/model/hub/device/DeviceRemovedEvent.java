package ru.yandex.practicum.telemetry.model.hub.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.model.hub.HubEventType;

@Builder
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRemovedEvent extends HubEvent {
    ///Идентификатор удаленного устройства.
    @NotBlank
    private String id;

    @Override
    @NotNull
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
