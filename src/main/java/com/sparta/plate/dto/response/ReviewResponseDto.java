package com.sparta.plate.dto.response;

import com.sparta.plate.entity.Timestamped;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReviewResponseDto {
    private Long paymentId;
    private Long reviewId;
    private String reviewDetail;
    private int reviewScore;
    private Long createdBy;
    private Timestamped createdAt;
    private Long updatedBy;
    private Timestamped updatedAt;
    private Long deletedBy;
    private Timestamped deletedAt;
}
