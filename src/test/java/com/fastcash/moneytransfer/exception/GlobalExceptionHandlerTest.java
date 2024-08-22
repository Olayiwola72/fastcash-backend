package com.fastcash.moneytransfer.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.dto.errors.ErrorResponse;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private ReloadableResourceBundleMessageSource messageSource;

    @BeforeEach
    void setUp() {
        MessageSourceConfig messageSourceConfig = new MessageSourceConfig();
        messageSource = messageSourceConfig.messageSource();
        exceptionHandler = new GlobalExceptionHandler(messageSource);
    }

    @Test
    void testHandleJwtValidationException() {
        JwtValidationException exception = mock(JwtValidationException.class);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleJwtValidationException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(
            messageSource.getMessage("TokenExpired", null, LocaleContextHolder.getLocale()),
            response.getBody().getErrors().get(0).getErrorMessage()
        );
    }

    @Test
    void testHandleBadJwtException() {
        BadJwtException exception = mock(BadJwtException.class);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadJwtException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(
            messageSource.getMessage("TokenInvalid", null, LocaleContextHolder.getLocale()),
            response.getBody().getErrors().get(0).getErrorMessage()
        );
    }

    @Test
    void testHandleAuthenticationException_jwtValidation() {
        JwtValidationException innerException = mock(JwtValidationException.class);
        AuthenticationException exception = new AuthenticationException("Authentication failed due to JwtValidation", innerException) {
			private static final long serialVersionUID = -5193879407090364078L;
		};

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthenticationException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(
            messageSource.getMessage("TokenExpired", null, LocaleContextHolder.getLocale()),
            response.getBody().getErrors().get(0).getErrorMessage()
        );
        assertNull(response.getBody().getErrors().get(0).getFieldName());
    }

    @Test
    void testHandleAuthenticationException_badJwt() {
        BadJwtException innerException = mock(BadJwtException.class);
        AuthenticationException exception = new AuthenticationException("Authentication failed due to BadJwt", innerException) {
			private static final long serialVersionUID = -5193879407090364078L;
		};

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthenticationException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(
            messageSource.getMessage("TokenInvalid", null, LocaleContextHolder.getLocale()),
            response.getBody().getErrors().get(0).getErrorMessage()
        );
        assertNull(response.getBody().getErrors().get(0).getFieldName());
    }

    @Test
    void testHandleAccessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("Access is denied");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(
            "Access is denied",
            response.getBody().getErrors().get(0).getErrorMessage()
        );
        assertNull(response.getBody().getErrors().get(0).getFieldName());
    }

    @Test
    void testHandleUsernameNotFoundAndBadCredentialsException() {
        UsernameNotFoundException exception = new UsernameNotFoundException("User not found");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUsernameNotFoundAndBadCredentialsException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(
            messageSource.getMessage("username.password.incorrect", null, LocaleContextHolder.getLocale()),
            response.getBody().getErrors().get(0).getErrorMessage()
        );
        assertNull(response.getBody().getErrors().get(0).getFieldName());
    }
    
    @Test
    void testHandleMethodArgumentNotValidException() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("objectName", "fieldName1", "Field1 error message");
        FieldError fieldError2 = new FieldError("objectName", "fieldName2", "Field2 error message");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(bindingResult.hasGlobalErrors()).thenReturn(true);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(2, response.getBody().getErrors().size());

        // Assertions for specific field errors
        assertEquals("Field1 error message", response.getBody().getErrors().get(0).getErrorMessage());
        assertEquals("fieldName1", response.getBody().getErrors().get(0).getFieldName());

        assertEquals("Field2 error message", response.getBody().getErrors().get(1).getErrorMessage());
        assertEquals("fieldName2", response.getBody().getErrors().get(1).getFieldName());
    }
    
    @Test
    void testHandleMethodArgumentNotValidWithGlobalError() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        when(bindingResult.hasGlobalErrors()).thenReturn(true);

        ObjectError globalError = new ObjectError("globalError", "Global error message");
        when(bindingResult.getGlobalErrors()).thenReturn(List.of(globalError));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());

        // Assertions for global error
        assertEquals("Global error message", response.getBody().getErrors().get(0).getErrorMessage());
        assertEquals(null, response.getBody().getErrors().get(0).getFieldName()); // Global errors have null field name
    }
    
    @Test
    void testHandleMethodArgumentNotValidWithFallbackMessage() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        when(bindingResult.hasGlobalErrors()).thenReturn(false);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());

        assertEquals(
            messageSource.getMessage("json.validation.failed", null, LocaleContextHolder.getLocale()),
            response.getBody().getErrors().get(0).getErrorMessage()
        );
        assertNull(response.getBody().getErrors().get(0).getFieldName());
    }
    
    @Test
    void testHandleConstraintViolationException() {
        // Mock ConstraintViolationException and its constraint violations
        ConstraintViolationException exception = mock(ConstraintViolationException.class);

        // Mock a set of constraint violations
        Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
        constraintViolations.add(mockConstraintViolation("Validation message 1", "field1"));
        constraintViolations.add(mockConstraintViolation("Validation message 2", "field2"));

        // Define behavior for the mocked exception
        when(exception.getConstraintViolations()).thenReturn(constraintViolations);

        // Invoke the handler method
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConstraintViolationException(exception);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(2, response.getBody().getErrors().size());
    }

    // Helper method to mock a ConstraintViolation
    // Correct way to mock ConstraintViolation getPropertyPath() method
    private ConstraintViolation<?> mockConstraintViolation(String message, String fieldName) {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn(message);
        // Mocking getPropertyPath() to return a Path mock
        Path pathMock = mock(Path.class);
        when(violation.getPropertyPath()).thenReturn(pathMock);
        when(pathMock.toString()).thenReturn(fieldName); // Mocking toString() of Path
        return violation;
    }
    
    
    @Test
    void testHandleHttpMessageNotReadableException() {
    	HttpInputMessage mockHttpInputMessage = mock(HttpInputMessage.class);
    	
        MismatchedInputException mockException = mock(MismatchedInputException.class);
        when(mockException.getPathReference()).thenReturn("fieldName");
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Message not readable", mockException, mockHttpInputMessage);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpMessageNotReadable(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
    }


    @Test
    void testHandleHttpMessageNotReadable_FallBack() {
    	HttpInputMessage mockHttpInputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Message not readable", mockHttpInputMessage);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpMessageNotReadable(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(
            messageSource.getMessage("json.invalid.format", null, LocaleContextHolder.getLocale()),
            response.getBody().getErrors().get(0).getErrorMessage()
        );
        assertNull(response.getBody().getErrors().get(0).getFieldName());
    }

    @Test
    void testHandleObjectNotFoundException() {
        ObjectNotFoundException exception = mock(ObjectNotFoundException.class);
        
        // Mock behavior for the exception
        String entityName = "User";
        String identifier = "123";
        when(exception.getEntityName()).thenReturn(entityName);
        when(exception.getIdentifier()).thenReturn(identifier);

        
        // Assuming you need to get entityName and identifier for message source
        // For instance, if you want to get the message key in a specific format
        String errorMessage = messageSource.getMessage(
			"NotFound", 
			new Object[]{
				entityName.substring(0, 1).toUpperCase() + entityName.substring(1), 
				identifier.toString()
			},
			LocaleContextHolder.getLocale()
		);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleObjectNotFoundException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(errorMessage, response.getBody().getErrors().get(0).getErrorMessage());
        assertEquals("id", response.getBody().getErrors().get(0).getFieldName());
    }
    
    @Test
    void testHandleMismatchedInputException() {
        MismatchedInputException exception = mock(MismatchedInputException.class);
        String pathReference = "field[\"subfield\"]"; // Example path reference
        String errorMessage = "Mismatched input";

        when(exception.getPathReference()).thenReturn(pathReference);
        when(exception.getMessage()).thenReturn(errorMessage);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMismatchedInputException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(
                messageSource.getMessage("json.invalid.value", new Object[]{ "subfield" }, LocaleContextHolder.getLocale()),
                response.getBody().getErrors().get(0).getErrorMessage()
        );
        assertEquals("subfield", response.getBody().getErrors().get(0).getFieldName());
    }

    @Test
    void testHandleUserAccountMismatchException() {
        // Create a mock UserAccountMismatchException
        String errorMessage = "Account mismatch";
        String code = "UserAccountMismatch";
        Object[] values = { "11111111" };
        String fieldName = "username";

        UserAccountMismatchException exception = new UserAccountMismatchException(errorMessage, code, values, fieldName);
        
     // Mock message source behavior
        String expectedErrorMessage = messageSource.getMessage(code, values, LocaleContextHolder.getLocale());

        // Call the handler method
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserAccountMismatchException(exception);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(expectedErrorMessage, response.getBody().getErrors().get(0).getErrorMessage());
        assertEquals(fieldName, response.getBody().getErrors().get(0).getFieldName());
    }

    @Test
    void testHandleUsernameAlreadyExistsException() {
     // Create a mock UsernameAlreadyExistsException
        String errorMessage = "Email already exists";
        String code = "DuplicateResource";
        Object[] values = { "test@email.com", "email" };
        String fieldName = "email";

        UsernameAlreadyExistsException exception = new UsernameAlreadyExistsException(errorMessage, code, values, fieldName);
        
        // Mock message source behavior
        String expectedErrorMessage = messageSource.getMessage(code, values, LocaleContextHolder.getLocale());

        // Call the handler method
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUsernameAlreadyExistsException(exception);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(expectedErrorMessage, response.getBody().getErrors().get(0).getErrorMessage());
        assertEquals(fieldName, response.getBody().getErrors().get(0).getFieldName());
    }

    @Test
    void testHandleExchangeRateException() {
    	// Create a mock UsernameAlreadyExistsException
        String errorMessage = "Failed to retrieve exchange data";
        Throwable cause = new Exception();

        ExchangeRateException exception = new ExchangeRateException(errorMessage, cause);
        
        // Call the handler method
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleExchangeRateException(exception);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertNotNull(response.getBody().getErrors().get(0).getErrorMessage());
        assertNull(response.getBody().getErrors().get(0).getFieldName());
    }

    @Test
    void testHandleInsufficientBalanceException() {
        // Mock user account and exception details
        UserAccount userAccount = new UserAccount(Currency.NGN, new User());
        userAccount.setId(1L);
        userAccount.setBalance(new BigDecimal("100.00"));
        BigDecimal amount = new BigDecimal("150.00");
        String code = "InsufficientBalance";
        String fieldName = "amount";
        Object[] values = { userAccount.getCurrency(), amount, userAccount.getId(), userAccount.getBalance() };

        // Create InsufficientBalanceException
        String errorMessage = "Insufficient balance";
        InsufficientBalanceException exception = new InsufficientBalanceException(errorMessage, code, userAccount, amount, fieldName);

        // Mock message source behavior
        String expectedErrorMessage = messageSource.getMessage(code, values, LocaleContextHolder.getLocale());

        // Call the handler method
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInsufficientBalanceException(exception);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(expectedErrorMessage, response.getBody().getErrors().get(0).getErrorMessage());
        assertEquals(fieldName, response.getBody().getErrors().get(0).getFieldName());
    }
    
    @Test
    void testHandleMissingInternalAccountException() {
        // Mock MissingInternalAccountException details
        String code = "MissingInternalAccount";
        Object[] values = new Object[] {Currency.NGN};
        String fieldName = "someField";
        String errorMessage = "Missing internal account error";

        // Create MissingInternalAccountException
        MissingInternalAccountException exception = new MissingInternalAccountException(errorMessage, code, values, fieldName);

        // Mock message source behavior
        String expectedErrorMessage = messageSource.getMessage(code, values, LocaleContextHolder.getLocale());

        // Call the handler method
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMissingInternalAccountException(exception);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(expectedErrorMessage, response.getBody().getErrors().get(0).getErrorMessage());
        assertEquals(fieldName, response.getBody().getErrors().get(0).getFieldName());
    }
    
    @Test
    void testHandleMissingInternalChargeAccountException() {
        // Mock MissingInternalAccountException details
        String code = "MissingInternalChargeAccount";
        Object[] values = new Object[] {Currency.NGN};
        String fieldName = "someField";
        String errorMessage = "Missing internal charge account error";

        // Create MissingInternalAccountException
        MissingInternalChargeAccountException exception = new MissingInternalChargeAccountException(errorMessage, code, values, fieldName);

        // Mock message source behavior
        String expectedErrorMessage = messageSource.getMessage(code, values, LocaleContextHolder.getLocale());

        // Call the handler method
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMissingInternalChargeAccountException(exception);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(expectedErrorMessage, response.getBody().getErrors().get(0).getErrorMessage());
        assertEquals(fieldName, response.getBody().getErrors().get(0).getFieldName());
    }

    @Test
    void testHandleNoResourceFoundException() {
        NoResourceFoundException exception = new NoResourceFoundException(null, "Resource not found");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNoResourceFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertNotNull(response.getBody().getErrors().get(0).getErrorMessage());
        assertNull(response.getBody().getErrors().get(0).getFieldName());
    }
    
    @Test
    void testHandleHttpRequestMethodNotSupportedException() {
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("Method not supported");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpRequestMethodNotSupportedException(exception);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertNotNull(response.getBody().getErrors().get(0).getErrorMessage());
        assertNull(response.getBody().getErrors().get(0).getFieldName());
    }

    @Test
    void testHandleMissingPathVariable() {
    	MissingPathVariableException exception = new MissingPathVariableException("Missing path variable", null);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMissingPathVariable(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertNotNull(response.getBody().getErrors().get(0).getErrorMessage());
        assertNull(response.getBody().getErrors().get(0).getFieldName());
    }
    
    @Test
    void testHandleTypeMismatch() {
        // Mock the MethodArgumentTypeMismatchException
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("id");
        when(exception.getValue()).thenReturn("stringValue");
        when(exception.getRequiredType()).thenAnswer(invocation -> String.class);

        // Call the method under test
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTypeMismatch(exception);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertNotNull(response.getBody().getErrors().get(0).getErrorMessage());
        assertEquals("id", response.getBody().getErrors().get(0).getFieldName());
    }
    
    @Test
    void testHandleIllegalArgumentException() {
    	IllegalArgumentException exception = new IllegalArgumentException("Illegal Argument");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertNotNull(response.getBody().getErrors().get(0).getErrorMessage());
    }
    
    @Test
    void testTokenRefreshException() {
    	TokenRefreshException exception = new TokenRefreshException("token","token is expired");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTokenRefreshException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertNotNull(response.getBody().getErrors().get(0).getErrorMessage());
        assertEquals("refreshToken", response.getBody().getErrors().get(0).getFieldName());
    }
    
    @Test
    void testHandleMissingServletRequestParameterException() {
    	MissingServletRequestParameterException exception = new MissingServletRequestParameterException("token", "fieldName");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMissingServletRequestParameterException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(exception.getMessage(), response.getBody().getErrors().get(0).getErrorMessage());
        assertEquals(exception.getParameterName(), response.getBody().getErrors().get(0).getFieldName());
    }

    @Test
    void testHandleOtherExceptions() {
        Exception exception = new Exception("Internal server error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleOtherExceptions(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(
        	messageSource.getMessage("InternalServerError", null, LocaleContextHolder.getLocale()),
        	response.getBody().getErrors().get(0).getErrorMessage()
        );
        assertNull(response.getBody().getErrors().get(0).getFieldName());
    }
}
