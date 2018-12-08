package com.spark.bc.wallet.api.entity.slu;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author shenzucai
 * @time 2018.12.05 09:31
 */
@Data
@NoArgsConstructor
public class VinBean {
    /**
     * coinbase : 0367390100
     * sequence : 4294967295
     * n : 0
     */

    private String coinbase;
    private long sequence;
    private int n;


    /**
     * txid : d230a161f8439570e4fdeaed87a689bf06ed279415d224824278ab93d894994a
     * vout : 1
     * scriptSig : {"hex":"483045022100e9d1ab39ee96e605fe3c8846c6fb53b05ace64ea7e3d13ecc176a23a4c89828102207837ac1e185ca70acbac317d851643cca908c475c020a8913f4dc04ddae54f6c01","asm":"3045022100e9d1ab39ee96e605fe3c8846c6fb53b05ace64ea7e3d13ecc176a23a4c89828102207837ac1e185ca70acbac317d851643cca908c475c020a8913f4dc04ddae54f6c[ALL]"}
     * addr : SLST1eYGe4Fqn1RptiX6re8i4A5QD7LBuJ6w
     * valueSat : 62519000000
     * value : 625.19
     * doubleSpentTxID : null
     */

    private String txid;
    private int vout;
    private ScriptSigBean scriptSig;
    private String addr;
    private long valueSat;
    private BigDecimal value;
    private Object doubleSpentTxID;
}
