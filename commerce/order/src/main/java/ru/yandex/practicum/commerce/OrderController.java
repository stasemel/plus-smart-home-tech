package ru.yandex.practicum.commerce;

import jakarta.validation.constraints.NotNull;
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
import ru.yandex.practicum.commerce.dto.order.OrderCreateDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.OrderReturnDto;
import ru.yandex.practicum.commerce.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/order")
@RequiredArgsConstructor
@Slf4j
@Validated
public class OrderController {
    private final OrderService orderService;

    @PutMapping
    public OrderDto createOrder(@RequestBody OrderCreateDto orderCreateDto) {
        log.info("Put order {}", orderCreateDto);
        OrderDto order = orderService.createOrder(orderCreateDto);
        log.info("Put order {} done ", order);
        return order;
    }

    @GetMapping
    public List<OrderDto> getOrdersByUsername(@RequestParam @NotNull String username) {
        log.info("Get all orders by username {}", username);
        List<OrderDto> list = orderService.findAllByUsername(username);
        log.info("Found {} users", list.size());
        return list;
    }

    @GetMapping("/{orderId}")
    public OrderDto getOrderById(@PathVariable @NotNull UUID orderId) {
        log.info("Get order by id {}", orderId);
        OrderDto orderDto = orderService.getOrderById(orderId);
        log.info("Get order by id {}: {}", orderId, orderDto);
        return orderDto;
    }

    @PutMapping("/return")
    public OrderDto returnOrder(@RequestBody OrderReturnDto orderReturnDto) {
        log.info("Request return order {}", orderReturnDto);
        OrderDto order = orderService.returnOrder(orderReturnDto);
        log.info("Request return order done {}", order);
        return order;
    }

    @PostMapping("/payment")
    public OrderDto paymentOrder(@RequestBody @NotNull UUID orderId) {
        log.info("Payment order {}", orderId);
        OrderDto order = orderService.paymentOrder(orderId);
        log.info("Payment order done {}", order);
        return order;
    }

    @PostMapping("/payment/failed")
    public OrderDto paymentOrderFailed(@RequestBody @NotNull UUID orderId) {
        log.info("Payment order failed {}", orderId);
        OrderDto order = orderService.paymentOrderFailed(orderId);
        log.info("Payment order failed done {}", order);
        return order;
    }

    @PostMapping("/delivery")
    public OrderDto deliveryOrder(@RequestBody @NotNull UUID orderId) {
        log.info("Delivery order {}", orderId);
        OrderDto order = orderService.deliveryOrder(orderId);
        log.info("Delivery order done {}", order);
        return order;
    }

    @PostMapping("/delivery/failed")
    public OrderDto deliveryOrderFailed(@RequestBody @NotNull UUID orderId) {
        log.info("Delivery order failed {}", orderId);
        OrderDto order = orderService.deliveryOrderFailed(orderId);
        log.info("Delivery order failed done {}", order);
        return order;
    }

    @PostMapping("/completed")
    public OrderDto completedOrder(@RequestBody @NotNull UUID orderId) {
        log.info("Completed order {}", orderId);
        OrderDto order = orderService.completedOrder(orderId);
        log.info("Completed order done {}", order);
        return order;
    }

    @PostMapping("/assembly")
    public OrderDto assemblyOrder(@RequestBody @NotNull UUID orderId) {
        log.info("Assembly order {}", orderId);
        OrderDto order = orderService.assemblyOrder(orderId);
        log.info("Assembly order done {}", order);
        return order;
    }

    @PostMapping("/assembly")
    public OrderDto assemblyOrderFailed(@RequestBody @NotNull UUID orderId) {
        log.info("Assembly order failed {}", orderId);
        OrderDto order = orderService.assemblyOrderFailed(orderId);
        log.info("Assembly order failed done {}", order);
        return order;
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotal(@RequestBody @NotNull UUID orderId) {
        log.info("Calculate total price for order {}", orderId);
        OrderDto order = orderService.calculateTotal(orderId);
        log.info("Calculate total price for order done {}", order);
        return order;
    }
}
