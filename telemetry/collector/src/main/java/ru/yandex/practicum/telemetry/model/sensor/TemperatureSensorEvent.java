package ru.yandex.practicum.telemetry.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class TemperatureSensorEvent extends SensorEvent {
    ///Температура в градусах Цельсия.
    @NotNull
    int temperatureC;
    ///Температура в градусах Фаренгейта.
    @NotNull
    int temperatureF;

    @Override
    @NotNull
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
