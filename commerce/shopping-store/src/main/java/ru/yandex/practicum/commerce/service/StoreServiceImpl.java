package ru.yandex.practicum.commerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.commerce.dto.product.ProductCategory;
import ru.yandex.practicum.commerce.dto.product.ProductCreateDto;
import ru.yandex.practicum.commerce.dto.product.ProductPageDto;
import ru.yandex.practicum.commerce.dto.product.ProductQuantityState;
import ru.yandex.practicum.commerce.dto.product.ProductState;
import ru.yandex.practicum.commerce.dto.product.ProductUpdateDto;
import ru.yandex.practicum.commerce.dto.product.SortDto;
import ru.yandex.practicum.commerce.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.model.Product;
import ru.yandex.practicum.commerce.repository.StoreMapper;
import ru.yandex.practicum.commerce.repository.StoreRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;

    @Override
    @Transactional
    public ProductCreateDto createProduct(ProductCreateDto productCreateDto) {
        Product product = storeMapper.dtoToModel(productCreateDto);
        return storeMapper.modelToDto(saveProductTransaction(product));

    }

    @Transactional
    protected Product saveProductTransaction(Product product) {
        return storeRepository.save(product);
    }

    @Override
    public ProductPageDto findAllProducts(ProductCategory category,
                                          Integer page,
                                          Integer size,
                                          String sortExpression) {
        Sort sort = getSortBy(sortExpression);
        Pageable pageable;
        if (sort != null) pageable = PageRequest.of(page, size, sort);
        else {
            pageable = PageRequest.of(page, size);
        }
        Page<Product> pageProduct;
        if (category != null) {
            pageProduct = storeRepository.findAll(pageable);
        } else {
            pageProduct = storeRepository.findAllByProductCategory(category, pageable);
        }
        return ProductPageDto.builder()
                .size(size)
                .page(page)
                .sort(getSortDto(sortExpression))
                .content(pageProduct.getContent().stream()
                        .map(product -> storeMapper.modelToDto(product))
                        .collect(Collectors.toList())
                ).build();
    }

    private Collection<SortDto> getSortDto(String sortExpression) {
        List<SortDto> list = new ArrayList<>();
        String[] parts = sortExpression.split(",");
        Sort.Direction direction = getSortDirection(parts);
        List<String> properties = getSortProperties(parts);
        if (properties.isEmpty()) {
            list.add(SortDto.builder().direction(direction.toString()).build());
            return list;
        }
        for (String property : properties) {
            list.add(SortDto.builder()
                    .direction(direction.toString())
                    .property(property)
                    .build()
            );
        }
        return list;
    }

    @Override
    public ProductCreateDto findByProductId(UUID productId) {
        return storeMapper.modelToDto(findById(productId));
    }

    @Override
    public ProductCreateDto updateProduct(ProductUpdateDto productUpdateDto) {
        Product product = findById(productUpdateDto.getProductId());
        storeMapper.updateProductFromDto(productUpdateDto, product);

        return storeMapper.modelToDto(saveProductTransaction(product));
    }

    @Override
    public boolean removeProduct(String id) {
        UUID productId = UUID.fromString(id.trim().replace("\"", ""));
        Product product = findById(productId);
        product.setProductState(ProductState.DEACTIVATE);
        Product saved = saveProductTransaction(product);
        return saved.getProductState().equals(ProductState.DEACTIVATE);
    }

    @Override
    public ProductCreateDto updateProductQuantityState(UUID productId, ProductQuantityState quantityState) {
        Product product = findById(productId);
        product.setQuantityState(quantityState);
        return storeMapper.modelToDto(storeRepository.save(product));
    }

    private Product findById(UUID productId) {
        Product product = storeRepository.findByProductId(productId);
        if (product == null) {
            throw new ProductNotFoundException(String.format("Product with id %s not found", productId));
        }
        return product;
    }

    private Sort.Direction getSortDirection(String[] parts) {
        Sort.Direction direction;
        String last = parts[parts.length - 1].trim().toUpperCase();
        String desc="DESC";
        if (parts[parts.length - 1].trim().toUpperCase().equals(desc)) {
            direction = Sort.Direction.DESC;
        }
        if ("ASC".equals(last) || desc.equals(last)) {
            direction = desc.equals(last) ? Sort.Direction.DESC : Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.ASC;
        }
        return direction;
    }

    private List<String> getSortProperties(String[] parts) {
        String last = parts[parts.length - 1].trim().toUpperCase();
        List<String> properties;
        if ("ASC".equals(last) || "DESC".equals(last)) {
            properties = Arrays.stream(parts, 0, parts.length - 1)
                    .map(String::trim)
                    .collect(Collectors.toList());
        } else {
            properties = Arrays.stream(parts)
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        return properties;
    }

    private Sort getSortBy(String sort) {
        if ((sort == null) || sort.isBlank()) return null;
        String[] parts = sort.split(",");
        String last = parts[parts.length - 1].trim().toUpperCase();
        Sort.Direction direction = getSortDirection(parts);
        List<String> properties = getSortProperties(parts);
        if (properties.isEmpty()) return null;
        return Sort.by(direction, properties.toArray(new String[0]));
    }
}
