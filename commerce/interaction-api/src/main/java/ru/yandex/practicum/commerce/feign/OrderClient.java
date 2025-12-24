package ru.yandex.practicum.commerce.feign;

import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.order.OrderDto;

import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient {
    @GetMapping("/{orderId}")
    OrderDto getOrderById(@PathVariable @NotNull UUID orderId);

    @PostMapping("/delivery")
    OrderDto deliveryOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto deliveryOrderFailed(@RequestBody @NotNull UUID orderId);

    @PostMapping("/completed")
    OrderDto completedOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/payment")
    OrderDto paymentOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto paymentOrderFailed(@RequestBody @NotNull UUID orderId);
}
