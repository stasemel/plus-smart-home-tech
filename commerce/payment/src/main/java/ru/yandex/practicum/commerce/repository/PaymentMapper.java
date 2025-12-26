package ru.yandex.practicum.commerce.repository;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.model.Payment;

@Component
@Mapper(componentModel = "spring")
public abstract class PaymentMapper {
    public abstract PaymentDto modelToDto(Payment payment);
}
