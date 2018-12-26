package com.spark.bc.wallet.api.contract;
import com.spark.bc.wallet.api.entity.TransactionCheck;
import com.spark.bc.wallet.api.entity.slu.UTXO;
import com.spark.bc.wallet.api.util.CurrentNetParams;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptChunk;
import org.bitcoinj.script.ScriptOpCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.*;


public class ContractBuilder {

    private String hashPattern = "0000000000000000000000000000000000000000000000000000000000000000"; //64b

    private Logger logger = LoggerFactory.getLogger(ContractBuilder.class);

    private final int radix = 16;
    private final String TYPE_INT = "int";
    private final String TYPE_STRING = "string";
    private final String TYPE_ADDRESS = "address";
    private final String TYPE_BOOL = "bool";

    private final String ARRAY_PARAMETER_CHECK_PATTERN = ".*?\\d+\\[\\d*\\]";
    private final String ARRAY_PARAMETER_TYPE = "(.*?\\d+)\\[(\\d*)\\]";

    final int OP_PUSHDATA_1 = 1;
    final int OP_PUSHDATA_4 = 0x04;
    final int OP_PUSHDATA_8 = 8;
    final int OP_EXEC = 193;
    final int OP_EXEC_ASSIGN = 194;
    final int OP_EXEC_SPEND = 195;

    public ContractBuilder() {
    }

    public Script createConstructScript(String abiParams, int gasLimitInt, int gasPriceInt) {
        byte[] version = Hex.decode("04000000");
        byte[] arrayGasLimit = org.spongycastle.util.Arrays.reverse((new BigInteger(String.valueOf(gasLimitInt))).toByteArray());
        byte[] gasLimit = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        System.arraycopy(arrayGasLimit, 0, gasLimit, 0, arrayGasLimit.length);
        byte[] arrayGasPrice = org.spongycastle.util.Arrays.reverse((new BigInteger(String.valueOf(gasPriceInt))).toByteArray());
        byte[] gasPrice = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        System.arraycopy(arrayGasPrice, 0, gasPrice, 0, arrayGasPrice.length);
        byte[] data = Hex.decode(abiParams);
        byte[] program;
        ScriptChunk versionChunk = new ScriptChunk(OP_PUSHDATA_4, version);
        ScriptChunk gasLimitChunk = new ScriptChunk(OP_PUSHDATA_8, gasLimit);
        ScriptChunk gasPriceChunk = new ScriptChunk(OP_PUSHDATA_8, gasPrice);
        ScriptChunk dataChunk = new ScriptChunk(ScriptOpCodes.OP_PUSHDATA2, data);
        ScriptChunk opExecChunk = new ScriptChunk(OP_EXEC, null);
        List<ScriptChunk> chunkList = new ArrayList<>();
        chunkList.add(versionChunk);
        chunkList.add(gasLimitChunk);
        chunkList.add(gasPriceChunk);
        chunkList.add(dataChunk);
        chunkList.add(opExecChunk);
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            for (ScriptChunk chunk : chunkList) {
                chunk.write(bos);
            }
            program = bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Script(program);
    }

    public Script createMethodScript(String abiParams, int gasLimitInt, int gasPriceInt, String _contractAddress) throws RuntimeException {
        byte[] version = Hex.decode("04000000");
        byte[] arrayGasLimit = org.spongycastle.util.Arrays.reverse((new BigInteger(String.valueOf(gasLimitInt))).toByteArray());
        byte[] gasLimit = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        System.arraycopy(arrayGasLimit, 0, gasLimit, 0, arrayGasLimit.length);
        byte[] arrayGasPrice = org.spongycastle.util.Arrays.reverse((new BigInteger(String.valueOf(gasPriceInt))).toByteArray());
        byte[] gasPrice = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        System.arraycopy(arrayGasPrice, 0, gasPrice, 0, arrayGasPrice.length);
        byte[] data = Hex.decode(abiParams);
        byte[] contractAddress = Hex.decode(_contractAddress);
        byte[] program;
        ScriptChunk versionChunk = new ScriptChunk(OP_PUSHDATA_4, version);
        ScriptChunk gasLimitChunk = new ScriptChunk(OP_PUSHDATA_8, gasLimit);
        ScriptChunk gasPriceChunk = new ScriptChunk(OP_PUSHDATA_8, gasPrice);
        ScriptChunk dataChunk = new ScriptChunk(ScriptOpCodes.OP_PUSHDATA2, data);
        ScriptChunk contactAddressChunk = new ScriptChunk(ScriptOpCodes.OP_PUSHDATA2, contractAddress);
        ScriptChunk opExecChunk = new ScriptChunk(OP_EXEC_ASSIGN, null);
        List<ScriptChunk> chunkList = new ArrayList<>();
        chunkList.add(versionChunk);
        chunkList.add(gasLimitChunk);
        chunkList.add(gasPriceChunk);
        chunkList.add(dataChunk);
        chunkList.add(contactAddressChunk);
        chunkList.add(opExecChunk);
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            for (ScriptChunk chunk : chunkList) {
                chunk.write(bos);
            }
            program = bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Script(program);
    }

    public TransactionCheck createTransactionHash(Script script, List<UTXO> unspentOutputs,List<ECKey> ecKeys, int gasLimit, int gasPrice, BigDecimal feePerKb, String feeString, String sendToContractString) throws Exception {
        Transaction transaction = new Transaction(CurrentNetParams.getNetParams());

        BigDecimal fee = new BigDecimal(feeString);
        BigDecimal gasFee = (new BigDecimal(gasLimit)).multiply(new BigDecimal(gasPrice)).divide(new BigDecimal(100000000), MathContext.DECIMAL128);
        BigDecimal totalAmount = fee.add(gasFee);
        BigDecimal amountFromOutput = new BigDecimal("0.0");
        BigDecimal overFlow = new BigDecimal("0.0");
        BigDecimal bitcoin = new BigDecimal(100000000);

        if (sendToContractString.isEmpty()) {
            transaction.addOutput(Coin.ZERO, script);
        } else {
            BigDecimal sendToContract = new BigDecimal(sendToContractString).multiply(bitcoin);
            transaction.addOutput(Coin.valueOf((long) (sendToContract.doubleValue())), script);
            totalAmount = totalAmount.add(sendToContract);
        }

        for (UTXO unspentOutput : unspentOutputs) {
            overFlow = overFlow.add(unspentOutput.getAmount());
            if (overFlow.doubleValue() >= totalAmount.doubleValue()) {
                break;
            }
        }
        if (overFlow.doubleValue() < totalAmount.doubleValue()) {
            throw new RuntimeException("You have insufficient funds for this transaction");
        }
        BigDecimal delivery = overFlow.subtract(totalAmount);

        Address myAddress;
        try {
            myAddress = Address.fromBase58(CurrentNetParams.getNetParams(), unspentOutputs.get(0).getAddress());
        } catch (AddressFormatException a) {
            throw new Exception("地址错误");
        }
        if (delivery.doubleValue() != 0.0) {
            transaction.addOutput(Coin.valueOf((long) (delivery.multiply(bitcoin).doubleValue())), myAddress);
        }
        for (UTXO unspentOutput : unspentOutputs) {
            for (ECKey deterministicKey : ecKeys) {
                if (deterministicKey.toAddress(CurrentNetParams.getNetParams()).toString().equals(unspentOutput.getAddress())) {
                    Sha256Hash sha256Hash = Sha256Hash.wrap(Utils.parseAsHexOrBase58(unspentOutput.getTxid()));
                    TransactionOutPoint outPoint = new TransactionOutPoint(CurrentNetParams.getNetParams(), unspentOutput.getVout(), sha256Hash);
                    Script script2 = new Script(Utils.parseAsHexOrBase58(unspentOutput.getScriptPubKey()));
                    transaction.addSignedInput(outPoint, script2, deterministicKey, Transaction.SigHash.ALL, true);
                    amountFromOutput = amountFromOutput.add(unspentOutput.getAmount());
                    break;
                }
            }
            if (amountFromOutput.doubleValue() >= totalAmount.doubleValue()) {
                break;
            }
        }
        transaction.getConfidence(new Context(CurrentNetParams.getNetParams())).setSource(TransactionConfidence.Source.SELF);
        transaction.setPurpose(Transaction.Purpose.USER_PAYMENT);
        byte[] bytes = transaction.unsafeBitcoinSerialize();
        int txSizeInkB = (int) Math.ceil(bytes.length / 1024.);
        BigDecimal minimumFee = (feePerKb.multiply(new BigDecimal(txSizeInkB)));
        logger.info("最小手续费 {}  transaction size kb {}", minimumFee.toString(), txSizeInkB);
        if (minimumFee.doubleValue() > fee.doubleValue()) {
            throw new Exception("手续费不足");
        }

        TransactionCheck transactionCheck = new TransactionCheck();
        transactionCheck.setTransaction(transaction);
        transactionCheck.setTransactionBytes(Hex.toHexString(bytes));
        return transactionCheck;
    }

    public TransactionHashWithSender createTransactionHashForCreateContract(Script script, List<UTXO> unspentOutputs,List<ECKey> ecKeys, int gasLimit, int gasPrice, BigDecimal feePerKb, String feeString) throws Exception {
        Transaction transaction = new Transaction(CurrentNetParams.getNetParams());
        transaction.addOutput(Coin.ZERO, script);
        BigDecimal fee = new BigDecimal(feeString);
        BigDecimal gasFee = (new BigDecimal(gasLimit)).multiply(new BigDecimal(gasPrice)).divide(new BigDecimal(100000000), MathContext.DECIMAL128);
        BigDecimal totalFee = fee.add(gasFee);
        BigDecimal amountFromOutput = new BigDecimal("0.0");
        BigDecimal overFlow = new BigDecimal("0.0");
        for (UTXO unspentOutput : unspentOutputs) {
            overFlow = overFlow.add(unspentOutput.getAmount());
            if (overFlow.doubleValue() >= totalFee.doubleValue()) {
                break;
            }
        }
        if (overFlow.doubleValue() < totalFee.doubleValue()) {
            throw new RuntimeException("You have insufficient funds for this transaction");
        }
        BigDecimal delivery = overFlow.subtract(totalFee);
        BigDecimal bitcoin = new BigDecimal(100000000);
        Address myAddress;
        try {
            myAddress = Address.fromBase58(CurrentNetParams.getNetParams(), unspentOutputs.get(0).getAddress());
        } catch (AddressFormatException a) {
            throw new Exception("地址错误");
        }
        if (delivery.doubleValue() != 0.0) {
            transaction.addOutput(Coin.valueOf((long) (delivery.multiply(bitcoin).doubleValue())), myAddress);
        }
        for (UTXO unspentOutput : unspentOutputs) {

            for (ECKey deterministicKey : ecKeys) {
                if (deterministicKey.toAddress(CurrentNetParams.getNetParams()).toString().equals(unspentOutput.getAddress())) {
                    Sha256Hash sha256Hash = Sha256Hash.wrap(Utils.parseAsHexOrBase58(unspentOutput.getTxid()));
                    TransactionOutPoint outPoint = new TransactionOutPoint(CurrentNetParams.getNetParams(), unspentOutput.getVout(), sha256Hash);
                    Script script2 = new Script(Utils.parseAsHexOrBase58(unspentOutput.getScriptPubKey()));
                    transaction.addSignedInput(outPoint, script2, deterministicKey, Transaction.SigHash.ALL, true);
                    amountFromOutput = amountFromOutput.add(unspentOutput.getAmount());
                    break;
                }
            }
            if (amountFromOutput.doubleValue() >= totalFee.doubleValue()) {
                break;
            }
        }
        transaction.getConfidence(new Context(CurrentNetParams.getNetParams())).setSource(TransactionConfidence.Source.SELF);
        transaction.setPurpose(Transaction.Purpose.USER_PAYMENT);
        byte[] bytes = transaction.unsafeBitcoinSerialize();
        int txSizeInkB = (int) Math.ceil(bytes.length / 1024.);
        BigDecimal minimumFee = (feePerKb.multiply(new BigDecimal(txSizeInkB)));
        logger.info("最小手续费 {}  transaction size kb {}", minimumFee.toString(), txSizeInkB);
        if (minimumFee.doubleValue() > fee.doubleValue()) {
            throw new Exception("手续费不足");
        }

        TransactionCheck transactionCheck = new TransactionCheck();
        transactionCheck.setTransaction(transaction);
        transactionCheck.setTransactionBytes(Hex.toHexString(bytes));
        return new TransactionHashWithSender(transactionCheck.getTransactionBytes(), transaction.getInputs().get(0).getFromAddress().toString(),transactionCheck);
    }
}
