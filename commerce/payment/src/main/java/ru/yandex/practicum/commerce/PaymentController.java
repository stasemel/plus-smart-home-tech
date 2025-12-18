package ru.yandex.practicum.commerce;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto createPayment(@RequestBody @Valid OrderDto orderDto) {
        log.info("Payment create {}", orderDto);
        PaymentDto paymentDto = paymentService.createPayment(orderDto);
        log.info("Payment create done {}", paymentDto);
        return paymentDto;
    }

    @PostMapping("/totalCost")
    public BigDecimal calculateTotalCost(@RequestBody @Valid OrderDto orderDto) {
        log.info("Calculate total cost for {}", orderDto);
        BigDecimal totalCost = paymentService.calculateTotalCost(orderDto);
        log.info("Calculate total cost for {} result: {}", orderDto, totalCost);
        return totalCost;
    }

    @PostMapping("refund")
    public void refundPayment(@RequestBody @NotBlank UUID paymentId) {
        log.info("Refund paymentId {}", paymentId);
        paymentService.refundPayment(paymentId);
        log.info("Refund paymentId {} done", paymentId);
    }

    @PostMapping("productCost")
    public BigDecimal calculateProductCost(@RequestBody @Valid OrderDto orderDto) {
        log.info("Calculate product cost for {}", orderDto);
        BigDecimal productCost = paymentService.calculateProductCost(orderDto);
        log.info("Calculate product cost for {} result: {}", orderDto, productCost);
        return productCost;
    }

    @PostMapping("failed")
    public void failedPayment(@RequestBody @NotBlank UUID paymentId) {
        log.info("Failed paymentId {}", paymentId);
        paymentService.failedPayment(paymentId);
        log.info("Failed paymentId {} done", paymentId);
    }

}
