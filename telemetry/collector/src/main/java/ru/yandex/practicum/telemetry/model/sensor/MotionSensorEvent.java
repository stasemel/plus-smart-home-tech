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
public class MotionSensorEvent extends SensorEvent {
    ///Качество связи.
    @NotNull
    private int linkQuality;
    ///Наличие/отсутствие движения.
    @NotNull
    private boolean motion;
    ///Напряжение.
    @NotNull
    private int voltage;

    @Override
    @NotNull
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
