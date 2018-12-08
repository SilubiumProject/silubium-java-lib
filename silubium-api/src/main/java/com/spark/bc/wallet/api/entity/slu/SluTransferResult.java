package com.spark.bc.wallet.api.entity.slu;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author shenzucai
 * @time 2018.12.06 17:46
 */
@Data
public class SluTransferResult {

    private String address;

    private BigDecimal amount;
}
