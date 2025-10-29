package ru.yandex.practicum.telemetry;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.telemetry.hub.device.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.hub.device.DeviceType;
import ru.yandex.practicum.telemetry.sensor.LightSensorEvent;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CollectorController.class)
class CollectorControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    void addDeviceLightSensorToHub() throws Exception {
        DeviceAddedEvent addedEvent = DeviceAddedEvent.builder()
                .id("device-1")
                .deviceType(DeviceType.LIGHT_SENSOR)
                .build();
        addedEvent.setHubId("hub-1");
        mvc.perform(post("/events/hubs")
                        .content(mapper.writeValueAsString(addedEvent))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addLightSensorEventToHub() throws Exception {
        LightSensorEvent sensorEvent = LightSensorEvent.builder()
                .linkQuality(10)
                .luminosity(5)
                .build();
        sensorEvent.setHubId("hub-1");
        sensorEvent.setId("light-sensor-1");
        mvc.perform(post("/events/sensors")
                        .content(mapper.writeValueAsString(sensorEvent))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}