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
import ru.yandex.practicum.commerce.dto.product.ProductDto;
import ru.yandex.practicum.commerce.dto.warhouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warhouse.BookingCartDto;
import ru.yandex.practicum.commerce.exception.OrderNotFoundException;
import ru.yandex.practicum.commerce.feign.DeliveryClient;
import ru.yandex.practicum.commerce.feign.ShoppingCartClient;
import ru.yandex.practicum.commerce.feign.ShoppingStoreClient;
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
    private final ShoppingStoreClient shoppingStoreClient;
    private final ShoppingCartClient shoppingCartClient;
    private final DeliveryClient deliveryClient;

    @Override
    public OrderDto createOrder(OrderCreateDto orderCreateDto) {
        CartDto cartDto = shoppingCartClient.findByCartId(orderCreateDto.getShoppingCart().getShoppingCartId());
        if (cartDto == null || cartDto.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty or not found");
        }

        BigDecimal productPrice = calculateProductPrice(cartDto.getProducts());
        if (productPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be > 0");
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
        order.setProductPrice(productPrice);
        order.setDeliveryPrice(BigDecimal.ZERO);
        order.setTotalPrice(productPrice.add(order.getDeliveryPrice()));
        order.setState(OrderState.NEW);

        //считаем доставку
        DeliveryDto delivery = createDelivery(order, fromAddressDto, orderCreateDto.getDeliveryAddress());
        order.setDeliveryId(delivery.getDeliveryId());
        BigDecimal deliveryCost = calculateDelivery(order);

        order.setDeliveryPrice(deliveryCost);
        order.setTotalPrice(productPrice.add(deliveryCost));

        //транзакция
        Order saved = orderRepository.save(order);

        return orderMapper.modelToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> findAllByUsername(String username) {
        List<Order> orders = orderRepository.findAllByUsername(username);
        return orders.stream().map(orderMapper::modelToDto).toList();
    }

    @Override
    @Transactional
    public OrderDto returnOrder(OrderReturnDto orderReturnDto) {
        Order order = orderRepository.findById(orderReturnDto.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderReturnDto.getOrderId()));
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
            throw new IllegalStateException("Order must be in CREATED state to be paid");
        }
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
        if (order.getState() != OrderState.PAID) {
            throw new IllegalStateException("Order must be PAID before assembly");
        }
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

    private BigDecimal calculateProductPrice(java.util.Map<UUID, Long> products) {
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
}