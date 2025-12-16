package ru.yandex.practicum.commerce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.product.ProductCreateDto;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient {
    @GetMapping("/{productId}")
    ProductCreateDto findByProductId(@PathVariable UUID productId);

    @GetMapping("/find-by-ids")
    List<ProductCreateDto> getProductsByIds(@RequestBody Collection<UUID> ids);


}
