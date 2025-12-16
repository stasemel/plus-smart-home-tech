package ru.yandex.practicum.commerce.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;
import ru.yandex.practicum.commerce.dto.order.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "UUID")
    UUID orderId;

    @Column(name = "user_name", nullable = false)
    String userName;

    @Column(name = "shopping_cart_id", nullable = false)
    UUID shoppingCartId;

    @ElementCollection
    @CollectionTable(name = "orders_products",
            joinColumns = {@JoinColumn(name = "order_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    Map<UUID, Long> products;

    @Column(name = "payment_id", nullable = false)
    UUID paymentId;
    @Column(name = "delivery_id", nullable = false)
    UUID deliveryId;
    @Column(name = "state", nullable = false)
    OrderState state;
    @Column(name = "delivery-weight", nullable = false)
    BigDecimal deliveryWeight;
    @Column(name = "delivery-volume", nullable = false)
    BigDecimal deliveryVolume;
    @Column(name = "fragile", nullable = false)
    Boolean fragile;
    @Column(name = "total-price", nullable = false)
    BigDecimal totalPrice;
    @Column(name = "delivery-price", nullable = false)
    BigDecimal deliveryPrice;
    @Column(name = "product-price", nullable = false)
    BigDecimal productPrice;
}
