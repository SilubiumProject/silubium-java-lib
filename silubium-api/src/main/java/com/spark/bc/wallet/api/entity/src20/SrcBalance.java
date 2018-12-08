package com.spark.bc.wallet.api.entity.src20;

import com.spark.bc.wallet.api.entity.slu.Balance;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author shenzucai
 * @time 2018.12.04 20:19
 */
@NoArgsConstructor
@Data
public class SrcBalance {

    /**
     * contractAddress : 1e88227d9f21cd26ee06f0f1473e119bbf392fc0
     * balances : [{"address":"SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm","balance":9997770689}]
     */

    private String contractAddress;
    private List<Balance> balances;
}
