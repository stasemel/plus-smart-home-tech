package ru.yandex.practicum.commerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.cart.CartDto;
import ru.yandex.practicum.commerce.dto.cart.ChangeQuantityDto;
import ru.yandex.practicum.commerce.dto.warhouse.BookingCartDto;
import ru.yandex.practicum.commerce.exception.CartNotFoundException;
import ru.yandex.practicum.commerce.exception.ProductNotFoundInCartException;
import ru.yandex.practicum.commerce.feign.WarehouseClient;
import ru.yandex.practicum.commerce.model.ShoppingCart;
import ru.yandex.practicum.commerce.repository.CartMapper;
import ru.yandex.practicum.commerce.repository.CartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final WarehouseClient warehouseClient;

    @Override
    public CartDto getCartByUserName(String userName) {
        ShoppingCart cart = getActiveCart(userName);
        return cartMapper.modelToDto(cart);
    }

    @Override
    @Transactional
    public CartDto create(String userName, Map<UUID, Long> products) {
        Map<UUID, Long> validatedProducts = new HashMap<>();
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();
            if (quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be > 0 for product " + productId);
            }
            validatedProducts.put(productId, quantity);
        }

        CartDto tempCartDto = CartDto.builder()
                .userName(userName)
                .products(validatedProducts)
                .build();

        BookingCartDto bookingResponse = warehouseClient.bookCart(tempCartDto);
        if (bookingResponse == null) {
            throw new IllegalStateException("Warehouse returned null response");
        }

        List<ShoppingCart> activeCarts = cartRepository.findAllByUserNameAndActivity(userName, true);
        for (ShoppingCart cart : activeCarts) {
            cart.setActivity(false);
        }
        if (!activeCarts.isEmpty()) {
            cartRepository.saveAll(activeCarts);
        }

        ShoppingCart newCart = ShoppingCart.builder()
                .userName(userName)
                .activity(true)
                .products(validatedProducts)
                .build();

        ShoppingCart saved = cartRepository.save(newCart);
        return cartMapper.modelToDto(saved);
    }

    @Override
    @Transactional
    public void deleteCart(String userName) {
        List<ShoppingCart> carts = cartRepository.findAllByUserNameAndActivity(userName, true);
        for (ShoppingCart cart : carts) {
            cart.setActivity(false);
        }
        if (!carts.isEmpty()) {
            cartRepository.saveAll(carts);
        }
    }

    @Override
    @Transactional
    public CartDto removeItems(String userName, List<UUID> productIds) {
        ShoppingCart cart = getActiveCart(userName);
        for (UUID productId : productIds) {
            if (!cart.getProducts().containsKey(productId)) {
                throw new ProductNotFoundInCartException(
                        String.format("Product %s not found for user %s", productId, userName)
                );
            }
            cart.getProducts().remove(productId);
        }

        if (!cart.getProducts().isEmpty()) {
            validateCartInWarehouse(cart);
        }

        ShoppingCart saved = cartRepository.save(cart);
        return cartMapper.modelToDto(saved);
    }

    @Override
    @Transactional
    public CartDto changeQuantity(String userName, ChangeQuantityDto changeDto) {
        ShoppingCart cart = getActiveCart(userName);
        UUID productId = changeDto.getProductId();
        Long newQuantity = changeDto.getNewQuantity();

        if (!cart.getProducts().containsKey(productId)) {
            throw new ProductNotFoundInCartException(
                    String.format("Product %s not found for user %s", productId, userName)
            );
        }

        if (newQuantity <= 0) {
            cart.getProducts().remove(productId);
        } else {
            cart.getProducts().put(productId, newQuantity);
        }

        if (!cart.getProducts().isEmpty()) {
            validateCartInWarehouse(cart);
        }

        ShoppingCart saved = cartRepository.save(cart);
        return cartMapper.modelToDto(saved);
    }

    @Override
    public CartDto getCartById(UUID cartId) {
        return cartRepository.findById(cartId)
                .map(cartMapper::modelToDto)
                .orElseThrow(() -> new CartNotFoundException("Cart with id " + cartId + " not found"));
    }


    private void validateCartInWarehouse(ShoppingCart cart) {
        CartDto cartDto = cartMapper.modelToDto(cart);
        warehouseClient.bookCart(cartDto);
    }

    private ShoppingCart getActiveCart(String userName) {
        return cartRepository.findByUserNameAndActivity(userName, true)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user " + userName));
    }
}