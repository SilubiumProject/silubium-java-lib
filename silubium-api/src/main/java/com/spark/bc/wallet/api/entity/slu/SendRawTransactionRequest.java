package com.spark.bc.wallet.api.entity.slu;

import lombok.Data;

/**
 * @author shenzucai
 * @time 2018.12.05 09:53
 */
@Data
public class SendRawTransactionRequest {
    private String rawtx;
    private Boolean allowAbsurdFees;
}
