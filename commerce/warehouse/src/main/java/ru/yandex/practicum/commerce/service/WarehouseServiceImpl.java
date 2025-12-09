package ru.yandex.practicum.commerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.cart.CartDto;
import ru.yandex.practicum.commerce.dto.warhouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warhouse.BookingCartDto;
import ru.yandex.practicum.commerce.dto.warhouse.ProductQuantityDto;
import ru.yandex.practicum.commerce.dto.warhouse.WarehouseDto;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.feign.ProductClient;
import ru.yandex.practicum.commerce.model.Address;
import ru.yandex.practicum.commerce.model.Warehouse;
import ru.yandex.practicum.commerce.repository.WarehouseMapper;
import ru.yandex.practicum.commerce.repository.WarehouseRepository;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseMapper warehouseMapper;
    private final WarehouseRepository warehouseRepository;
    private final ProductClient productClient;
    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    @Override
    public void putNewProduct(WarehouseDto warehouseDto) {
        if (warehouseRepository.existsById(warehouseDto.getProductId())) {
            throw new ProductAlreadyInWarehouseException(String.format("Product %s is exists in warehouse",
                    warehouseDto.getProductId()));
        }
        Warehouse warehouse = warehouseMapper.dtoToModel(warehouseDto);
        warehouse.setQuantity(0L);
        warehouseRepository.save(warehouse);
    }

    @Override
    public BookingCartDto bookCart(CartDto cart) {
        BigDecimal weight = BigDecimal.ZERO;
        BigDecimal volume = BigDecimal.ZERO;
        Boolean fragile = false;
        List<UUID> ids = cart.getProducts().entrySet().stream().map(Map.Entry::getKey).toList();
        List<Warehouse> warehouses = warehouseRepository.findAllById(ids);
        if (warehouses.size() != ids.size()) {
            throw new NoSpecifiedProductInWarehouseException("Some products is not found in warehouse");
        }
        for (Warehouse warehouse : warehouses) {
            if (!fragile && warehouse.getFragile().equals(true)) {
                fragile = true;
            }
            weight = weight.add(warehouse.getWeight());
            volume = volume.add(warehouse.getDimension().calcVolume());
            if (warehouse.getQuantity() < cart.getProducts().get(warehouse.getProductId())) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(String.format("Product %s low in warehouse",
                        warehouse.getProductId()));
            }
        }
        return BookingCartDto.builder()
                .deliveryVolume(volume)
                .deliveryWeight(weight)
                .fragile(fragile)
                .build();
    }

    @Override
    public void addQuantity(ProductQuantityDto productQuantityDto) {
        Optional<Warehouse> optWarehouse = warehouseRepository.findById(productQuantityDto.getProductId());
        if (optWarehouse.isEmpty()) {
            throw new NoSpecifiedProductInWarehouseException(String.format("Product %s not found in warehouse",
                    productQuantityDto.getProductId()));
        }
        Warehouse warehouse = optWarehouse.get();
        warehouse.setQuantity(
                warehouse.getQuantity() +
                        productQuantityDto.getQuantity()
        );
        warehouseRepository.save(warehouse);
    }

    @Override
    public AddressDto getAddress() {
        Address address = new Address(CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS);
        return warehouseMapper.modelToDto(address);
    }

}
