package ru.yandex.practicum.commerce.feign;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient {
    @PostMapping
    PaymentDto createPayment(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/totalCost")
    BigDecimal calculateTotalCost(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("refund")
    void refundPayment(@RequestBody @NotBlank UUID paymentId);

    @PostMapping("productCost")
    BigDecimal calculateProductCost(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("failed")
    void failedPayment(@RequestBody @NotBlank UUID paymentId);
}
