package com.sparta.plate.service;

import com.sparta.plate.dto.request.ProductDetailsRequestDto;
import com.sparta.plate.dto.request.ProductQueryDto;
import com.sparta.plate.entity.*;
import com.sparta.plate.exception.*;
import com.sparta.plate.repository.ProductRepository;
import com.sparta.plate.repository.StoreRepository;
import com.sparta.plate.security.UserDetailsImpl;
import com.sparta.plate.service.product.ProductHistoryService;
import com.sparta.plate.service.product.ProductOwnershipService;
import com.sparta.plate.service.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// 상품 도메인 테스트 코드
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ProductOwnershipService productOwnershipService;

    @Mock
    private ProductHistoryService productHistoryService;

    @InjectMocks
    private ProductService productService;

    private User customer, owner1, owner2, manager, master;
    private Store store;
    private Product product;

    @BeforeEach
    void setUp() {
        customer = User.builder()
                .loginId("customer1")
                .password("Customer1234")
                .nickname("고객1")
                .role(UserRoleEnum.CUSTOMER)
                .email("customer1@naver.com")
                .phone("010-1234-5678")
                .address("경기도 성남시 분당구 네이버로23")
                .build();

        owner1 = User.builder()
                .loginId("owner1")
                .password("Owner1234")
                .nickname("주인1")
                .role(UserRoleEnum.OWNER)
                .email("owner1@naver.com")
                .phone("010-1234-5678")
                .address("경기도 성남시 분당구 네이버로23")
                .build();

        owner2 = User.builder()
                .loginId("owner2")
                .password("Owner1234")
                .nickname("주인2")
                .role(UserRoleEnum.OWNER)
                .email("owner2@naver.com")
                .phone("010-1234-5678")
                .address("경기도 성남시 분당구 네이버로23")
                .build();

        manager = User.builder()
                .loginId("manager1")
                .password("Manager1234")
                .nickname("매니저1")
                .role(UserRoleEnum.MANAGER)
                .email("manager1@naver.com")
                .phone("010-1234-5678")
                .address("경기도 성남시 분당구 네이버로23")
                .build();

        master = User.builder()
                .loginId("master1")
                .password("Master1234")
                .nickname("마스터1")
                .role(UserRoleEnum.MASTER)
                .email("master1@naver.com")
                .phone("010-1234-5678")
                .address("경기도 성남시 분당구 네이버로23")
                .build();

        StoreCategory category = new StoreCategory(UUID.randomUUID(), "음식점");

        store = Store.builder()
                .user(owner1)
                .storeCategory(category)
                .storeName("테스트 상점")
                .storeNumber("12345")
                .address("서울특별시 강남구 테헤란로 123")
                .build();

        product = Product.builder()
                .id(UUID.randomUUID())
                .store(store)
                .name("연어 샐러드")
                .description("설명")
                .price(new BigDecimal("18000"))
                .displayStatus(ProductDisplayStatusEnum.PENDING_SALE)
                .maxOrderLimit(100)
                .stockQuantity(55)
                .isHidden(false)
                .build();

    }

    @Test
    @Transactional
    @DisplayName("Product 생성 및 저장 테스트")
    void createProductAndSave() {
        when(storeRepository.findAll()).thenReturn(List.of(store));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        List<ProductImage> productImages = new ArrayList<>();
        ProductImage image1 = ProductImage.builder()
                .fileName("salad1")
                .uploadPath("/download/salad1")
                .isPrimary(true)
                .product(product)
                .build();
        ProductImage image2 = ProductImage.builder()
                .fileName("salad2")
                .uploadPath("/download/salad2")
                .isPrimary(false)
                .product(product)
                .build();

        productImages.add(image1);
        productImages.add(image2);
        product.setProductImages(productImages);

        Product savedProduct = productRepository.save(product);

        verify(productRepository, times(1)).save(any(Product.class));
        assertEquals("연어 샐러드", savedProduct.getName());
        assertEquals("설명", savedProduct.getDescription());
        assertEquals(new BigDecimal("18000"), savedProduct.getPrice());
        assertEquals(100, savedProduct.getMaxOrderLimit());
        assertEquals(55, savedProduct.getStockQuantity());
    }

    @Test
    @DisplayName("Product 노출 상태 수정 테스트 (실패) - 유효하지 않은 상태 ENUM")
    void updateProductDisplayStatusFailWhenInvalidStatus() {
        UUID productId = product.getId();
        String invalidStatus = "INVALID_STATUS";
        UserDetailsImpl owner = new UserDetailsImpl(owner1);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        doThrow(new InvalidDisplayStatusException("Invalid display status"))
                .when(productOwnershipService).checkProductOwnership(eq(productId), any(UserDetailsImpl.class));

        assertThrows(InvalidDisplayStatusException.class,
                () -> productService.updateProductDisplayStatus(productId, invalidStatus, owner));

        verify(productRepository, never()).save(any(Product.class));
    }


    @Test
    @DisplayName("Product 정보 수정 테스트 (실패) - 해당 상품의 주인이 아닌 경우")
    void updateProductDetailFailWhenNotOwner() {
        UUID productId = product.getId();
        ProductDetailsRequestDto requestDto = new ProductDetailsRequestDto("새 상품명", "새 설명", BigDecimal.valueOf(20000));
        UserDetailsImpl notOwner = new UserDetailsImpl(owner2);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        doThrow(new ProductOwnerMismatchException("You are not the owner of this product"))
                .when(productOwnershipService).checkProductOwnership(productId, notOwner);

        assertThrows(ProductOwnerMismatchException.class,
                () -> productService.updateProductDetails(productId, requestDto, notOwner));

        verify(productOwnershipService, times(1)).checkProductOwnership(productId, notOwner);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Product 정보 수정 테스트 (성공) - 해당 상품의 주인이거나 매니저, 마스터인 경우")
    void updateProductDetailSuccessWhenOwner() {
        UUID productId = product.getId();
        ProductDetailsRequestDto requestDto = new ProductDetailsRequestDto("새 이름", "새 설명", BigDecimal.valueOf(20000));
        UserDetailsImpl owner = new UserDetailsImpl(owner1);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        doNothing().when(productOwnershipService).checkProductOwnership(productId, owner);
        doNothing().when(productHistoryService).createProductHistory(requestDto, productId);

        productService.updateProductDetails(productId, requestDto, owner);

        verify(productOwnershipService, times(1)).checkProductOwnership(productId, owner);
        verify(productRepository, times(1)).saveAndFlush(any(Product.class));
        verify(productHistoryService, times(1)).createProductHistory(requestDto, productId);

        assertEquals("새 이름", product.getName());
        assertEquals("새 설명", product.getDescription());
        assertEquals(BigDecimal.valueOf(20000), product.getPrice());
    }

    @Test
    @DisplayName("Product 정보 수정 테스트 (실패) - 이미 삭제된 상품을 수정하는 경우")
    void updateProductDetailFailWhenDeleted() {
        product.markAsDeleted(owner1.getId());
        UUID productId = product.getId();
        ProductDetailsRequestDto requestDto = new ProductDetailsRequestDto("새 이름", "새 설명", BigDecimal.valueOf(20000));
        UserDetailsImpl owner = new UserDetailsImpl(owner1);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(ProductIsDeletedException.class,
                () -> productService.updateProductDetails(productId, requestDto, owner));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Product 삭제 테스트 (실패) - 이미 삭제된 상품을 삭제하는 경우")
    void deleteProductDetailFailWhenAlreadyDeleted() {
        product.markAsDeleted(owner1.getId());
        UUID productId = product.getId();
        UserDetailsImpl owner = new UserDetailsImpl(owner1);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(ProductIsDeletedException.class,
                () -> productService.deleteProduct(productId, owner));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Product 삭제 테스트 (성공) - 해당 상품의 주인이거나 매니저, 마스터인 경우")
    void deleteProductDetailSuccessWhenOwner() {
        UUID productId = product.getId();
        UserDetailsImpl owner = new UserDetailsImpl(owner1);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        doNothing().when(productOwnershipService).checkProductOwnership(productId, owner);

        productService.deleteProduct(productId, owner);

        verify(productOwnershipService, times(1)).checkProductOwnership(productId, owner);
        verify(productRepository, times(1)).save(any(Product.class));
        assertTrue(product.isDeleted());
    }

    @Test
    @DisplayName("Product 조회 실패 테스트 (실패) -상품이 존재하지 않는 경우")
    void getProductFailWhenProductNotFound() {
        UUID invalidProductId = UUID.randomUUID();

        when(productRepository.findById(invalidProductId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.getProductById(invalidProductId));
    }

    @Test
    @DisplayName("Product 조회 실패 테스트 - ROLE_CUSTOMER가 판매중인 상품 외의 상품을 조회할 때 예외 발생")
    void searchProductFailWhenCustomerRoleWithInvalidStatus() {
        ProductQueryDto queryDto = ProductQueryDto.builder()
                .displayStatus("PENDING_SALE")
                .build();
        UserDetailsImpl customerUserDetails = new UserDetailsImpl(customer);

        InvalidDisplayStatusException exception = assertThrows(InvalidDisplayStatusException.class,
                () -> productService.searchProducts(queryDto, customerUserDetails));

        assertEquals("Customer는 판매중인 상품만 조회할 수 있습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("Product 조회 실패 테스트 - 기간 검색을 OWNER, MANAGER, MASTER 외의 사용자로 수행할 때 예외 발생")
    void searchProductFailWhenNonAuthorizedRoleForDateRange() {
        ProductQueryDto queryDto = ProductQueryDto.builder()
                .startDate(LocalDateTime.of(2024, 1, 1, 0, 0))
                .endDate(LocalDateTime.of(2024, 12, 31, 23, 59))
                .build();
        UserDetailsImpl customerUserDetails = new UserDetailsImpl(customer);

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
                () -> productService.searchProducts(queryDto, customerUserDetails));

        assertEquals("상품의 기간 검색은 Owner, Manager, Master만 수행할 수 있습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("Product 조회 실패 테스트 - 숨겨진 상품을 OWNER, MANAGER, MASTER 외의 사용자로 조회할 때 예외 발생")
    void searchProductFailWhenNonAuthorizedRoleForHiddenProduct() {
        ProductQueryDto queryDto = ProductQueryDto.builder()
                .isHidden("true")
                .build();
        UserDetailsImpl customerUserDetails = new UserDetailsImpl(customer);

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
                () -> productService.searchProducts(queryDto, customerUserDetails));

        assertEquals("숨겨진 상품은 Owner, Manager, Master만 조회할 수 있습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("Product 조회 실패 테스트 - 삭제된 상품을 MANAGER, MASTER 외의 사용자로 조회할 때 예외 발생")
    void searchProductFailWhenNonAuthorizedRoleForDeletedProduct() {
        ProductQueryDto queryDto = ProductQueryDto.builder()
                .isDeleted("true")
                .build();
        UserDetailsImpl ownerUserDetails = new UserDetailsImpl(owner1);

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
                () -> productService.searchProducts(queryDto, ownerUserDetails));

        assertEquals("삭제된 상품은 Manager와 Master만 조회할 수 있습니다.", exception.getMessage());
    }

}