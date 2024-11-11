package com.sparta.plate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Timestamped {

    // 생성일
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 생성자
    @CreatedBy
    @Column(updatable = false)
    private UUID createBy;

    // 수정일
    @LastModifiedDate
    @Column
    private LocalDateTime updateAt;

    // 수정자
    @LastModifiedBy
    @Column
    private UUID updateBy;

    // 삭제일
    @Column
    private LocalDateTime deletedAt;

    // 삭제자
    @Column
    private UUID deletedBy;
}