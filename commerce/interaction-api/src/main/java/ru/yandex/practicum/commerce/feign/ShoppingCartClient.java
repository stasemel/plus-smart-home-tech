package ru.yandex.practicum.commerce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.commerce.dto.cart.CartDto;

import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient {

    @GetMapping("/{cartId}")
    CartDto findByCartId(@PathVariable UUID cartId);
}
