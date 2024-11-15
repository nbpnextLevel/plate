package com.sparta.plate.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {
    private Long userId;

    private UUID paymentId;

    @Size(max = 2000)
    private String reviewDetail;

    @NotNull
    @Max(5)
    @Min(1)
    private int reviewScore;

    private boolean reviewStatus;
}
