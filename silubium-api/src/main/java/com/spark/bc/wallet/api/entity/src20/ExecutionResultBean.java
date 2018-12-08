package com.spark.bc.wallet.api.entity.src20;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2018.12.06 08:38
 */
@NoArgsConstructor
@Data
public class ExecutionResultBean {
    /**
     * gasUsed : 39999999
     * excepted : BadInstruction
     * newAddress : 1e88227d9f21cd26ee06f0f1473e119bbf392fc0
     * output :
     * codeDeposit : 0
     * gasRefunded : 0
     * depositSize : 0
     * gasForDeposit : 0
     */

    private int gasUsed;
    private String excepted;
    private String newAddress;
    private String output;
    private int codeDeposit;
    private int gasRefunded;
    private int depositSize;
    private int gasForDeposit;
}
