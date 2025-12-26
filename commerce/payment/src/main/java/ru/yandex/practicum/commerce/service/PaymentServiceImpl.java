package ru.yandex.practicum.commerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentState;
import ru.yandex.practicum.commerce.dto.product.ProductDto;
import ru.yandex.practicum.commerce.exception.NoEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.commerce.exception.PaymentNotFoundException;
import ru.yandex.practicum.commerce.feign.OrderClient;
import ru.yandex.practicum.commerce.feign.ShoppingStoreClient;
import ru.yandex.practicum.commerce.model.Payment;
import ru.yandex.practicum.commerce.repository.PaymentMapper;
import ru.yandex.practicum.commerce.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    public static final BigDecimal NDS = BigDecimal.valueOf(0.10);
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderClient orderClient;
    private final ShoppingStoreClient shoppingStoreClient;

    @Override
    @Transactional
    public PaymentDto createPayment(OrderDto orderDto) {
        BigDecimal productPrice = calculateProductCost(orderDto);
        if (productPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be > 0");
        }
        validateDeliveryPrice(orderDto);
        BigDecimal fee = calculateFee(productPrice);
        BigDecimal total = orderDto.getProductPrice().add(fee).add(orderDto.getDeliveryPrice());
        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .feeTotal(fee)
                .deliveryTotal(orderDto.getDeliveryPrice())
                .productPrice(productPrice)
                .totalPayment(total)
                .build();
        payment.setState(PaymentState.PENDING);
        return paymentMapper.modelToDto(paymentRepository.save(payment));
    }

    private BigDecimal calculateProductCost(OrderDto orderDto) {
        Map<UUID, Long> products = orderDto.getProducts();
        if (products == null || products.isEmpty()) {
            return BigDecimal.ZERO;
        }
        List<ProductDto> productList =
                shoppingStoreClient.getProductsByIds(products.keySet());

        BigDecimal total = BigDecimal.ZERO;
        for (ProductDto product : productList) {
            Long quantity = products.get(product.getProductId());
            if (quantity != null && quantity > 0) {
                total = total.add(
                        product.getPrice().multiply(BigDecimal.valueOf(quantity))
                );
            }
        }
        return total;
    }

    private BigDecimal calculateFee(BigDecimal productPrice) {
        return productPrice.multiply(NDS);
    }

    private void validateDeliveryPrice(OrderDto orderDto) {
        if (orderDto.getDeliveryPrice() == null) {
            throw new NoEnoughInfoInOrderToCalculateException("Not enough data for calculation. Wrong delivery price.");
        }
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        Payment payment = paymentRepository.findById(orderDto.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + orderDto.getOrderId()));
        return payment.getTotalPayment();
    }

    @Override
    @Transactional
    public void refundPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(String.format("Payment %s not found", paymentId)));
        orderClient.paymentOrder(payment.getOrderId());
        payment.setState(PaymentState.SUCCESS);
        paymentRepository.save(payment);
    }

    @Override
    public BigDecimal getProductCost(OrderDto orderDto) {
        Payment payment = paymentRepository.findById(orderDto.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + orderDto.getOrderId()));
        return orderDto.getProductPrice();
    }

    @Override
    @Transactional
    public void failedPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(String.format("Payment %s not found", paymentId)));
        orderClient.paymentOrderFailed(payment.getOrderId());
        payment.setState(PaymentState.FAILED);
        paymentRepository.save(payment);
    }
}
