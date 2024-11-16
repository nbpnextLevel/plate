package com.sparta.plate.service.store;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.plate.entity.Store;
import com.sparta.plate.entity.StoreCategory;
import com.sparta.plate.entity.User;
import com.sparta.plate.entity.UserRoleEnum;
import com.sparta.plate.repository.StoreRepository;
import com.sparta.plate.repository.UserRepository;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class CreateStoreServiceTest {

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("가게 생성 성공 테스트")
	void createStore() {

		// given
		String loginId = "owner1111";
		String password = "Owner1111@";
		String nickName = "owner1";
		UserRoleEnum role = UserRoleEnum.OWNER;
		String email = "owner1111@test.com";
		String phone = "010-1234-5678";
		String address = "서울시 서초구 123";

		String encodedPwd = passwordEncoder.encode(password);

		User user = User.builder()
			.loginId(loginId)
			.password(encodedPwd)
			.nickname(nickName)
			.role(role)
			.email(email)
			.phone(phone)
			.address(address)
			.build();

		userRepository.save(user);

		StoreCategory storeCategory = new StoreCategory(UUID.randomUUID(), "한식");
		String storeName = "test";
		String storeNumber = "032-123-4568";
		String storeAddress = "서울시 서초구 123-5";

		Store store = Store.builder()
			.user(user)
			.storeCategory(storeCategory)
			.storeName(storeName)
			.storeNumber(storeNumber)
			.address(storeAddress)
			.build();

		// when
		storeRepository.save(store);

		// then
		Store savedStore = storeRepository.findById(store.getId()).orElseThrow();
		assertEquals(user, savedStore.getUser());
		assertEquals(storeCategory.getId(), savedStore.getStoreCategory().getId());
		assertEquals(storeName, savedStore.getStoreName());
		assertEquals(storeNumber, savedStore.getStoreNumber());
		assertEquals(storeAddress, savedStore.getAddress());
	}
}