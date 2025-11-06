package ru.yandex.practicum.telemetry.model.sensor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = ErrorSensorEvent.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClimateSensorEvent.class, name = "CLIMATE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = LightSensorEvent.class, name = "LIGHT_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = MotionSensorEvent.class, name = "MOTION_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = "TEMPERATURE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = SwitchSensorEvent.class, name = "SWITCH_SENSOR_EVENT")
})
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public abstract class SensorEvent {
    ///Идентификатор события датчика.
    @NotBlank
    String id;
    ///Идентификатор хаба, связанного с событием.
    @NotBlank
    String hubId;
    ///Временная метка события. По умолчанию устанавливается текущее время.
    Instant timestamp = Instant.now();

    // абстрактный метод, который должен быть определён в конкретных реализациях
    @NotNull
    public abstract SensorEventType getType();
}