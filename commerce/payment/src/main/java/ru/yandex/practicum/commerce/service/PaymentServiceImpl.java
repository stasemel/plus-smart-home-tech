package ru.yandex.practicum.commerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentState;
import ru.yandex.practicum.commerce.exception.PaymentNotFoundException;
import ru.yandex.practicum.commerce.model.Payment;
import ru.yandex.practicum.commerce.repository.PaymentMapper;
import ru.yandex.practicum.commerce.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    public static final BigDecimal NDS = BigDecimal.valueOf(0.10);
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentDto createPayment(OrderDto orderDto) {
        validateProductPrice(orderDto);
        validateDeliveryPrice(orderDto);
        BigDecimal fee = calculateFee(orderDto.getProductPrice());
        BigDecimal total = orderDto.getProductPrice().add(fee).add(orderDto.getDeliveryPrice());
        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .feeTotal(fee)
                .deliveryTotal(orderDto.getDeliveryPrice())
                .totalPayment(total)
                .build();
        payment.setState(PaymentState.PENDING);
        return paymentMapper.modelToDto(paymentRepository.save(payment));
    }

    private BigDecimal calculateFee(BigDecimal productPrice) {
        return productPrice.multiply(NDS);
    }

    private void validateProductPrice(OrderDto orderDto) {
        if (orderDto.getProductPrice() == null) {
            throw new IllegalArgumentException("Not enough data for calculation. Wrong product price.");
        }
    }

    private void validateDeliveryPrice(OrderDto orderDto) {
        if (orderDto.getDeliveryPrice() == null) {
            throw new IllegalArgumentException("Not enough data for calculation. Wrong delivery price.");
        }
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        validateDeliveryPrice(orderDto);
        return calculateProductCost(orderDto)
                .add(orderDto.getDeliveryPrice());
    }

    @Override
    @Transactional
    public void refundPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(String.format("Payment %s not found", paymentId)));
        payment.setState(PaymentState.REFUND);
        paymentRepository.save(payment);
    }

    @Override
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        validateProductPrice(orderDto);
        return orderDto.getProductPrice()
                .add(calculateFee(orderDto.getProductPrice()));
    }

    @Override
    @Transactional
    public void failedPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(String.format("Payment %s not found", paymentId)));
        payment.setState(PaymentState.FAILED);
        paymentRepository.save(payment);

    }
}
