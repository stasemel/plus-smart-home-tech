package ru.yandex.practicum.commerce.dto.product;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
@ToString
public class ProductUpdateDto {
    @NotNull
    UUID productId;

    String productName;

    String description;

    String imageSrc;

    ProductQuantityState quantityState;

    ProductState productState;

    ProductCategory productCategory;

    BigDecimal price;

}
