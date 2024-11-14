package com.sparta.plate.config.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sparta.plate.security.UserDetailsImpl;

public class AuditorAwareImpl implements AuditorAware<Long> {


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
