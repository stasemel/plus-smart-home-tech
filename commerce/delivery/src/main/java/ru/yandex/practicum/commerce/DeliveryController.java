package ru.yandex.practicum.commerce;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/delivery")
@RequiredArgsConstructor
@Slf4j
@Validated
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto createDelivery(@RequestBody @Valid DeliveryDto deliveryDto) {
        log.info("Create delivery {}", deliveryDto);
        DeliveryDto dto = deliveryService.createDelivery(deliveryDto);
        log.info("Created delivery done: {}", deliveryDto);
        return deliveryDto;
    }

    @PostMapping("/successful")
    public void successfulDelivery(@RequestBody @NotNull UUID deliveryId) {
        log.info("Successful delivery id = {}", deliveryId);
        deliveryService.successfulDelivery(deliveryId);
        log.info("Successful delivery id = {} done", deliveryId);
    }

    @PostMapping("/picked")
    public void pickedDelivery(@RequestBody @NotNull UUID deliveryId) {
        log.info("Picked delivery id = {}", deliveryId);
        deliveryService.pickedDelivery(deliveryId);
        log.info("Picked delivery id = {} done", deliveryId);
    }

    @PostMapping("/failed")
    public void failedDelivery(@RequestBody @NotNull UUID deliveryId) {
        log.info("Failed delivery id = {}", deliveryId);
        deliveryService.failedDelivery(deliveryId);
        log.info("Failed delivery id = {} done", deliveryId);
    }

    @PostMapping("/cost")
    public BigDecimal costDelivery(@RequestBody @NotNull OrderDto order) {
        log.info("Cost delivery order = {}", order);
        BigDecimal cost = deliveryService.costDelivery(order);
        log.info("Cost delivery order = {} is {}", order, cost);
        return cost;
    }
}
