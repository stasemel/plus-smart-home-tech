package ru.yandex.practicum.commerce.dto.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
@ToString
public class OrderDto {
    UUID orderId;
    UUID shoppingCartId;
    Map<UUID, Long> products;
    UUID paymentId;
    UUID deliveryId;
    OrderState state;
    BigDecimal deliveryWeight;
    BigDecimal deliveryVolume;
    Boolean fragile;
    BigDecimal totalPrice;
    BigDecimal deliveryPrice;
    BigDecimal productPrice;
}
