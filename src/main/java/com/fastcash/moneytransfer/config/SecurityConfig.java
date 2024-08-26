package com.fastcash.moneytransfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fastcash.moneytransfer.security.AccountUpdateAuthorizationManager;
import com.fastcash.moneytransfer.security.DelegatedAuthenticationEntryPoint;
import com.fastcash.moneytransfer.security.DelegatedBearerTokenAccessDeniedHandler;
import com.fastcash.moneytransfer.security.InternalExternalUserFilter;
import com.fastcash.moneytransfer.security.UserUpdateAuthorizationManager;
import com.fastcash.moneytransfer.service.UserService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	private final String[] authWhitelistUrls;
	
	private final String apiBaseUrl;
	
	private final String authEndpoint;
	
	private final String userEndpoint;
	
	private final String accountEndpoint;
	
	private final String passwordEndpoint;
	
	private final RsaKeyConfig rsaKeys;
	
	private final PasswordEncoder passwordEncoder;
	
	private final UserService userService;

	private final DelegatedAuthenticationEntryPoint delegatedAuthenticationEntryPoint;
	
	private final DelegatedBearerTokenAccessDeniedHandler delegatedBearerTokenAccessDeniedHandler;
	
	private final AccountUpdateAuthorizationManager accountUpdateAuthorizationManager;
	
	private final UserUpdateAuthorizationManager userUpdateAuthorizationManager;
	
	public SecurityConfig(
			@Value("${auth.whitelist.urls}") String[] authWhitelistUrls,
			@Value("${api.base.url}") String apiBaseUrl,
			@Value("${endpoint.auth}") String authEndpoint,
			@Value("${endpoint.user}") String userEndpoint,
			@Value("${endpoint.account}") String accountEndpoint,
			@Value("${endpoint.password}") String passwordEndpoint,
			RsaKeyConfig rsaKeys,
			PasswordEncoder passwordEncoder,
			UserService userService, 
			DelegatedAuthenticationEntryPoint delegatedAuthenticationEntryPoint,
			DelegatedBearerTokenAccessDeniedHandler delegatedBearerTokenAccessDeniedHandler,
			UserUpdateAuthorizationManager userUpdateAuthorizationManager,
			AccountUpdateAuthorizationManager accountUpdateAuthorizationManager
			) {
		this.authWhitelistUrls = authWhitelistUrls;
		this.authEndpoint = authEndpoint;
		this.apiBaseUrl = apiBaseUrl;
		this.userEndpoint = userEndpoint;
		this.accountEndpoint = accountEndpoint;
		this.passwordEndpoint = passwordEndpoint;
		this.rsaKeys = rsaKeys;
		this.passwordEncoder = passwordEncoder;
		this.userService = userService;
		this.delegatedAuthenticationEntryPoint = delegatedAuthenticationEntryPoint;
		this.delegatedBearerTokenAccessDeniedHandler = delegatedBearerTokenAccessDeniedHandler;
		this.accountUpdateAuthorizationManager = accountUpdateAuthorizationManager;
		this.userUpdateAuthorizationManager = userUpdateAuthorizationManager;
	}
	
	@Bean
	@Order(1)
	public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, InternalExternalUserFilter internalExternalUserFilter,  AuthenticationProvider authenticationProvider) throws Exception {
		return http
			.securityMatcher(apiBaseUrl+"/**")
			.authorizeHttpRequests(auth -> auth
					.requestMatchers(HttpMethod.PUT, apiBaseUrl + userEndpoint + "/**").access(userUpdateAuthorizationManager) // The authorization rule is defined in the UserUpdateAuthorizationManager
					.requestMatchers(HttpMethod.PATCH, apiBaseUrl + userEndpoint + "/**").access(userUpdateAuthorizationManager) // The authorization rule is defined in the UserUpdateAuthorizationManager
					.requestMatchers(HttpMethod.DELETE, apiBaseUrl + userEndpoint + "/**").access(userUpdateAuthorizationManager) // The authorization rule is defined in the UserUpdateAuthorizationManager
					.requestMatchers(HttpMethod.PUT, apiBaseUrl + accountEndpoint + "/**").access(accountUpdateAuthorizationManager) // The authorization rule is defined in the AccountUpdateAuthorizationManager
					.requestMatchers(HttpMethod.POST, apiBaseUrl + passwordEndpoint + "/**").permitAll()
					.requestMatchers(apiBaseUrl + authEndpoint + "/**").permitAll()
					.anyRequest()	              
	                .authenticated()
				)
			.cors(Customizer.withDefaults()) // by default use a bean by the name of corsConfigurationSource
			.csrf(csrf -> csrf.disable())
			.httpBasic(basic -> basic
					.authenticationEntryPoint(this.delegatedAuthenticationEntryPoint)
				)
			.oauth2ResourceServer((oauth2) -> oauth2 // Spring Security built-in support for JWTs using oAuth2 Resource Server.
					.jwt(Customizer.withDefaults())
					.authenticationEntryPoint(this.delegatedAuthenticationEntryPoint)
					.accessDeniedHandler(this.delegatedBearerTokenAccessDeniedHandler)
				)
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authenticationProvider(authenticationProvider)
			.addFilterBefore(internalExternalUserFilter, BasicAuthenticationFilter.class)
			.build();
	}
	
	@Bean
	@Order(2)
	public SecurityFilterChain otherSecurityFilterChain(HttpSecurity http) throws Exception {			
	    return http
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(authWhitelistUrls).permitAll() // Permit whitelisted URLs
	            .requestMatchers("/**/*.{js,html,css,svg,png,jpg,jpeg,gif,ico}").permitAll() // Permit static resources
	            .anyRequest().permitAll() // Allow SPA fallback
	        )
	        .csrf(csrf -> csrf
	            .ignoringRequestMatchers(authWhitelistUrls)
	            .disable()
	        )
	        .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable)) // Disable frame options for H2 console access
	        .build();
	}
	
	@Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.rsaKeys.getPublicKey()).build();
    }
	
    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.rsaKeys.getPublicKey()).privateKey(this.rsaKeys.getPrivateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
    
	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter jWTGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		
		jWTGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
		jWTGrantedAuthoritiesConverter.setAuthorityPrefix("");
		
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jWTGrantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(this.userService);
		authenticationProvider.setPasswordEncoder(this.passwordEncoder);
		return authenticationProvider;
	}

	@Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
               .build();
    }
	
}