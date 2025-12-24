package ru.yandex.practicum.commerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.cart.CartDto;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryState;
import ru.yandex.practicum.commerce.dto.order.OrderCreateDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.OrderReturnDto;
import ru.yandex.practicum.commerce.dto.order.OrderState;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.dto.warhouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warhouse.AssemblyOrderDto;
import ru.yandex.practicum.commerce.dto.warhouse.BookingCartDto;
import ru.yandex.practicum.commerce.exception.OrderNotFoundException;
import ru.yandex.practicum.commerce.feign.DeliveryClient;
import ru.yandex.practicum.commerce.feign.PaymentClient;
import ru.yandex.practicum.commerce.feign.ShoppingCartClient;
import ru.yandex.practicum.commerce.feign.WarehouseClient;
import ru.yandex.practicum.commerce.model.Order;
import ru.yandex.practicum.commerce.repository.OrderMapper;
import ru.yandex.practicum.commerce.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final WarehouseClient warehouseClient;
    private final ShoppingCartClient shoppingCartClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Override
    public OrderDto createOrder(OrderCreateDto orderCreateDto) {
        CartDto cartDto = shoppingCartClient.findByCartId(orderCreateDto.getShoppingCart().getShoppingCartId());
        if (cartDto == null || cartDto.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty or not found");
        }

        AddressDto fromAddressDto = warehouseClient.getAddress();
        BookingCartDto booking = warehouseClient.bookCart(cartDto);

        Order order = orderMapper.dtoToModel(orderCreateDto);
        order.setOrderId(UUID.randomUUID());
        order.setUserName(cartDto.getUserName());
        order.setProducts(cartDto.getProducts());
        order.setFragile(booking.getFragile());
        order.setDeliveryVolume(booking.getDeliveryVolume());
        order.setDeliveryWeight(booking.getDeliveryWeight());
        order.setDeliveryPrice(BigDecimal.ZERO);
        order.setState(OrderState.NEW);

        //считаем доставку
        DeliveryDto delivery = createDelivery(order, fromAddressDto, orderCreateDto.getDeliveryAddress());
        order.setDeliveryId(delivery.getDeliveryId());
        BigDecimal deliveryCost = calculateDelivery(order);
        order.setDeliveryPrice(deliveryCost);

        //создаем оплату, считаем НДС
        PaymentDto paymentDto = paymentClient.createPayment(orderMapper.modelToDto(order));
        order.setPaymentId(paymentDto.getPaymentId());
        BigDecimal productPrice = paymentClient.calculateProductCost(orderMapper.modelToDto(order));
        order.setProductPrice(productPrice);
        BigDecimal totalPrice = paymentClient.calculateTotalCost(orderMapper.modelToDto(order));
        order.setTotalPrice(totalPrice);

        //транзакция
        Order saved = orderRepository.save(order);

        return orderMapper.modelToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> findAllByUserName(String username) {
        List<Order> orders = orderRepository.findAllByUserName(username);
        return orders.stream().map(orderMapper::modelToDto).toList();
    }

    @Override
    @Transactional
    public OrderDto returnOrder(OrderReturnDto orderReturnDto) {
        Order order = orderRepository.findById(orderReturnDto.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderReturnDto.getOrderId()));
        paymentClient.refundPayment(order.getPaymentId());
        order.setState(OrderState.PRODUCT_RETURNED);
        Order saved = orderRepository.save(order);
        return orderMapper.modelToDto(saved);
    }

    @Override
    @Transactional
    public OrderDto paymentOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        if (order.getState() != OrderState.NEW) {
            throw new IllegalStateException("Order must be in NEW state to be paid");
        }
        AssemblyOrderDto assemblyOrderDto = AssemblyOrderDto.builder()
                .products(order.getProducts())
                .orderId(orderId)
                .build();

        BookingCartDto bookingCartDto = warehouseClient.assemblyProductForOrderFromShoppingCart(assemblyOrderDto);
        order.setState(OrderState.PAID);
        Order saved = orderRepository.save(order);
        return orderMapper.modelToDto(saved);
    }

    @Override
    @Transactional
    public OrderDto paymentOrderFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        order.setState(OrderState.PAYMENT_FAILED);
        Order saved = orderRepository.save(order);
        return orderMapper.modelToDto(saved);
    }

    @Override
    @Transactional
    public OrderDto deliveryOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        if (order.getState() != OrderState.PAID) {
            throw new IllegalStateException("Order must be PAID before delivery");
        }
        order.setState(OrderState.DELIVERED);
        Order saved = orderRepository.save(order);
        return orderMapper.modelToDto(saved);
    }

    @Override
    @Transactional
    public OrderDto deliveryOrderFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        order.setState(OrderState.DELIVERY_FAILED);
        Order saved = orderRepository.save(order);
        return orderMapper.modelToDto(saved);
    }

    @Override
    @Transactional
    public OrderDto completedOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        if (order.getState() != OrderState.DELIVERED) {
            throw new IllegalStateException("Order must be DELIVERED to be completed");
        }
        order.setState(OrderState.COMPLETED);
        Order saved = orderRepository.save(order);
        return orderMapper.modelToDto(saved);
    }

    @Override
    @Transactional
    public OrderDto assemblyOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        deliveryClient.pickedDelivery(order.getDeliveryId());
        order.setState(OrderState.ASSEMBLED);
        Order saved = orderRepository.save(order);
        return orderMapper.modelToDto(saved);
    }

    @Override
    @Transactional
    public OrderDto assemblyOrderFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        order.setState(OrderState.ASSEMBLY_FAILED);
        Order saved = orderRepository.save(order);
        return orderMapper.modelToDto(saved);
    }

    @Override
    public OrderDto calculateTotal(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        return orderMapper.modelToDto(order);
    }

    private BigDecimal calculateDelivery(Order order) {
        return deliveryClient.costDelivery(orderMapper.modelToDto(order));
    }

    @Override
    public OrderDto getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        return orderMapper.modelToDto(order);
    }

    private DeliveryDto createDelivery(Order order, AddressDto fromAddress, AddressDto toAddress) {
        DeliveryDto deliveryDto = DeliveryDto.builder()
                .deliveryState(DeliveryState.CREATED)
                .fromAddress(fromAddress)
                .toAddress(toAddress)
                .orderId(order.getOrderId())
                .build();
        DeliveryDto createdDelivery = deliveryClient.createDelivery(deliveryDto);
        return createdDelivery;
    }

}