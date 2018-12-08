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
public class Transaction {

    /**
     * txid : 6a9fa5dc32910a4d12cc9c14efb757f68ea29c6fb3055d9fb4631fc763ab741b
     * version : 1
     * locktime : 0
     * receipt : [{"blockHash":"fd15378467e1423e55d7c748f6cb4038ba5cf0dc69d945465f33ee4a26e4104b","blockNumber":84060,"transactionHash":"6a9fa5dc32910a4d12cc9c14efb757f68ea29c6fb3055d9fb4631fc763ab741b","transactionIndex":3,"from":"SLScZFoQ3KAgoVng8vqsWnYKgWQWEKkMdTvm","to":"1e88227d9f21cd26ee06f0f1473e119bbf392fc0","cumulativeGasUsed":51328,"gasUsed":51328,"contractAddress":"1e88227d9f21cd26ee06f0f1473e119bbf392fc0","excepted":"None","log":[{"address":"1e88227d9f21cd26ee06f0f1473e119bbf392fc0","topics":["ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef","SLScZFoQ3KAgoVng8vqsWnYKgWQWEKkMdTvm","SLSQWxnqf7fZ7ZPYYYE1Q9a7DQvU8NpnMK8o"],"data":"0000000000000000000000000000000000000000000000000000000000989680"}]}]
     * issrc20Transfer : true
     * vin : [{"txid":"94e09da456e51eb16b42ffc2cf26ffcd8076c83df1a1535fec31dc14e1ac4b8a","vout":1,"sequence":4294967295,"n":0,"scriptSig":{"hex":"473044022011039a801331307ea967f2a65b04e3fdb1b9bbef34596cbf239fecd5c799d57402205fa77a51ffa14460b2bd0c09d3a9bfaecdd0442206519e5313c65366e8d01369012103f56d823e53e69be9137b0d20702d682cc948b10b33e7c6f07a68922b26a8488a","asm":"3044022011039a801331307ea967f2a65b04e3fdb1b9bbef34596cbf239fecd5c799d57402205fa77a51ffa14460b2bd0c09d3a9bfaecdd0442206519e5313c65366e8d01369[ALL] 03f56d823e53e69be9137b0d20702d682cc948b10b33e7c6f07a68922b26a8488a"},"addr":"SLScZFoQ3KAgoVng8vqsWnYKgWQWEKkMdTvm","valueSat":7314867157,"value":73.14867157,"doubleSpentTxID":null}]
     * vout : [{"value":"0.00000000","n":0,"scriptPubKey":{"hex":"540390d003012844a9059cbb000000000000000000000000235be9efcc20ec3238eebe3a9f54158f640523060000000000000000000000000000000000000000000000000000000000989680141e88227d9f21cd26ee06f0f1473e119bbf392fc0c2","asm":"4 250000 40 a9059cbb000000000000000000000000235be9efcc20ec3238eebe3a9f54158f640523060000000000000000000000000000000000000000000000000000000000989680 1e88227d9f21cd26ee06f0f1473e119bbf392fc0 OP_CALL","addresses":["SLSQ5SPQMp8jV4bXQyXDkNswai2MGuJwZUw1"],"type":"pubkeyhash"},"spentTxId":null,"spentIndex":null,"spentHeight":null},{"value":"73.04857157","n":1,"scriptPubKey":{"hex":"76a914a76c743d9cac11a1f9eb5b3c8ebea31d366a10b988ac","asm":"OP_DUP OP_HASH160 a76c743d9cac11a1f9eb5b3c8ebea31d366a10b9 OP_EQUALVERIFY OP_CHECKSIG","addresses":["SLScZFoQ3KAgoVng8vqsWnYKgWQWEKkMdTvm"],"type":"pubkeyhash"},"spentTxId":null,"spentIndex":null,"spentHeight":null}]
     * blockhash : fd15378467e1423e55d7c748f6cb4038ba5cf0dc69d945465f33ee4a26e4104b
     * blockheight : 84060
     * confirmations : 518
     * time : 1543932128
     * blocktime : 1543932128
     * valueOut : 73.04857157
     * size : 298
     * valueIn : 73.14867157
     * fees : 0.1001
     */

    private String txid;
    private int version;
    private int locktime;
    private boolean issrc20Transfer;
    private String blockhash;
    private int blockheight;
    private int confirmations;
    private int time;
    private int blocktime;
    private double valueOut;
    private int size;
    private double valueIn;
    private double fees;
    private List<ReceiptBean> receipt;
    private List<VinBean> vin;
    private List<VoutBean> vout;
}
