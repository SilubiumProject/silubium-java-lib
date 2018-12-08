package com.spark.bc.wallet.api.entity.slu;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author shenzucai
 * @time 2018.12.04 11:05
 */
@Data
@NoArgsConstructor
public class Balance {

    /**
     * address : SLSQWxnqf7fZ7ZPYYYE1Q9a7DQvU8NpnMK8o
     * balance : 4865.73313043
     */

    private String address;
    private BigDecimal balance;
}
