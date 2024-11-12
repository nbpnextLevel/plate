package com.sparta.plate.config.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sparta.plate.security.UserDetailsImpl;

public class AuditorAwareImpl implements AuditorAware<Long> {

	private static final ThreadLocal<Long> MANUAL_AUDITOR = new ThreadLocal<>();

	// 현재 쓰레드에 현재 생성된 사용자 id 임시 저장
	public static void setManualAuditor(Long userId) {
		MANUAL_AUDITOR.set(userId);
	}

	public static void clearManualAuditor() {
		MANUAL_AUDITOR.remove();
	}

	@Override
	public Optional<Long> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null || !authentication.isAuthenticated()){
			return Optional.empty();
		}

		Object principal = authentication.getPrincipal();
		if (principal instanceof UserDetailsImpl) {
			UserDetailsImpl userDetails = (UserDetailsImpl) principal;
			return Optional.of(userDetails.getUser().getId());
		}

		return Optional.empty();
	}

}
