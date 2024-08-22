package com.fastcash.moneytransfer.dto.errors;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Error Response Information")
public class ErrorResponse {
	@Schema(description = "errors")
	@NotNull
	private List<ErrorInfo> errors;

    public ErrorResponse() {
        this.errors = new ArrayList<>();
    }
    
    public ErrorResponse(String errorMessage) {
        this();
        ErrorInfo errorInfo = new ErrorField(errorMessage);
        this.addErrors(errorInfo);
    }

    public ErrorResponse(String errorMessage, String fieldName) {
        this();
        ErrorInfo errorInfo = new ErrorField(errorMessage, fieldName);
        this.addErrors(errorInfo);
    }

    public List<ErrorInfo> getErrors() {
        return this.errors;
    }

    public void addErrors(ErrorInfo errorInfo) {
        this.errors.add(errorInfo);
    }
}
