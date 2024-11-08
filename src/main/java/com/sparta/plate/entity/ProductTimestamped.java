package com.sparta.plate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class ProductTimestamped {

    @CreatedDate
    @Column(updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    // @CreatedBy : 추후 인증 로직 구현되면 변경예정
    @Column(nullable = false)
    protected Long createdBy;

    @Column
    protected LocalDateTime deletedAt;

    @Column
    protected Long deletedBy;

}