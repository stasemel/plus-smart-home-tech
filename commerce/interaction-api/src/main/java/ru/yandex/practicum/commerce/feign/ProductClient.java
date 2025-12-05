package ru.yandex.practicum.commerce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.commerce.dto.product.ProductCreateDto;

import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ProductClient {
    @GetMapping("/{productId}")
    ProductCreateDto findByProductId(@PathVariable UUID productId);

}
