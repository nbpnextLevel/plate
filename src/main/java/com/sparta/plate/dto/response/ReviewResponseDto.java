package com.sparta.plate.dto.response;

import com.sparta.plate.entity.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long paymentId;
    private Long reviewId;
    private String reviewDetail;
    private int reviewScore;

    private Timestamped timestamped;
}
