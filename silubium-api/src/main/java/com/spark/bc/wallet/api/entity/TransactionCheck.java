package com.spark.bc.wallet.api.entity;

import lombok.Data;
import org.bitcoinj.core.Transaction;
/**
 * 比对交易信息，便于扩展
 * @author shenzucai
 * @time 2018.12.20 10:11
 */
@Data
public class TransactionCheck {
    private Transaction transaction;
    private String transactionBytes;
}
