package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@SpringBootTest
class GoogleCredentialsRequestTest {
	
	@Autowired
	private Validator validator;
	
	private String credential;
	private String clientId;
	private String select_by;
	
	@BeforeEach
    void setUp() {
   	 	// Create input values
		credential = "ssassasasaxa11.eyJpc3MiOiJodHRwcz";
	    clientId = "clientId@auth.com";
	    select_by = "btn";
	}
	
	@Test
    public void testValidRequest() {
        GoogleCredentialsRequest request = new GoogleCredentialsRequest(credential, clientId, select_by);
        Set<ConstraintViolation<GoogleCredentialsRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty());
    }
	
	@Test
    public void testEmptyValues() {
		Set<String> set = new HashSet<>(List.of("credential", "clientId", "select_by"));
		
		GoogleCredentialsRequest request = new GoogleCredentialsRequest("", "", "");
        Set<ConstraintViolation<GoogleCredentialsRequest>> violations = validator.validate(request);
        
        for(ConstraintViolation<GoogleCredentialsRequest> violation : violations) {
        	assertTrue(set.contains(violation.getPropertyPath().toString()));
        }
        
        assertFalse(violations.isEmpty());
        assertEquals(set.size(), violations.size());
    }
	
	@Test
    public void testEmptyValues_NullValues() {
		Set<String> set = new HashSet<>(List.of("credential", "clientId", "select_by"));
		
		GoogleCredentialsRequest request = new GoogleCredentialsRequest(null, null, null);
        Set<ConstraintViolation<GoogleCredentialsRequest>> violations = validator.validate(request);
        
        for(ConstraintViolation<GoogleCredentialsRequest> violation : violations) {
        	assertTrue(set.contains(violation.getPropertyPath().toString()));
        }
        
        assertFalse(violations.isEmpty());
        assertEquals(set.size(), violations.size());
    }
}
