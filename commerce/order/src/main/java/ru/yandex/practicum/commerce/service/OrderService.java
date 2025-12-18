package ru.yandex.practicum.commerce.service;

import ru.yandex.practicum.commerce.dto.order.OrderCreateDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.OrderReturnDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDto createOrder(OrderCreateDto orderCreateDto);

    List<OrderDto> findAllByUsername(String username);

    OrderDto returnOrder(OrderReturnDto orderReturnDto);

    OrderDto paymentOrder(UUID orderId);

    OrderDto paymentOrderFailed(UUID orderId);

    OrderDto deliveryOrder(UUID orderId);

    OrderDto deliveryOrderFailed(UUID orderId);

    OrderDto completedOrder(UUID orderId);

    OrderDto assemblyOrder(UUID orderId);

    OrderDto assemblyOrderFailed(UUID orderId);

    OrderDto calculateTotal(UUID orderId);

    OrderDto getOrderById(UUID orderId);
}
