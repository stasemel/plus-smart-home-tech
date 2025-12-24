package ru.yandex.practicum.commerce.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.cart.CartDto;
import ru.yandex.practicum.commerce.dto.warhouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warhouse.AssemblyOrderDto;
import ru.yandex.practicum.commerce.dto.warhouse.BookingCartDto;
import ru.yandex.practicum.commerce.dto.warhouse.ProductQuantityDto;
import ru.yandex.practicum.commerce.dto.warhouse.ShippedDto;
import ru.yandex.practicum.commerce.dto.warhouse.WarehouseDto;

import java.util.Map;
import java.util.UUID;

@Service
public interface WarehouseService {
    void putNewProduct(WarehouseDto warehouseDto);

    BookingCartDto bookCart(CartDto cart);

    void addQuantity(ProductQuantityDto productQuantityDto);

    AddressDto getAddress();

    BookingCartDto assemblyOrder(AssemblyOrderDto assemblyOrderDto);

    void shippedOrder(ShippedDto shippedDto);

    void returnProducts(Map<UUID, Long> returnedProducts);
}
