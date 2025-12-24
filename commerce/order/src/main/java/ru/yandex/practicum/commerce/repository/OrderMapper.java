package ru.yandex.practicum.commerce.repository;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.order.OrderCreateDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.OrderState;
import ru.yandex.practicum.commerce.dto.warhouse.AddressDto;
import ru.yandex.practicum.commerce.model.Order;

@Component
@Mapper(componentModel = "spring")
public abstract class OrderMapper {
    @Mapping(target = "userName", ignore = true)
    public abstract Order dtoToModel(OrderDto dto);


    public abstract OrderDto modelToDto(Order model);

    public Order dtoToModel(OrderCreateDto orderCreateDto) {
        return Order.builder()
                .shoppingCartId(orderCreateDto.getShoppingCart().getShoppingCartId())
                .products(orderCreateDto.getShoppingCart().getProducts())
                .state(OrderState.NEW)
                .build();
    }
}
