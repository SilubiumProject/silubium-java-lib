package com.spark.bc.wallet.api.exception;

import lombok.Data;

/**
 * error
 * @author shenzucai
 * @time 2018.12.04 10:23
 */
@Data
public class ApiError {

	private String status;

	private String url;

	private String error;

}
