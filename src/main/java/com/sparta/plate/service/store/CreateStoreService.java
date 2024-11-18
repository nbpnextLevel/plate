package com.sparta.plate.service.store;

import com.sparta.plate.dto.request.StoreByAdminRequestDto;
import com.sparta.plate.dto.request.StoreRequestDto;
import com.sparta.plate.entity.Store;
import com.sparta.plate.entity.StoreCategory;
import com.sparta.plate.entity.User;
import com.sparta.plate.entity.UserRoleEnum;
import com.sparta.plate.exception.AlreadyStoreExsitException;
import com.sparta.plate.exception.StoreCategoryNotFoundException;
import com.sparta.plate.exception.UserNotFoundException;
import com.sparta.plate.repository.StoreCategoryRepository;
import com.sparta.plate.repository.StoreRepository;
import com.sparta.plate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateStoreService {

    private final StoreRepository storeRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final UserRepository userRepository;

    public Store createStore(StoreRequestDto request, User user) {

        validateUserHasNoStore(user);

        StoreCategory storeCategory = findStoreCategoryById(request.getCategoryId());

        Store store = Store.builder()
                .user(user)
                .storeCategory(storeCategory)
                .storeName(request.getName())
                .storeNumber(request.getStoreNumber())
                .address(request.getAddress())
                .build();

        Store savedStore = storeRepository.save(store);

        return savedStore;
    }

    @Transactional
    public Store createStoreByAdmin(StoreByAdminRequestDto request, User user) {

        StoreCategory storeCategory = findStoreCategoryById(request.getCategoryId());
        User targetUser = getTargetUser(request);

        if (!targetUser.getRole().equals(UserRoleEnum.OWNER)) {
            targetUser.changeAuthority(UserRoleEnum.OWNER);
        }

        Store store = Store.builder()
                .user(targetUser)
                .storeCategory(storeCategory)
                .storeName(request.getName())
                .storeNumber(request.getStoreNumber())
                .address(request.getAddress())
                .build();

        storeRepository.save(store);

        return store;
    }

    private User getTargetUser(StoreByAdminRequestDto request) {
        return userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + request.getUserId()));
    }

    private void validateUserHasNoStore(User user) {
        storeRepository.findByUserIdAndIsDeletedFalse(user.getId())
                .ifPresent(store -> {
                    throw new AlreadyStoreExsitException("해당 유저는 이미 가게를 생성했습니다. loginID = " + user.getLoginId());
                });
    }

    private StoreCategory findStoreCategoryById(UUID categoryId) {
        return storeCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new StoreCategoryNotFoundException("Store Category not found with ID: " + categoryId));
    }

}


