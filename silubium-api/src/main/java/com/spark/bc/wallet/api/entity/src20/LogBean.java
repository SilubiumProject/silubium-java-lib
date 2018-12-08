package com.spark.bc.wallet.api.entity.src20;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author shenzucai
 * @time 2018.12.05 09:31
 */
@NoArgsConstructor
@Data
public class LogBean {
    /**
     * address : 1e88227d9f21cd26ee06f0f1473e119bbf392fc0
     * topics : ["ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef","SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm","SLSatkvYJEtLnAB1WQ5MLgZemm9qCAYZDULV"]
     * data : 00000000000000000000000000000000000000000000000000000002540be400
     */

    private String address;
    private String data;
    private List<String> topics;
}
