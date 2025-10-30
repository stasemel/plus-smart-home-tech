package ru.yandex.practicum.telemetry.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClimateSensorEvent extends SensorEvent {
    ///Уровень температуры по шкале Цельсия.
    @NotNull
    private int temperatureC;
    ///Влажность.
    @NotNull
    private int humidity;
    ///Уровень CO2.
    @NotNull
    private int co2Level;

    @Override
    @NotNull
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
