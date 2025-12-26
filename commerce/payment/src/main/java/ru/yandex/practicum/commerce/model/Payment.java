package ru.yandex.practicum.commerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;
import ru.yandex.practicum.commerce.dto.payment.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "UUID")
    UUID paymentId;
    UUID orderId;
    PaymentState state;
    BigDecimal productPrice;
    BigDecimal totalPayment;
    BigDecimal deliveryTotal;
    BigDecimal feeTotal;
}
