package com.sparta.plate.config;

import com.sparta.plate.jwt.CustomLogoutFilter;
import com.sparta.plate.jwt.JwtFilter;
import com.sparta.plate.jwt.JwtTokenProvider;
import com.sparta.plate.jwt.LoginFilter;
import com.sparta.plate.security.CustomAccessDeniedHandler;
import com.sparta.plate.security.CustomAuthenticationEntryPoint;
import com.sparta.plate.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final RedisTemplate<String, String> redisTemplate;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter filter = new LoginFilter(jwtTokenProvider);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtFilter jwtFilter() throws Exception {
        return new JwtFilter(jwtTokenProvider, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // csrf disable
        http.csrf((csrf) -> csrf.disable());

        // From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        // http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 시큐리티 예외 처리 설정
        http.exceptionHandling(handler ->
            handler
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
        );


        // TODO 각자 권한에 따른 설정 필요
        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                        .requestMatchers("/", "/api/users/signup", "/api/users/exists/*", "/api/users/login", "/api/users/reissue").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAnyAuthority("ROLE_OWNER", "ROLE_MANAGER")

                        .requestMatchers(HttpMethod.GET, "/api/stores/**", "/api/stores", "/api/categories").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/stores").hasAnyAuthority("ROLE_OWNER", "ROLE_MANAGER", "ROLE_MASTER")
                        .requestMatchers(HttpMethod.POST, "/api/stores/admin").hasAnyAuthority("ROLE_MANAGER", "ROLE_MASTER")
                        .requestMatchers(HttpMethod.PATCH, "/api/stores/**").hasAnyAuthority("ROLE_OWNER", "ROLE_MANAGER", "ROLE_MASTER")
                        .requestMatchers(HttpMethod.DELETE, "/api/stores/**").hasAnyAuthority("ROLE_OWNER", "ROLE_MANAGER", "ROLE_MASTER")

                        .requestMatchers(HttpMethod.GET, "/api/products/{productId}", "/api/products/search", "/api/products/stores/{storeId}").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_OWNER", "ROLE_MANAGER", "ROLE_MASTER")
						.requestMatchers(HttpMethod.POST, "/api/products").hasAnyAuthority("ROLE_OWNER", "ROLE_MANAGER", "ROLE_MASTER")
						.requestMatchers(HttpMethod.PATCH, "/api/products/{productId}").hasAnyAuthority("ROLE_OWNER", "ROLE_MANAGER", "ROLE_MASTER")
						.requestMatchers("/api/products/suggestion", "/api/products/images/{imageId}/delete", "/api/products/{productId}/images", "/api/products/{productId}/delete", "/api/products/{productId}/inventory", "/api/products/{productId}/visibility", "/api/products/{productId}/display-status").hasAnyAuthority("ROLE_OWNER", "ROLE_MANAGER", "ROLE_MASTER")
						.requestMatchers("/api/products/suggestion/history").hasAnyAuthority("ROLE_MANAGER", "ROLE_MASTER")
						.requestMatchers(HttpMethod.GET, "/api/products/histories", "/api/products/images").hasAnyAuthority("ROLE_MANAGER", "ROLE_MASTER")
						.requestMatchers("/api/products/suggestion/{suggestionId}/delete", "/api/products/histories/{historyId}/delete").hasAnyAuthority("ROLE_MASTER")

                        .requestMatchers(HttpMethod.POST, "/api/reviews/{paymentId}").hasAuthority("ROLE_CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/api/reviews/{paymentId/update}").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_MANAGER", "ROLE_MASTER")
                        .requestMatchers(HttpMethod.PUT, "/api/reviews/{paymentId/delete}").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_MANAGER", "ROLE_MASTER")
                        .requestMatchers(HttpMethod.GET, "/api/reviews/{reviewId}").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_MANAGER", "ROLE_MASTER")
                        .requestMatchers(HttpMethod.GET, "/api/reviews/user/{userId}").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_MANAGER", "ROLE_MASTER")
                        .requestMatchers(HttpMethod.GET, "/api/reviews/search/{userId}").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_MANAGER", "ROLE_MASTER")
                        .requestMatchers(HttpMethod.GET, "/api/store/{storeId}").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/payments/{orderId}").hasAnyAuthority("ROLE_CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/payments/{paymentId}").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_MANAGER", "ROLE_MASTER")
                        .requestMatchers(HttpMethod.GET, "/api/payments/user/{userId}").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_MANAGER", "ROLE_MASTER")
                        .requestMatchers(HttpMethod.GET, "/api/payments/search/{userId}").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_MANAGER", "ROLE_MASTER")
                        .requestMatchers(HttpMethod.GET, "/api/payments/store/{storeId}").hasAnyAuthority("ROLE_OWNER", "ROLE_MANAGER", "ROLE_MASTER")

                        .requestMatchers("/api/order/**").hasAnyAuthority("ROLE_OWNER", "ROLE_MANAGER", "ROLE_MASTER", "ROLE_CUSTOMER")
						            .requestMatchers(HttpMethod.PATCH,"/api/order/delete/{orderId}").hasAnyAuthority("ROLE_MANAGER", "ROLE_MASTER")
						            .requestMatchers(HttpMethod.POST,"/api/order").hasAnyAuthority("ROLE_CUSTOMER")

                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        http.addFilterBefore(jwtFilter(), LoginFilter.class);
        http.addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new CustomLogoutFilter(jwtTokenProvider, redisTemplate), LogoutFilter.class);

        return http.build();
    }
}
