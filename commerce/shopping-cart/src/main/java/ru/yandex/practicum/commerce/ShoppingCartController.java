package ru.yandex.practicum.commerce;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.dto.cart.CartDto;
import ru.yandex.practicum.commerce.dto.cart.ChangeQuantityDto;
import ru.yandex.practicum.commerce.service.CartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/shopping-cart")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ShoppingCartController {
    private final CartService cartService;

    @GetMapping
    public CartDto getCart(@RequestParam("username") @NotBlank String userName
    ) {
        log.info("Get cart for userName {}", userName);
        CartDto cartDto = cartService.getCartByUserName(userName);
        log.info("Found cart for userName {}: {}", userName, cartDto);
        return cartDto;
    }

    @GetMapping("/{cartId}")
    public CartDto getCartById(@PathVariable UUID cartId) {
        log.info("Get cart by id cartId {}", cartId);
        CartDto cartDto = cartService.getCartById(cartId);
        log.info("Found cart by cartId {}: {}", cartId, cartDto);
        return cartDto;
    }

    @PutMapping
    public CartDto createCart(@RequestParam("username") @NotBlank String userName,
                              @RequestBody Map<UUID, Long> products) {
        log.info("Create Cart for userName {} with {}", userName, products);
        CartDto cartDto = cartService.create(userName, products);
        log.info("Cart created: {}", cartDto);
        return cartDto;
    }

    @DeleteMapping
    public void deleteCart(@RequestParam("username") @NotBlank String userName) {
        log.info("Delete cart for user {}", userName);
        cartService.deleteCart(userName);
        log.info("Delete ok");
    }

    @PostMapping("/remove")
    public CartDto removeItems(@RequestParam("username") @NotBlank String userName,
                               @RequestBody List<UUID> productIds) {
        log.info("Remove products {} for cart user {}", productIds, userName);
        CartDto cartDto = cartService.removeItems(userName, productIds);
        log.info("Remove ok");
        return cartDto;
    }

    @PostMapping("/change-quantity")
    public CartDto changeQuantity(@RequestParam("username") @NotBlank String userName,
                                  @RequestBody ChangeQuantityDto changeQuantityDto) {
        log.info("Change quantity for user {}'s cart to {}", userName, changeQuantityDto);
        CartDto cart = cartService.changeQuantity(userName, changeQuantityDto);
        log.info("User {}'s cart changed: {}", cart);
        return cart;
    }
}
