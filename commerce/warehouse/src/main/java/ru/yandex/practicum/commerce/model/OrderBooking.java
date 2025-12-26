package ru.yandex.practicum.commerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_booking")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderBooking {
    @Id
    UUID orderId;
    BigDecimal deliveryWeight;
    BigDecimal deliveryVolume;
    Boolean fragile;
    UUID deliveryId;
    UUID warehouseDeliveryId;
}
