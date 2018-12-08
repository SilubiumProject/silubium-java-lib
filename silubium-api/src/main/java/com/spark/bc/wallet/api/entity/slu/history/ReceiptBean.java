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
public class ReceiptBean {
    /**
     * blockHash : fd15378467e1423e55d7c748f6cb4038ba5cf0dc69d945465f33ee4a26e4104b
     * blockNumber : 84060
     * transactionHash : 6a9fa5dc32910a4d12cc9c14efb757f68ea29c6fb3055d9fb4631fc763ab741b
     * transactionIndex : 3
     * from : SLScZFoQ3KAgoVng8vqsWnYKgWQWEKkMdTvm
     * to : 1e88227d9f21cd26ee06f0f1473e119bbf392fc0
     * cumulativeGasUsed : 51328
     * gasUsed : 51328
     * contractAddress : 1e88227d9f21cd26ee06f0f1473e119bbf392fc0
     * excepted : None
     * log : [{"address":"1e88227d9f21cd26ee06f0f1473e119bbf392fc0","topics":["ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef","SLScZFoQ3KAgoVng8vqsWnYKgWQWEKkMdTvm","SLSQWxnqf7fZ7ZPYYYE1Q9a7DQvU8NpnMK8o"],"data":"0000000000000000000000000000000000000000000000000000000000989680"}]
     */

    private String blockHash;
    private int blockNumber;
    private String transactionHash;
    private int transactionIndex;
    private String from;
    private String to;
    private int cumulativeGasUsed;
    private int gasUsed;
    private String contractAddress;
    private String excepted;
    private List<LogBean> log;
}
