package ru.yandex.practicum.commerce.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "cart")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class CartConfig {
    boolean isUsernameUnique;
}
