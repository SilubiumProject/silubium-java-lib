package com.spark.bc.wallet.api.contract;

import com.spark.bc.wallet.api.entity.TransactionCheck;

public class TransactionHashWithSender {
    String transactionHash;
    String sender;
    TransactionCheck transactionCheck;

    public TransactionHashWithSender(String transactionHash, String sender,TransactionCheck transactionCheck) {
        this.transactionHash = transactionHash;
        this.sender = sender;
        this.transactionCheck = transactionCheck;
    }

    public String getSender() {
        return sender;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public TransactionCheck getTransactionCheck() {
        return transactionCheck;
    }
}
