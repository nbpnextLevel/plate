package com.sparta.plate.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDto {
    private String loginId;
    private Long paymentId;

    @Size(max = 2000)
    private String reviewDetail;

    @NotNull
    private int reviewScore;
}
