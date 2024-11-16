package com.sparta.plate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.plate.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);

	User saveAndFlush(User user);

	Optional<User> findByLoginId(String loginId);

	Optional<User> findByIdAndIsDeletedFalse(Long id);
}
