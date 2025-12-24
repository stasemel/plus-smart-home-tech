package ru.yandex.practicum.commerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryState;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.warhouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warhouse.ShippedDto;
import ru.yandex.practicum.commerce.exception.DeliveryNotFoundException;
import ru.yandex.practicum.commerce.feign.OrderClient;
import ru.yandex.practicum.commerce.feign.WarehouseClient;
import ru.yandex.practicum.commerce.model.Delivery;
import ru.yandex.practicum.commerce.repository.DeliveryMapper;
import ru.yandex.practicum.commerce.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;
    private static final BigDecimal COST_COEFFICIENT_ADDRESS_ONE = BigDecimal.valueOf(1);
    private static final BigDecimal COST_COEFFICIENT_ADDRESS_TWO = BigDecimal.valueOf(2);
    private static final BigDecimal COST_COEFFICIENT_FRAGILE = BigDecimal.valueOf(0.2);
    private static final BigDecimal COST_COEFFICIENT_WEIGHT = BigDecimal.valueOf(0.3);
    private static final BigDecimal COST_COEFFICIENT_VOLUME = BigDecimal.valueOf(0.2);
    private static final BigDecimal COST_COEFFICIENT_DELIVERY = BigDecimal.valueOf(0.2);
    private static final BigDecimal COST_BASE = BigDecimal.valueOf(5L);

    @Override
    @Transactional
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        OrderDto orderDto = orderClient.getOrderById(deliveryDto.getOrderId());
        BigDecimal cost = calculateDeliveryPrice(orderDto, deliveryDto);
        Delivery delivery = deliveryMapper.dtoToModel(deliveryDto);
        delivery.setDeliveryVolume(orderDto.getDeliveryVolume());
        delivery.setDeliveryWeight(orderDto.getDeliveryWeight());
        delivery.setFragile(orderDto.getFragile());
        delivery.setDeliveryPrice(cost);
        return deliveryMapper.modelToDto(delivery);
    }

    private BigDecimal calculateDeliveryPrice(OrderDto orderDto, DeliveryDto deliveryDto) {
        final AddressDto fromAddress = deliveryDto.getFromAddress();
        final AddressDto toAddress = deliveryDto.getToAddress();
        BigDecimal price = COST_BASE;
        if (fromAddress.toString().contains("ADDRESS_1")) {
            price = price.multiply(COST_COEFFICIENT_ADDRESS_ONE);
        }
        if (fromAddress.toString().contains("ADDRESS_2")) {
            price = price.add(price.multiply(COST_COEFFICIENT_ADDRESS_TWO));
        }
        if (orderDto.getFragile()) {
            price = price.add(price.multiply(COST_COEFFICIENT_FRAGILE));
        }
        price = price.add(COST_COEFFICIENT_WEIGHT.multiply(orderDto.getDeliveryWeight()));
        price = price.add(COST_COEFFICIENT_VOLUME.multiply(orderDto.getDeliveryVolume()));
        if (fromAddress.getCity().equals(toAddress.getCity()) && fromAddress.getStreet().equals(toAddress.getStreet())) {
            price = price.add(price.multiply(COST_COEFFICIENT_DELIVERY));
        }
        return price;
    }

    @Override
    @Transactional
    public void successfulDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(String.format("Delivery not found: %s", deliveryId)));

        orderClient.deliveryOrder(delivery.getOrderId());
        orderClient.completedOrder(delivery.getOrderId());

        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public void pickedDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(String.format("Delivery not found: %s", deliveryId)));

        ShippedDto shippedDto = ShippedDto.builder()
                .orderId(delivery.getOrderId())
                .deliveryId(deliveryId)
                .build();
        warehouseClient.shippedToDelivery(shippedDto);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);
    }

    @Override
    public void failedDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(String.format("Delivery not found: %s", deliveryId)));

        orderClient.deliveryOrderFailed(delivery.getOrderId());

        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public BigDecimal costDelivery(OrderDto order) {
        Delivery delivery = deliveryRepository.findById(order.getDeliveryId())
                .orElseThrow(() -> new DeliveryNotFoundException(String.format("Delivery not found: %s", order.getDeliveryId())));
        return delivery.getDeliveryPrice();
    }
}
