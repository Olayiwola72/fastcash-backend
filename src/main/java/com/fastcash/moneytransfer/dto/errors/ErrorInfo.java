package com.fastcash.moneytransfer.dto.errors;

import jakarta.validation.constraints.NotEmpty;

public interface ErrorInfo {
	@NotEmpty
    String getErrorMessage();
    String getFieldName();
}