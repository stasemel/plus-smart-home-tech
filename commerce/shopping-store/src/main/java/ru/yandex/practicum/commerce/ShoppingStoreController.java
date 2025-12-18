package ru.yandex.practicum.commerce;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.dto.product.ProductCategory;
import ru.yandex.practicum.commerce.dto.product.ProductDto;
import ru.yandex.practicum.commerce.dto.product.ProductPageDto;
import ru.yandex.practicum.commerce.dto.product.ProductQuantityState;
import ru.yandex.practicum.commerce.dto.product.ProductUpdateDto;
import ru.yandex.practicum.commerce.service.StoreService;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/shopping-store")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ShoppingStoreController {
    public final StoreService storeService;

    @PutMapping
    public ProductDto create(@Valid @RequestBody ProductDto productDto) {
        log.info("Create product {}", productDto);
        ProductDto savedProduct = storeService.createProduct(productDto);
        log.info("Create product {} done", savedProduct);
        return savedProduct;
    }

    @GetMapping
    public ProductPageDto findAllProducts(
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
            @RequestParam(defaultValue = "1") @Positive Integer size,
            @RequestParam(defaultValue = "productName") @NotBlank String sort) {
        log.info("Find all products category: {}, page: {}, size: {}, sort: {}", category, page, size, sort);
        ProductPageDto list = storeService.findAllProducts(category, page, size, sort);
        log.info("Found {} products", list.getContent().size());
        return list;
    }

    @GetMapping("/{productId}")
    public ProductDto findByProductId(@PathVariable UUID productId) {
        log.info("Get product by id {}", productId);
        ProductDto productDto = storeService.findByProductId(productId);
        log.info("Found {}", productDto);
        return productDto;
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody @Valid ProductUpdateDto productUpdateDto) {
        log.info("Update product {}", productUpdateDto);
        ProductDto productDto = storeService.updateProduct(productUpdateDto);
        log.info("Update product {} done", productDto);
        return productDto;
    }

    @PostMapping("/removeProductFromStore")
    public boolean removeProductFromStore(@RequestBody @NotNull String id) {
        log.info("Remove product {} from store", id);
        boolean isRemoved = storeService.removeProduct(id);
        log.info("Remove product {} from store result: {}", id, isRemoved);
        return isRemoved;
    }

    @PostMapping("/quantityState")
    public ProductDto updateProductQuantityState(
            @RequestParam UUID productId,
            @RequestParam ProductQuantityState quantityState) {
        log.info("Update product {} quantity state {}", productId, quantityState);
        ProductDto productDto = storeService.updateProductQuantityState(productId, quantityState);
        log.info("Update product {} quantity state {} done", productId, quantityState);
        return productDto;
    }

    @GetMapping("/find-by-ids")
    public List<ProductDto> getProductsByIds(@RequestBody Collection<UUID> ids) {
        log.info("Get products by ids {}", ids);
        List<ProductDto> products = storeService.findProductsById(ids);
        log.info("Get products by ids: {} done: {}", products);
        return products;
    }
}