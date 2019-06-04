package com.spark.bc.wallet.api.util;

import com.spark.bc.wallet.api.contract.ContractBuilder;
import com.spark.bc.wallet.api.contract.TransactionHashWithSender;
import com.spark.bc.wallet.api.entity.TransactionCheck;
import com.spark.bc.wallet.api.entity.slu.SendGasResult;
import com.spark.bc.wallet.api.entity.slu.UTXO;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.script.Script;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shenzucai
 * @time 2018.12.05 19:30
 */
public class ContractUtils {

    /**
     * sendToContract
     * @author shenzucai
     * @time 2018.12.06 8:50
     * @param abiParams
     * @param contractAddress
     * @param unspentOutputs
     * @param ecKeys
     * @param gasLimit
     * @param gasPrice
     * @param fee
     * @return true
     */
    public static TransactionCheck createTransactionHash(String abiParams, String contractAddress, List<UTXO> unspentOutputs, List<ECKey> ecKeys, int gasLimit, int gasPrice, String fee, BigDecimal amount) throws Exception {
        ContractBuilder contractBuilder = new ContractBuilder();
        Script script = contractBuilder.createMethodScript(abiParams, gasLimit, gasPrice, contractAddress);
        return contractBuilder.createTransactionHash(script, unspentOutputs,ecKeys, gasLimit, gasPrice,FeeUtil.getEstimateFeePerKb(amount.doubleValue()), fee,amount.toPlainString());
    }


    /**
     * sendToContract
     * @author shenzucai
     * @time 2018.12.06 8:50
     * @param abiParams
     * @param contractAddress
     * @param unspentOutputs
     * @param ecKeys
     * @param gasLimit
     * @param gasPrice
     * @param fee
     * @return true
     */
    public static TransactionCheck createTransactionHash(List<String> abiParams, String contractAddress, List<UTXO> unspentOutputs, List<ECKey> ecKeys, int gasLimit, int gasPrice, String fee, BigDecimal sluAmount, List<SendGasResult> senGasList, List<Address> addressList) throws Exception {
        ContractBuilder contractBuilder = new ContractBuilder();
        List<Script> scripts = new ArrayList<>();
        if(StringUtils.isNotEmpty(contractAddress)) {
            for (String abiParam : abiParams) {
                scripts.add(contractBuilder.createMethodScript(abiParam, gasLimit, gasPrice, contractAddress));
            }
        }
        return contractBuilder.createNewTransactionHash(scripts, unspentOutputs,ecKeys, gasLimit, gasPrice,FeeUtil.getEstimateFeePerKb(sluAmount.doubleValue()), fee,sluAmount,senGasList,addressList);
    }

    /**
     * createContract
     * @author shenzucai
     * @time 2018.12.06 8:51
     * @param abiParams
     * @param unspentOutputs
     * @param ecKeys
     * @param gasLimit
     * @param gasPrice
     * @param fee
     * @return true
     */
    public static TransactionHashWithSender createCreateContractTransactionHash(String abiParams, List<UTXO> unspentOutputs, List<ECKey> ecKeys, int gasLimit, int gasPrice, String fee) throws Exception {
        ContractBuilder contractBuilder = new ContractBuilder();
        Script script = contractBuilder.createConstructScript(abiParams, gasLimit, gasPrice);
        return contractBuilder.createTransactionHashForCreateContract(script, unspentOutputs,ecKeys, gasLimit, gasPrice, FeeUtil.getEstimateFeePerKb(0), fee);
    }

}
