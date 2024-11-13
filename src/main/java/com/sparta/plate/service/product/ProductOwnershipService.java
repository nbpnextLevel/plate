package com.sparta.plate.service.product;

import com.sparta.plate.exception.ProductOwnerMismatchException;
import com.sparta.plate.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductOwnershipService {

    private final ProductRepository productRepository;

    public boolean isProductOwner(UUID productId, Long userId) {
        Long foundUserId = productRepository.findUserIdByProductId(productId);
        return foundUserId != null && foundUserId.equals(userId);
    }

    public void checkProductOwnership(UUID productId, Long userId) {
        if (!isProductOwner(productId, userId)) {
            throw new ProductOwnerMismatchException("You are not the owner of this product");
        }
    }
}
