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
public class ClimateSensorEvent extends SensorEvent {
    ///Уровень температуры по шкале Цельсия.
    @NotNull
    int temperatureC;
    ///Влажность.
    @NotNull
    int humidity;
    ///Уровень CO2.
    @NotNull
    int co2Level;

    @Override
    @NotNull
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
