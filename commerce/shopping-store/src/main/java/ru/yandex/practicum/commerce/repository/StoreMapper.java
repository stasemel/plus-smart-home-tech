package ru.yandex.practicum.commerce.repository;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.product.ProductCreateDto;
import ru.yandex.practicum.commerce.dto.product.ProductUpdateDto;
import ru.yandex.practicum.commerce.model.Product;

@Component
@Mapper(componentModel = "spring")
public abstract class StoreMapper {
    public abstract Product dtoToModel(ProductCreateDto productCreateDto);

    public abstract ProductCreateDto modelToDto(Product product);

    // Основной метод обновления
    public void updateProductFromDto(ProductUpdateDto dto, @MappingTarget Product product) {
        mapNonEmptyFields(dto, product);
    }

    // Защищённый метод с настройками
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "productName", qualifiedByName = "mapNonEmptyString")
    @Mapping(target = "description", qualifiedByName = "mapNonEmptyString")
    @Mapping(target = "imageSrc", qualifiedByName = "mapNonEmptyString")
    protected abstract void mapNonEmptyFields(ProductUpdateDto dto, @MappingTarget Product product);

    @Named("mapNonEmptyString")
    protected String mapNonEmptyString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return value;
    }
}
