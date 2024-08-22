
package com.fastcash.moneytransfer.dto.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ErrorFieldTest {
	@Test 
	void testErrorMessageConstructor(){
		ErrorField errorField = new ErrorField("errorMessage");
		errorField.setErrorMessage("errorMessage");
		
		assertNull(errorField.getFieldName());
		assertNotNull(errorField.getErrorMessage());
		assertEquals("errorMessage", errorField.getErrorMessage());
	}
	
	@Test
	void testErrorMessageAndFieldNameConstructor() {
		testGetSetErrorMessage();
		testGetSetFieldName();
	}
	
	@Test
	void testGetSetErrorMessage() {
		ErrorField errorField = new ErrorField("errorMessage", "fieldName");
		errorField.setErrorMessage("errors");
		
		assertNotNull(errorField.getErrorMessage());
		assertEquals("errors", errorField.getErrorMessage());
	}
	
	@Test
	void testGetSetFieldName() {
		ErrorField errorField = new ErrorField("errorMessage", "fieldName");
		errorField.setFieldName("fields");
		
		assertNotNull(errorField.getFieldName());
		assertEquals("fields", errorField.getFieldName());
	}
}