package com.sparta.plate.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    @NotNull(message = "Store ID cannot be null")
    private UUID storeId;

    @NotBlank(message = "Product name cannot be blank")
    private String productName;

    @NotBlank(message = "Product description cannot be blank")
    private String productDescription;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be a positive value")
    private BigDecimal price;

    private String displayStatus;

    @NotNull(message = "Max order limit cannot be null")
    @Positive(message = "Max order limit must be positive")
    private Integer maxOrderLimit;

    @NotNull(message = "Stock quantity cannot be null")
    @Positive(message = "Stock quantity must be positive")
    private Integer stockQuantity;

    @JsonProperty("isHidden")
    @Builder.Default
    private Boolean isHidden = false;

    @Setter
    private ProductImageRequestDto images;
}