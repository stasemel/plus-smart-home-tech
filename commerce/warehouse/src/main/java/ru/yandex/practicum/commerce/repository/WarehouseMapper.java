package ru.yandex.practicum.commerce.repository;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.warhouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warhouse.DimensionDto;
import ru.yandex.practicum.commerce.dto.warhouse.WarehouseDto;
import ru.yandex.practicum.commerce.model.Address;
import ru.yandex.practicum.commerce.model.Dimension;
import ru.yandex.practicum.commerce.model.Warehouse;

@Component
@Mapper(componentModel = "spring")
public abstract class WarehouseMapper {
    @Mapping(target = "weight", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    public abstract Warehouse dtoToModel(WarehouseDto warehouseDto);


    public abstract AddressDto modelToDto(Address address);

    public abstract Address dtoToModel(AddressDto addressDto);

    @Mapping(target = "id", ignore = true) // id генерируется БД
    public abstract Dimension dtoToModel(DimensionDto dimensionDto);

    public abstract DimensionDto ModelToDto(Dimension dimension);
}
