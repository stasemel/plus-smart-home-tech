package ru.yandex.practicum.commerce.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.cart.CartDto;
import ru.yandex.practicum.commerce.dto.cart.ChangeQuantityDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service public interface CartService {
    CartDto getCartByUserName(String userName);

    CartDto create(String userName, Map<UUID, Integer> products);

    void deleteCart(String userName);

    CartDto removeItems(String userName, List<UUID> productIds);

    CartDto changeQuantity(String userName, ChangeQuantityDto changeQuantityDto);
}
