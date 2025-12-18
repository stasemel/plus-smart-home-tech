package ru.yandex.practicum.commerce.feign;

import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.commerce.dto.order.OrderDto;

import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient {
    @GetMapping("/{orderId}")
    OrderDto getOrderById(@PathVariable @NotNull UUID orderId);
}
