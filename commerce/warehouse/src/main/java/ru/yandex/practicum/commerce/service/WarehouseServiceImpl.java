package ru.yandex.practicum.commerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.cart.CartDto;
import ru.yandex.practicum.commerce.dto.warhouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warhouse.AssemblyOrderDto;
import ru.yandex.practicum.commerce.dto.warhouse.BookingCartDto;
import ru.yandex.practicum.commerce.dto.warhouse.ProductQuantityDto;
import ru.yandex.practicum.commerce.dto.warhouse.ShippedDto;
import ru.yandex.practicum.commerce.dto.warhouse.WarehouseDto;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.OrderBookingNotFoundException;
import ru.yandex.practicum.commerce.exception.ProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.model.Address;
import ru.yandex.practicum.commerce.model.OrderBooking;
import ru.yandex.practicum.commerce.model.Warehouse;
import ru.yandex.practicum.commerce.repository.OrderBookingRepository;
import ru.yandex.practicum.commerce.repository.WarehouseMapper;
import ru.yandex.practicum.commerce.repository.WarehouseRepository;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseMapper warehouseMapper;
    private final WarehouseRepository warehouseRepository;
    private final OrderBookingRepository orderBookingRepository;

    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS;

    static {
        SecureRandom random = new SecureRandom();
        CURRENT_ADDRESS = ADDRESSES[random.nextInt(ADDRESSES.length)];
    }

    @Override
    @Transactional
    public void putNewProduct(WarehouseDto warehouseDto) {
        if (warehouseRepository.existsById(warehouseDto.getProductId())) {
            throw new ProductAlreadyInWarehouseException(
                    String.format("Product %s already exists in warehouse", warehouseDto.getProductId())
            );
        }
        Warehouse warehouse = warehouseMapper.dtoToModel(warehouseDto);
        warehouse.setQuantity(0L);
        warehouseRepository.save(warehouse);
    }

    @Override
    public BookingCartDto bookCart(CartDto cart) {
        if (cart.getProducts() == null || cart.getProducts().isEmpty()) {
            return BookingCartDto.builder()
                    .deliveryWeight(BigDecimal.ZERO)
                    .deliveryVolume(BigDecimal.ZERO)
                    .fragile(false)
                    .build();
        }
        return calculateProperties(cart.getProducts());
    }

    @Override
    @Transactional
    public void addQuantity(ProductQuantityDto productQuantityDto) {
        UUID productId = productQuantityDto.getProductId();
        Optional<Warehouse> optional = warehouseRepository.findById(productId);

        if (optional.isEmpty()) {
            throw new NoSpecifiedProductInWarehouseException(
                    String.format("Product %s not found in warehouse", productId)
            );
        }

        Warehouse warehouse = optional.get();
        warehouse.setQuantity(warehouse.getQuantity() + productQuantityDto.getQuantity());
        warehouseRepository.save(warehouse);
    }

    @Override
    public AddressDto getAddress() {
        Address address = new Address(
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS
        );
        return warehouseMapper.modelToDto(address);
    }

    @Override
    @Transactional
    public BookingCartDto assemblyOrder(AssemblyOrderDto assemblyOrderDto) {
        if (assemblyOrderDto.getProducts() == null || assemblyOrderDto.getProducts().isEmpty()) {
            throw new IllegalArgumentException(String.format("Products in order not found %s", assemblyOrderDto.getOrderId()));
        }
        Map<UUID, Long> products = assemblyOrderDto.getProducts();
        BookingCartDto bookingCartDto = calculateProperties(products);

        List<UUID> productIds = products.keySet().stream().toList();
        List<Warehouse> warehouses = warehouseRepository.findAllById(productIds);
        for (Warehouse warehouse : warehouses) {
            warehouse.setQuantity(warehouse.getQuantity() - products.get(warehouse.getProductId()));
        }
        warehouseRepository.saveAll(warehouses);
        OrderBooking orderBooking = OrderBooking.builder()
                .orderId(assemblyOrderDto.getOrderId())
                .deliveryWeight(bookingCartDto.getDeliveryWeight())
                .deliveryVolume(bookingCartDto.getDeliveryVolume())
                .fragile(bookingCartDto.getFragile())
                .build();
        orderBookingRepository.save(orderBooking);
        return bookingCartDto;
    }

    @Override
    public void shippedOrder(ShippedDto shippedDto) {
        OrderBooking orderBooking = orderBookingRepository.findById(shippedDto.getOrderId())
                .orElseThrow(() -> new OrderBookingNotFoundException(
                        String.format("Booking order not found: %s", shippedDto.getOrderId())
                ));
        UUID warehouseDeliveryId = UUID.randomUUID();
        orderBooking.setDeliveryId(shippedDto.getDeliveryId());
        orderBooking.setWarehouseDeliveryId(warehouseDeliveryId);
    }

    @Override
    public void returnProducts(Map<UUID, Long> returnedProducts) {
        List<UUID> productIds = returnedProducts.keySet().stream().toList();
        List<Warehouse> warehouses = warehouseRepository.findAllById(productIds);
        for (Warehouse warehouse : warehouses) {
            warehouse.setQuantity(warehouse.getQuantity() - returnedProducts.get(warehouse.getProductId()));
        }
        warehouseRepository.saveAll(warehouses);
    }

    private BookingCartDto calculateProperties(Map<UUID, Long> products) {
        List<UUID> productIds = products.keySet().stream().toList();
        List<Warehouse> warehouses = warehouseRepository.findAllById(productIds);

        if (warehouses.size() != productIds.size()) {
            throw new NoSpecifiedProductInWarehouseException("Some products are not found in warehouse");
        }

        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalVolume = BigDecimal.ZERO;
        boolean hasFragile = false;

        for (Warehouse warehouse : warehouses) {
            UUID productId = warehouse.getProductId();
            Long requestedQuantity = products.get(productId);
            Long availableQuantity = warehouse.getQuantity();

            if (requestedQuantity == null || requestedQuantity <= 0) {
                continue;
            }

            if (availableQuantity < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        String.format("Product %s has insufficient quantity in warehouse", productId)
                );
            }

            if (!hasFragile && Boolean.TRUE.equals(warehouse.getFragile())) {
                hasFragile = true;
            }

            totalWeight = totalWeight.add(warehouse.getWeight());
            totalVolume = totalVolume.add(warehouse.getDimension().calcVolume());
        }

        return BookingCartDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile)
                .build();
    }
}