package com.sparta.plate.service.product;

import com.sparta.plate.dto.request.ProductDetailsRequestDto;
import com.sparta.plate.dto.request.ProductImageRequestDto;
import com.sparta.plate.dto.request.ProductQuantityRequestDto;
import com.sparta.plate.dto.request.ProductRequestDto;
import com.sparta.plate.dto.response.ProductImageResponseDto;
import com.sparta.plate.dto.response.ProductResponseDto;
import com.sparta.plate.entity.Product;
import com.sparta.plate.entity.ProductDisplayStatusEnum;
import com.sparta.plate.entity.ProductImage;
import com.sparta.plate.entity.Store;
import com.sparta.plate.exception.ProductIsDeletedException;
import com.sparta.plate.exception.ProductNotFoundException;
import com.sparta.plate.exception.UnauthorizedAccessException;
import com.sparta.plate.repository.ProductRepository;
import com.sparta.plate.security.UserDetailsImpl;
import com.sparta.plate.service.store.GetStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageService imageService;
    private final ProductOwnershipService productOwnershipService;
    private final ProductHistoryService historyService;
    private final GetStoreService storeService;

    @Transactional
    public UUID createProduct(ProductRequestDto requestDto) throws IOException {
        Store store = storeService.getStore(requestDto.getStoreId());
        Product product = Product.toEntity(requestDto, store);

        List<ProductImage> newImages = imageService.processProductImages(product, requestDto.getImages());

        product.setProductImages(newImages);

        Product savedProduct = productRepository.saveAndFlush(product);

        ProductDetailsRequestDto currentDto = new ProductDetailsRequestDto(product.getName(), product.getDescription(), product.getPrice());
        historyService.createProductHistory(currentDto, savedProduct.getId());

        return savedProduct.getId();
    }

    @Transactional
    public void deleteProduct(UUID productId, UserDetailsImpl userDetails) {
        Product product = findProductById(productId);

        Long userId = userDetails.getUser().getId();

        boolean isOwner = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_OWNER"));

        if (isOwner) {
            productOwnershipService.checkProductOwnership(product.getId(), userId);
        }

        product.markAsDeleted(userId);
        productRepository.save(product);
    }

    @Transactional
    public void updateProductDetails(UUID productId, ProductDetailsRequestDto requestDto) {
        Product product = findProductById(productId);

        requestDto.setProductName(requestDto.getProductName() == null ? product.getName() : requestDto.getProductName());
        product.setName(requestDto.getProductName() != null ? requestDto.getProductName() : product.getName());

        requestDto.setProductDescription(requestDto.getProductDescription() == null ? product.getDescription() : requestDto.getProductDescription());
        product.setDescription(requestDto.getProductDescription() != null ? requestDto.getProductDescription() : product.getDescription());

        requestDto.setPrice(requestDto.getPrice() == null ? product.getPrice() : requestDto.getPrice());
        product.setPrice(requestDto.getPrice() != null ? requestDto.getPrice() : product.getPrice());

        productRepository.saveAndFlush(product);

        historyService.createProductHistory(requestDto, productId);
    }

    @Transactional
    public void updateStockAndLimit(UUID productId, ProductQuantityRequestDto requestDto) {
        Product product = findProductById(productId);

        product.setMaxOrderLimit(requestDto.getMaxOrderLimit() != null ? requestDto.getMaxOrderLimit() : product.getMaxOrderLimit());
        product.setStockQuantity(requestDto.getStockQuantity() != null ? requestDto.getStockQuantity() : product.getStockQuantity());

        productRepository.save(product);
    }

    @Transactional
    public void updateProductVisibility(UUID productId) {
        Product product = findProductById(productId);

        product.setHidden(!product.isHidden());

        productRepository.save(product);
    }

    @Transactional
    public void updateProductDisplayStatus(UUID productId, String displayStatus) {
        Product product = findProductById(productId);

        ProductDisplayStatusEnum status = ProductDisplayStatusEnum.fromString(displayStatus);
        product.setDisplayStatus(status);

        productRepository.save(product);
    }

    @Transactional
    public void manageProductImage(UUID productId, ProductImageRequestDto requestDto, UserDetailsImpl userDetails) throws IOException {
        Product product = findProductById(productId);

        Long userId = userDetails.getUser().getId();

        boolean isOwner = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_OWNER"));

        if (isOwner) {
            productOwnershipService.checkProductOwnership(product.getId(), userId);
        }

        List<ProductImage> currentImages = product.getProductImages();
        List<ProductImage> newImages = imageService.processProductImages(product, requestDto);

        if (requestDto.getDeletedImageIds() != null) {
            currentImages.stream()
                    .filter(image -> requestDto.getDeletedImageIds().contains(image.getId()))
                    .forEach(image -> image.markAsDeleted(userId));
        }

        if (requestDto.getDeletedImageIds() != null) {
            currentImages = currentImages.stream()
                    .filter(image -> !requestDto.getDeletedImageIds().contains(image.getId()))
                    .toList();
        }

        List<ProductImage> allImages = new ArrayList<>(currentImages);
        allImages.addAll(newImages);

        allImages = imageService.updatePrimaryImage(allImages, requestDto);

        product.setProductImages(allImages);
        productRepository.save(product);
    }

    public ProductResponseDto getProduct(UUID productId, UserDetailsImpl userDetails) {
        Product product = findProductById(productId);

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        Set<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        boolean isRoleCustomer = roles.contains("ROLE_CUSTOMER");
        boolean isRoleOwner = roles.contains("ROLE_OWNER");

        if ((isRoleCustomer || isRoleOwner) && product.isDeleted()) {
            throw new ProductIsDeletedException("The product has been deleted.");
        }

        if (isRoleCustomer && product.isHidden()) {
            throw new UnauthorizedAccessException("You do not have permission to view this hidden product.");
        }

        if (isRoleOwner && product.isHidden()) {
            productOwnershipService.checkProductOwnership(product.getId(), userDetails.getUser().getId());
        }

        List<ProductImageResponseDto> imageResponseDtos = imageService.findActiveImages(productId).stream()
                .map(ProductImageResponseDto::toDto)
                .collect(Collectors.toList());

        return ProductResponseDto.toDto(product, imageResponseDtos);
    }

    private Product findProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
    }

}