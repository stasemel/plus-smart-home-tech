package ru.yandex.practicum.commerce.feign;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;

import java.math.BigDecimal;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient {
    @PutMapping
    public DeliveryDto createDelivery(@RequestBody @Valid DeliveryDto deliveryDto);

    @PostMapping("/cost")
    public BigDecimal costDelivery(@RequestBody @NotNull OrderDto order);
}
