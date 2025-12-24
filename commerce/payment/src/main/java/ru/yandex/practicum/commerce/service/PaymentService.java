package ru.yandex.practicum.commerce.service;

import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentDto createPayment(OrderDto orderDto);

    BigDecimal calculateTotalCost(OrderDto orderDto);

    void refundPayment(UUID paymentId);

    BigDecimal getProductCost(OrderDto orderDto);

    void failedPayment(UUID paymentId);
}
