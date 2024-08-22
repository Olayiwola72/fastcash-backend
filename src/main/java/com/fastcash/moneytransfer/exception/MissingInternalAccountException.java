package com.fastcash.moneytransfer.exception;

public class MissingInternalAccountException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    
	private final String code;
	private final Object[] values;
	private final String fieldName;
    
    public MissingInternalAccountException(String message, String code, Object[] values, String fieldName) {
        super(message);
        this.code = code;
        this.values = values;
        this.fieldName = fieldName;
    }

	public String getCode() {
		return code;
	}

	public Object[] getValues() {
		return values;
	}

	public String getFieldName() {
		return fieldName;
	}
}