package ru.yandex.practicum.commerce;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.dto.cart.CartDto;
import ru.yandex.practicum.commerce.dto.warhouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warhouse.AssemblyOrderDto;
import ru.yandex.practicum.commerce.dto.warhouse.BookingCartDto;
import ru.yandex.practicum.commerce.dto.warhouse.ProductQuantityDto;
import ru.yandex.practicum.commerce.dto.warhouse.ShippedDto;
import ru.yandex.practicum.commerce.dto.warhouse.WarehouseDto;
import ru.yandex.practicum.commerce.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/warehouse")
@RequiredArgsConstructor
@Slf4j
@Validated
public class WarehouseCollector {
    private final WarehouseService warehouseService;

    @PutMapping
    public void putNewProduct(@RequestBody @Valid WarehouseDto warehouseDto) {
        log.info("Put new product {}", warehouseDto);
        warehouseService.putNewProduct(warehouseDto);
        log.info("Put new product {} done", warehouseDto);
    }

    @PostMapping("/check")
    public BookingCartDto bookCart(@RequestBody @Valid CartDto cart) {
        log.info("Check cart {}", cart);
        BookingCartDto bookedCartDto = warehouseService.bookCart(cart);
        log.info("Booked cart: {}", bookedCartDto);
        return bookedCartDto;
    }

    @PostMapping("/add")
    public void addQuantity(@RequestBody @Valid ProductQuantityDto productQuantityDto) {
        log.info("Add quantity {}", productQuantityDto);
        warehouseService.addQuantity(productQuantityDto);
        log.info("Add quantity {} done", productQuantityDto);
    }

    @PostMapping("/shipped")
    public void shippedToDelivery(@RequestBody @Valid ShippedDto shippedDto) {
        log.info("Shipped order {}", shippedDto);
        warehouseService.shippedOrder(shippedDto);
        log.info("Shipped order {} done", shippedDto);
    }

    @GetMapping("/address")
    public AddressDto getAddress() {
        log.info("Get address");
        AddressDto addressDto = warehouseService.getAddress();
        log.info("Address: {}", addressDto);
        return addressDto;
    }

    @PostMapping("/assembly")
    public BookingCartDto assemblyProductForOrderFromShoppingCart(@RequestBody @Valid AssemblyOrderDto assemblyOrderDto) {
        log.info("Assembly products from cart {}", assemblyOrderDto);
        BookingCartDto bookingCartDto = warehouseService.assemblyOrder(assemblyOrderDto);
        log.info("Assembled products from cart {}", assemblyOrderDto);
        return bookingCartDto;
    }

    @PostMapping("/return")
    public void returnProducts(@RequestBody Map<UUID, Long> returnedProducts) {
        log.info("Return products {}", returnedProducts);
        warehouseService.returnProducts(returnedProducts);
        log.info("Return products {} done", returnedProducts);
    }
}
