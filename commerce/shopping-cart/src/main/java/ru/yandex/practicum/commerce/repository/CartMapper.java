package ru.yandex.practicum.commerce.repository;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.cart.CartDto;
import ru.yandex.practicum.commerce.model.ShoppingCart;

@Component
@Mapper(componentModel = "spring")
public abstract class CartMapper {
    public abstract CartDto modelToDto(ShoppingCart shoppingCart);

    public abstract ShoppingCart dtoToModel(CartDto cartDto);
}
