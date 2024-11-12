package com.sparta.plate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_product_suggestion_request")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSuggestionRequest extends TimestampedCreationDeletion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(length = 1000, nullable = false)
    private String requestText;

    @Column(columnDefinition = "TEXT")
    private String responseText;

    @Column
    private LocalDateTime requestAt;

    @Column
    private LocalDateTime responseAt;

    @Column(nullable = false)
    private boolean isSuccess;

    @Column
    private String responseStatus;
}
