package com.fastcash.moneytransfer.exception;

import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Profile;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fastcash.moneytransfer.dto.errors.ErrorField;
import com.fastcash.moneytransfer.dto.errors.ErrorResponse;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private final ReloadableResourceBundleMessageSource messageSource;

	public GlobalExceptionHandler(ReloadableResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;		
	}
	
	@ExceptionHandler(JwtValidationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleJwtValidationException(JwtValidationException ex) {
		String errorMessage = messageSource.getMessage("TokenExpired", null, LocaleContextHolder.getLocale());
		
		ErrorResponse errorResponse = new ErrorResponse(errorMessage);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}
	
	@ExceptionHandler(BadJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleBadJwtException(BadJwtException ex) {
		String errorMessage = messageSource.getMessage("TokenInvalid", null, LocaleContextHolder.getLocale());
		
		ErrorResponse errorResponse = new ErrorResponse(errorMessage);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}
	
	@ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {        		
		Throwable cause = ex.getCause();
		String errorMessage;
		
		// Check the cause to determine the error message
		// We check for JwtValidationException first to ensure that if the cause is both a JwtValidationException
		// and a BadJwtException (or they share a common superclass), we prioritize setting the error message 
		// for JwtValidationException, as it is more specific.
	    if (cause instanceof JwtValidationException) {
	        JwtValidationException jwtValidationException = (JwtValidationException) cause;
	        return handleJwtValidationException(jwtValidationException);
	    } else if (cause instanceof BadJwtException) {
	    	BadJwtException badJwtException = (BadJwtException) cause;
	        return handleBadJwtException(badJwtException);
	    }else {
	    	errorMessage = ex.getMessage();
	    }
	    
        ErrorResponse errorResponse = new ErrorResponse(errorMessage);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
	
	@ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
	
	@ExceptionHandler({ UsernameNotFoundException.class, BadCredentialsException.class })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundAndBadCredentialsException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(messageSource.getMessage("username.password.incorrect", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		BindingResult result = ex.getBindingResult();
		
		List<FieldError> fieldErrors = result.getFieldErrors();
		if(!fieldErrors.isEmpty()) {
			ErrorResponse errorResponse = new ErrorResponse(); 
			
			 for(FieldError fieldError : fieldErrors) {
				 String fieldName = fieldError.getField();
				 String errorCode = fieldError.getCode();
				 String errorMessage = fieldError.getDefaultMessage();
				 Object rejectedValue = fieldError.getRejectedValue();
				 if(rejectedValue != null) rejectedValue = rejectedValue.toString();
				 
				 try{
					 errorMessage = messageSource.getMessage(
	 					errorCode, 
	 					new Object[]{
							fieldName, 
							rejectedValue
						},
	 					LocaleContextHolder.getLocale()
			 		);
				 }catch(NoSuchMessageException e) {}
 
				 ErrorField errorField = new ErrorField(errorMessage, fieldName);
				 errorResponse.addErrors(errorField);
			 }
			 
			 return ResponseEntity.badRequest().body(errorResponse);
		}else if(result.hasGlobalErrors()){
				ErrorResponse errorResponse = new ErrorResponse();
				
			    for (ObjectError error : result.getGlobalErrors()) {
			        ErrorField errorField = new ErrorField(error.getDefaultMessage(), null);
					errorResponse.addErrors(errorField);
			    }
			    
			    return ResponseEntity.badRequest().body(errorResponse);
		}else {
			ErrorResponse errorResponse = new ErrorResponse(messageSource.getMessage("json.validation.failed", null, LocaleContextHolder.getLocale()));
	        return ResponseEntity.badRequest().body(errorResponse);
		}
    }
	
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
		ErrorResponse errorResponse = new ErrorResponse();
    	
    	for(ConstraintViolation<?> constraintViolation: ex.getConstraintViolations()) {
    		String errorMessage = constraintViolation.getMessage();
    		String fieldName = constraintViolation.getPropertyPath().toString().isBlank() ? null : constraintViolation.getPropertyPath().toString();
    		
    		ErrorField errorField = new ErrorField(errorMessage, fieldName);
    		errorResponse.addErrors(errorField);
    	}
    	
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		Throwable cause = ex.getCause();

        if (cause instanceof MismatchedInputException) {
        	MismatchedInputException mismatchedInputException = (MismatchedInputException) cause;
        	return handleMismatchedInputException(mismatchedInputException);
        }
        
        if (cause instanceof ValueInstantiationException) {
        	ErrorResponse errorResponse = new ErrorResponse(ex.getMostSpecificCause().getMessage()); 
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        
        // JsonParseException
		ErrorResponse errorResponse = new ErrorResponse(messageSource.getMessage("json.invalid.format", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
	
	@ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleObjectNotFoundException(ObjectNotFoundException ex) {
		String entityName = ex.getEntityName();
      
        String errorMessage = messageSource.getMessage(
			"NotFound", 
			new Object[]{
				entityName.substring(0, 1).toUpperCase() + entityName.substring(1), 
				ex.getIdentifier().toString()
			},
			LocaleContextHolder.getLocale()
		);
		
		ErrorResponse errorResponse = new ErrorResponse(errorMessage, "id");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
	
	@ExceptionHandler(MismatchedInputException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMismatchedInputException(MismatchedInputException ex) {
		String pathReference = ex.getPathReference(); // Get the path reference
        String fieldName = extractFieldName(pathReference); // Extract the field name
        String errorMessage = ex.getMessage();
        		
        if(fieldName != null) {
        	errorMessage = messageSource.getMessage(
					"json.invalid.value", 
					new Object[]{
							fieldName
						},
					LocaleContextHolder.getLocale()
			);
        }
        
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, fieldName); 
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
	
	@ExceptionHandler(UserAccountMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleUserAccountMismatchException(UserAccountMismatchException ex) {
		
		String errorMessage = messageSource.getMessage(
				ex.getCode(), 
				ex.getValues(),
				LocaleContextHolder.getLocale()
		);
		
		ErrorResponse errorResponse = new ErrorResponse(errorMessage, ex.getFieldName());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
	
	@ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
		
		String errorMessage = messageSource.getMessage(
				ex.getCode(), 
				ex.getValues(),
				LocaleContextHolder.getLocale()
		);
		
		ErrorResponse errorResponse = new ErrorResponse(errorMessage, ex.getFieldName());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
	
	@ExceptionHandler(ExchangeRateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleExchangeRateException(ExchangeRateException ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
	
	@ExceptionHandler(InsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(InsufficientBalanceException ex) {
		String errorMessage = ex.getMessage();
		
		UserAccount userAccount = ex.getAccount();
		 
		try{
			 errorMessage = messageSource.getMessage(
				ex.getCode(), 
				new Object[]{
							userAccount.getCurrency(),
							ex.getAmount(),
							userAccount.getId().toString(),
							userAccount.getBalance()
						},
				LocaleContextHolder.getLocale()
	 		);
		 }catch(NoSuchMessageException e) {}
		 
		ErrorResponse errorResponse = new ErrorResponse(errorMessage,ex.getFieldName());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
	
	@ExceptionHandler(MissingInternalAccountException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMissingInternalAccountException(MissingInternalAccountException ex) {
		
		String errorMessage = messageSource.getMessage(
				ex.getCode(), 
				ex.getValues(),
				LocaleContextHolder.getLocale()
		);
		
		ErrorResponse errorResponse = new ErrorResponse(errorMessage, ex.getFieldName());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
	
	@ExceptionHandler(MissingInternalChargeAccountException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMissingInternalChargeAccountException(MissingInternalChargeAccountException ex) {
		
		String errorMessage = messageSource.getMessage(
				ex.getCode(), 
				ex.getValues(),
				LocaleContextHolder.getLocale()
		);
		
		ErrorResponse errorResponse = new ErrorResponse(errorMessage, ex.getFieldName());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
	
	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage()); 
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage()); 
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
	}
	
	@ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorResponse> handleMissingPathVariable(MissingPathVariableException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Path parameter is missing: " + ex.getVariableName()); 
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		String fieldName = ex.getName();
		
        String errorMessage = String.format("Invalid path variable '%s'. Expected type '%s'. Please check the request URL", 
        		fieldName, ex.getRequiredType().getSimpleName());
        
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, fieldName); 
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
	
	@ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage()); 
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
	
	@ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ErrorResponse> handleTokenRefreshException(TokenRefreshException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "refreshToken"); 
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getParameterName()); 
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
	
	@Profile("prod")
	@ExceptionHandler(Exception.class)
   	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse(messageSource.getMessage("InternalServerError", null, LocaleContextHolder.getLocale())); 
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
	
	private String extractFieldName(String pathReference) {
        int startIndex = pathReference.indexOf("[\"");
        int endIndex = pathReference.indexOf("\"]");
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return pathReference.substring(startIndex + 2, endIndex);
        }
        return null;
    }
}
