package ru.yandex.practicum.commerce.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.product.ProductCategory;
import ru.yandex.practicum.commerce.dto.product.ProductDto;
import ru.yandex.practicum.commerce.dto.product.ProductPageDto;
import ru.yandex.practicum.commerce.dto.product.ProductQuantityState;
import ru.yandex.practicum.commerce.dto.product.ProductUpdateDto;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public interface StoreService {
    ProductDto createProduct(ProductDto productDto);

    ProductPageDto findAllProducts(ProductCategory category, Integer page, Integer size, String sort);

    ProductDto findByProductId(UUID productId);

    ProductDto updateProduct(ProductUpdateDto productUpdateDto);

    boolean removeProduct(String id);

    ProductDto updateProductQuantityState(UUID productId, ProductQuantityState quantityState);

    List<ProductDto> findProductsById(Collection<UUID> ids);
}
