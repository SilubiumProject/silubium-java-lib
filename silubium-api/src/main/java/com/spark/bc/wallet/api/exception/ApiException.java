package com.spark.bc.wallet.api.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * excepiton
 * @author shenzucai
 * @time 2018.12.04 10:23
 */
public class ApiException extends RuntimeException {


	private static final long serialVersionUID = 1L;

	private ApiError error;

	public ApiException(){};

	public ApiException(ApiError apiError) {
		this.error = apiError;
	}

	public ApiException(Throwable cause) {
		super(cause);
	}


	public ApiError getError() {
		return error;
	}

	public void setError(ApiError error) {
		this.error = error;
	}

	@Override
	public String getMessage() {
		if (error != null) {
			return error.toString();
		}
		return super.getMessage();
	}
}
