package com.spark.bc.wallet.api.entity.slu;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author shenzucai
 * @time 2018.12.05 09:31
 */
@NoArgsConstructor
@Data
public class VoutBean {
    /**
     * value : 0.00000000
     * n : 0
     * scriptPubKey : {"hex":"","asm":""}
     * spentTxId : null
     * spentIndex : null
     * spentHeight : null
     */

    private BigDecimal value;
    private int n;
    private ScriptPubKeyBean scriptPubKey;
    private Object spentTxId;
    private Object spentIndex;
    private Object spentHeight;
}
