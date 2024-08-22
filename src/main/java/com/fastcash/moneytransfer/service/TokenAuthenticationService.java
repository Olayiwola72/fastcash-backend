package com.fastcash.moneytransfer.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.enums.NotificationType;
import com.fastcash.moneytransfer.model.NotificationContext;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.security.TokenAuthenticationProvider;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class TokenAuthenticationService {
	
	private final long jwtExpiresIn;
	private final JwtEncoder jwtEncoder;
	private final JwtDecoder jwtDecoder;
	private final AuthenticationManager authenticationManager;
	private final TokenAuthenticationProvider tokenAuthenticationProvider;
	private final ApplicationEventPublisher eventPublisher;
	private final EmailNotifiable emailNotifiable;
	
	public TokenAuthenticationService(
		@Value("${jwt.expires.in.hours}") long jwtExpiresIn,
		JwtEncoder jwtEncoder,
		JwtDecoder jwtDecoder,
		AuthenticationManager authenticationManager,
		TokenAuthenticationProvider tokenAuthenticationProvider,
		ApplicationEventPublisher eventPublisher,
		EmailNotifiable emailNotifiable
	){
		this.jwtExpiresIn = jwtExpiresIn;
		this.jwtEncoder = jwtEncoder;
		this.jwtDecoder = jwtDecoder;
		this.authenticationManager = authenticationManager;
		this.tokenAuthenticationProvider = tokenAuthenticationProvider;
		this.eventPublisher = eventPublisher;
		this.emailNotifiable = emailNotifiable;
	}
	
	// for password less authentication
    public Authentication authenticateUser(String username, HttpServletRequest request) {    	
    	Authentication authentication = tokenAuthenticationProvider.authenticateUser(username);
        
        ((AbstractAuthenticationToken) authentication).setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Publish the success event
        eventPublisher.publishEvent(new AuthenticationSuccessEvent(authentication));
        return authentication;
    }
    
    public String authenticateUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
        	new UsernamePasswordAuthenticationToken(username, password)
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return generateToken(authentication, null);
    }
    
	public String generateToken(Authentication authentication, User user) {        
        JwtClaimsSet claims = buildJwtClaimsSet(authentication.getName(), getAuthoritiesAsString(authentication.getAuthorities()));
        String encodedJwt = encodeJwt(claims);
        
        // Send user login notification email asynchronously
        if(user != null) emailNotifiable.sendUserLoginNotification(new NotificationContext(NotificationType.EMAIL, user));
        
        return encodedJwt;
    }
	
    
    protected String getAuthoritiesAsString(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }

    private String encodeJwt(JwtClaimsSet claims) {
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
    
	protected JwtClaimsSet buildJwtClaimsSet(String subject, String authorities) {
        Instant now = Instant.now();
        
        return JwtClaimsSet.builder()
        		.issuer(Constants.JWT_ISSUER) // changing "self" to a valid URL
                .issuedAt(now)
                .expiresAt(now.plus(jwtExpiresIn, ChronoUnit.HOURS))
                .subject(subject)
                .claim("authorities", authorities)
                .build();
    }
	
	public String getUserNameFromJwtToken(String token) {
		Jwt jwt = jwtDecoder.decode(token);
		return jwt.getSubject();
    }
	
	public boolean validateJwtToken(String token) {
	    try {
	        // Decode the JWT
	        Jwt jwt = jwtDecoder.decode(token);

	        // Check if the token is expired
	        Instant now = Instant.now();
	        Instant expiresAt = jwt.getExpiresAt();

	        if (expiresAt == null || expiresAt.isBefore(now)) {
	            // Token is expired
	            return false;
	        }

	     // Check the issuer
	        String expectedIssuer = Constants.JWT_ISSUER; // Replace with the expected issuer
	        String issuer = jwt.getIssuer().toString();
	        if (issuer == null || !issuer.equals(expectedIssuer)) {
	            return false;
	        }

	        // Check the subject (user)
	        String subject = jwt.getSubject();
	        if (subject == null || subject.isEmpty()) {
	            return false;
	        }

	        // Check authorities or other custom claims
	        String authorities = jwt.getClaim("authorities");
	        if (authorities == null || authorities.isEmpty()) {
	            return false;
	        }

	        // If all checks pass, return true
	        return true;
	    } catch (Exception e) {
	        // If there's any issue with decoding the token or it's invalid in some way, return false
	        return false;
	    }
	}
	
}