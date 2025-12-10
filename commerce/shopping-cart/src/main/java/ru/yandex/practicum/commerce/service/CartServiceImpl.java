package ru.yandex.practicum.commerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.cart.CartDto;
import ru.yandex.practicum.commerce.dto.cart.ChangeQuantityDto;
import ru.yandex.practicum.commerce.dto.warhouse.BookingCartDto;
import ru.yandex.practicum.commerce.exception.CartNotFoundException;
import ru.yandex.practicum.commerce.exception.ProductNotFoundInCartException;
import ru.yandex.practicum.commerce.feign.ShoppingStoreClient;
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
    private final ShoppingStoreClient shoppingStoreClient;
    private final WarehouseClient warehouseClient;

    @Override
    public CartDto getCartByUserName(String userName) {
        ShoppingCart cart = getCart(userName);
        return cartMapper.modelToDto(cart);
    }

    @Override
    public CartDto create(String userName, Map<UUID, Long> products) {
        cartPrepare(userName);
        ShoppingCart cart = ShoppingCart.builder().userName(userName).build();
        final HashMap<UUID, String> addedError = new HashMap<>();
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();
            if ((quantity == null) || (quantity < 0)) {
                addedError.put(productId, String.format("Quantity of product %s must be greater than 0 (%n)",
                        productId,
                        quantity));
                continue;
            }
            cart.addItem(productId, quantity);
        }
        validateCartInWarehouse(cart);
        return cartMapper.modelToDto(saveCartTransaction(cart));
    }

    protected void cartPrepare(String userName) {
        ShoppingCart cart = cartRepository.findByUserName(userName);
        if (cart == null) return;
        deleteCartTransaction(cart);
    }

    @Transactional
    protected void deleteCartTransaction(ShoppingCart cart) {
        cartRepository.delete(cart);
    }

    @Override
    public void deleteCart(String userName) {
        ShoppingCart cart = cartRepository.findByUserName(userName);
        if (cart != null) deleteCartTransaction(cart);
    }

    @Override
    public CartDto removeItems(String userName, List<UUID> productIds) {
        ShoppingCart cart = getCart(userName);
        for (UUID productId : productIds) {
            if (!cart.getProducts().containsKey(productId)) {
                throw new ProductNotFoundInCartException(String.format("Product %s not found fro user %s",
                        productId,
                        userName));
            }
            cart.getProducts().remove(productId);
        }
        return cartMapper.modelToDto(saveCartTransaction(cart));
    }

    @Override
    public CartDto changeQuantity(String userName, ChangeQuantityDto changeQuantityDto) {
        ShoppingCart cart = cartRepository.findByUserName(userName);
        if (!cart.getProducts().containsKey(changeQuantityDto.getProductId())) {
            throw new ProductNotFoundInCartException(String.format("Product %s not found fro user %s",
                    changeQuantityDto.getProductId(),
                    userName));
        }
        cart.getProducts().put(changeQuantityDto.getProductId(), changeQuantityDto.getNewQuantity());
        validateCartInWarehouse(cart);
        return cartMapper.modelToDto(saveCartTransaction(cart));
    }

    private boolean validateCartInWarehouse(ShoppingCart cart) {
        CartDto cartDto = cartMapper.modelToDto(cart);
        BookingCartDto bookingCartDto = warehouseClient.bookCart(cartDto);
        return true;
    }

    private ShoppingCart getCart(String userName) {
        ShoppingCart cart = cartRepository.findByUserName(userName);
        if ((cart == null) || (cart.getProducts() == null) || (cart.getProducts().isEmpty())) {
            throw new CartNotFoundException(String.format("Not found cart for user %s", userName));
        }
        return cart;
    }

    @Transactional
    protected ShoppingCart saveCartTransaction(ShoppingCart cart) {
        return cartRepository.save(cart);
    }
}
