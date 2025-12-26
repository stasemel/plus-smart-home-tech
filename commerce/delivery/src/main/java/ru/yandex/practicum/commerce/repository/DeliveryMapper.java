package ru.yandex.practicum.commerce.repository;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.model.Delivery;

@Component
@Mapper(componentModel = "spring")
public abstract class DeliveryMapper {
    @Mapping(target = "deliveryWeight", ignore = true)
    @Mapping(target = "deliveryVolume", ignore = true)
    @Mapping(target = "fragile", ignore = true)
    @Mapping(target = "deliveryPrice", ignore = true)
    public abstract Delivery dtoToModel(DeliveryDto dto);

    public abstract DeliveryDto modelToDto(Delivery model);

}
