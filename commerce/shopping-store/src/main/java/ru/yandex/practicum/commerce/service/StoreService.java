package ru.yandex.practicum.commerce.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.product.ProductCategory;
import ru.yandex.practicum.commerce.dto.product.ProductCreateDto;
import ru.yandex.practicum.commerce.dto.product.ProductPageDto;
import ru.yandex.practicum.commerce.dto.product.ProductQuantityState;
import ru.yandex.practicum.commerce.dto.product.ProductUpdateDto;

import java.util.UUID;

@Service
public interface StoreService {
    ProductCreateDto createProduct(ProductCreateDto productCreateDto);

    ProductPageDto findAllProducts(ProductCategory category, Integer page, Integer size, String sort);

    ProductCreateDto findByProductId(UUID productId);

    ProductCreateDto updateProduct(ProductUpdateDto productUpdateDto);

    boolean removeProduct(String id);

    ProductCreateDto updateProductQuantityState(UUID productId, ProductQuantityState quantityState);
}
