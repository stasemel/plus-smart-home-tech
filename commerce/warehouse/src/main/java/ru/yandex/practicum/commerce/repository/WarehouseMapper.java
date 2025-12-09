package ru.yandex.practicum.commerce.repository;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.warhouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warhouse.WarehouseDto;
import ru.yandex.practicum.commerce.model.Address;
import ru.yandex.practicum.commerce.model.Warehouse;

@Component
@Mapper(componentModel = "spring")
public abstract class WarehouseMapper {
    public abstract Warehouse dtoToModel(WarehouseDto warehouseDto);

    public abstract WarehouseDto modelToDto(Warehouse warehouse);

    public abstract AddressDto modelToDto(Address address);

    public abstract Address dtoToModel(AddressDto addressDto);
}
