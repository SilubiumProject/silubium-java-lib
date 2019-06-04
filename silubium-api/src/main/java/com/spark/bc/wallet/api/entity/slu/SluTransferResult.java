package com.spark.bc.wallet.api.entity.slu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author shenzucai
 * @time 2018.12.06 17:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SluTransferResult {

    private String address;

    private BigDecimal amount;
}
