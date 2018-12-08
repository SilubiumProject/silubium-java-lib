package com.spark.bc.wallet.api.entity.slu.history;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author shenzucai
 * @time 2018.12.05 18:50
 */
@NoArgsConstructor
@Data
public class LogBean {
    /**
     * address : 1e88227d9f21cd26ee06f0f1473e119bbf392fc0
     * topics : ["ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef","SLScZFoQ3KAgoVng8vqsWnYKgWQWEKkMdTvm","SLSQWxnqf7fZ7ZPYYYE1Q9a7DQvU8NpnMK8o"]
     * data : 0000000000000000000000000000000000000000000000000000000000989680
     */

    private String address;
    private String data;
    private List<String> topics;
}
