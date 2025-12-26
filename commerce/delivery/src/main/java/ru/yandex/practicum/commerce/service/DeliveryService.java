package ru.yandex.practicum.commerce.service;

import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto createDelivery(DeliveryDto deliveryDto);

    void successfulDelivery(UUID deliveryId);

    void pickedDelivery(UUID deliveryId);

    void failedDelivery(UUID deliveryId);

    BigDecimal costDelivery(OrderDto order);
}
