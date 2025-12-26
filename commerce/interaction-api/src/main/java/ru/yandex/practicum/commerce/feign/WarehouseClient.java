package ru.yandex.practicum.commerce.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.cart.CartDto;
import ru.yandex.practicum.commerce.dto.warhouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warhouse.AssemblyOrderDto;
import ru.yandex.practicum.commerce.dto.warhouse.BookingCartDto;
import ru.yandex.practicum.commerce.dto.warhouse.ShippedDto;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {
    @GetMapping("/address")
    AddressDto getAddress();

    @PostMapping("/check")
    BookingCartDto bookCart(@RequestBody @Valid CartDto cart);

    @PostMapping("/assembly")
    BookingCartDto assemblyProductForOrderFromShoppingCart(@RequestBody @Valid AssemblyOrderDto assemblyOrderDto);

    @PostMapping("/return")
    void returnProducts(@RequestBody Map<UUID, Long> returnedProducts);

    @PostMapping("/shipped")
    void shippedToDelivery(@RequestBody @Valid ShippedDto shippedDto);
}
