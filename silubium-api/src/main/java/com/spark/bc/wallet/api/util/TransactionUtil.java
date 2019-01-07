package com.spark.bc.wallet.api.util;

import com.spark.bc.wallet.api.entity.TransactionCheck;
import com.spark.bc.wallet.api.entity.slu.*;
import com.spark.bc.wallet.api.entity.src20.CallResult;
import com.spark.bc.wallet.api.entity.src20.Contract;
import com.spark.bc.wallet.api.entity.src20.SrcBalance;
import com.spark.bc.wallet.api.service.SilubiumService;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.*;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author shenzucai
 * @time 2018.12.05 10:44
 */
public class TransactionUtil {


    private static Logger logger = LoggerFactory.getLogger(TransactionUtil.class);

    /**
     * 正常构建交易
     *
     * @param froms      Map<address,privateKey>
     * @param addresses
     * @param amountList
     * @param feeString
     * @return true
     * @author shenzucai
     * @time 2018.12.05 10:56
     */
    public static TransactionCheck createTx(final Map<String, String> froms, final Set<String> addresses, final List<BigDecimal> amountList, final String feeString, BigDecimal minUtxoAmount,List<com.spark.bc.wallet.api.entity.slu.UTXO> usedUtxos) throws Exception {
        try {
            if (addresses == null || amountList == null || addresses.size() != amountList.size()) {
                throw new Exception("输出地址个数和金额个数不匹配");
            }

            List<Address> toAddressList = new ArrayList<>();
            List<Address> fromAddressList = new ArrayList<>();
            List<ECKey> ecKeyList = new ArrayList<>();

            Transaction transaction = new Transaction(CurrentNetParams.getNetParams());
            BigDecimal bitcoin = new BigDecimal(100000000);

            StringBuffer fromAddressStr = new StringBuffer();
            try {
                for (String address : addresses) {
                    toAddressList.add(Address.fromBase58(CurrentNetParams.getNetParams(), address));
                }
                for (Map.Entry<String,String> address : froms.entrySet()) {
                    fromAddressList.add(Address.fromBase58(CurrentNetParams.getNetParams(), address.getKey()));
                    ecKeyList.add(ECKeyUtils.fromPrivateKey(address.getValue()));
                    if (StringUtils.isEmpty(fromAddressStr.toString())) {
                        fromAddressStr.append(address.getKey());
                    } else {
                        fromAddressStr.append("," + address.getKey());
                    }
                }
            } catch (AddressFormatException a) {
                throw new Exception("地址不合法");
            }

            if (fromAddressList.size() != ecKeyList.size() || fromAddressList.size() == 0 || ecKeyList.size() == 0) {
                throw new Exception("地址和私钥不匹配");
            }

            if (toAddressList.size() != amountList.size() || toAddressList.size() == 0 || amountList.size() == 0) {
                throw new Exception("输出地址和金额个数不匹配");
            }

            BigDecimal amount = new BigDecimal("0.0");
            for (BigDecimal amountStr : amountList) {
                amount = amount.add(amountStr);
            }
            BigDecimal fee = new BigDecimal(feeString);
            // 最大交易手续费为0.5
            fee = fee.compareTo(new BigDecimal("0.5"))==1?new BigDecimal("0.5"):fee;
            BigDecimal amountFromOutput = new BigDecimal("0.0");
            BigDecimal overFlow = new BigDecimal("0.0");
            for (int i = 0; i < toAddressList.size(); i++) {
                transaction.addOutput(Coin.valueOf((long) (amountList.get(i).multiply(bitcoin).doubleValue())), toAddressList.get(i));
            }
            amount = amount.add(fee);

            List<com.spark.bc.wallet.api.entity.slu.UTXO> unspentOutputs = Generator.executeSync(Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl()).getAddrUTXOs(fromAddressStr.toString()));
            if(unspentOutputs == null || unspentOutputs.size() == 0){
                throw new Exception("slu可用余额不足");
            }
            UTXOUtils.getValidUTXODESCAmount(unspentOutputs, minUtxoAmount,usedUtxos);
            List<com.spark.bc.wallet.api.entity.slu.UTXO> usefulUxtos = new ArrayList<>();
            for (com.spark.bc.wallet.api.entity.slu.UTXO unspentOutput : unspentOutputs) {
                overFlow = overFlow.add(unspentOutput.getAmount());
                usefulUxtos.add(unspentOutput);
                if (overFlow.doubleValue() >= amount.doubleValue()) {
                    break;
                }
            }
            if (overFlow.doubleValue() < amount.doubleValue()) {
                throw new Exception("余额不足");
            }

            BigDecimal delivery = overFlow.subtract(amount);
            if (delivery.doubleValue() != 0.0) {
                // 找零会默认找给第一个地址
                transaction.addOutput(Coin.valueOf((long) (delivery.multiply(bitcoin).doubleValue())), ecKeyList.get(0).toAddress(CurrentNetParams.getNetParams()));
            }
            for (com.spark.bc.wallet.api.entity.slu.UTXO unspentOutput : usefulUxtos) {
                for (ECKey deterministicKey : ecKeyList) {
                    if (deterministicKey.toAddress(CurrentNetParams.getNetParams()).toString().equals(unspentOutput.getAddress())) {
                        Sha256Hash sha256Hash = Sha256Hash.wrap(Utils.parseAsHexOrBase58(unspentOutput.getTxid()));
                        TransactionOutPoint outPoint = new TransactionOutPoint(CurrentNetParams.getNetParams(), unspentOutput.getVout(), sha256Hash);
                        Script script = new Script(Utils.parseAsHexOrBase58(unspentOutput.getScriptPubKey()));
                        transaction.addSignedInput(outPoint, script, deterministicKey, Transaction.SigHash.ALL, true);
                        amountFromOutput = amountFromOutput.add(unspentOutput.getAmount());
                        break;
                    }
                }
                if (amountFromOutput.doubleValue() >= amount.doubleValue()) {
                    break;
                }
            }
            transaction.getConfidence(new Context(CurrentNetParams.getNetParams())).setSource(TransactionConfidence.Source.SELF);
            transaction.setPurpose(Transaction.Purpose.USER_PAYMENT);
            byte[] bytes = transaction.unsafeBitcoinSerialize();
            int txSizeInkB = (int) Math.ceil(bytes.length / 1024.);
             BigDecimal minimumFee = (FeeUtil.getEstimateFeePerKb(amount.doubleValue()).multiply(new BigDecimal(txSizeInkB)));
            logger.info("推荐最小手续费为 {}",minimumFee);
             // 最大交易手续费为0.5
            // minimumFee = minimumFee.compareTo(new BigDecimal("0.5"))==1?new BigDecimal("0.5"):minimumFee;
            /*if (minimumFee.doubleValue() > fee.doubleValue()) {
                throw new Exception("手续费不足");
            }*/

            TransactionCheck transactionCheck = new TransactionCheck();
            transactionCheck.setTransaction(transaction);
            transactionCheck.setTransactionBytes(Hex.toHexString(bytes));
            return transactionCheck;
        } catch (Exception e) {
            throw e;
        }

    }


    /**
     * 正常构建token交易
     *
     * @param from      Map<address,privateKey>
     * @param address
     * @param address
     * @param feeString
     * @return true
     * @author shenzucai
     * @time 2018.12.05 10:56
     */
    public static TransactionCheck createSrc20Tx(final Map<String, String> from, String contractAddress, String address, BigDecimal amount, final String feeString, BigDecimal sluAmount, BigDecimal minUtxoAmount, String decimals,List<com.spark.bc.wallet.api.entity.slu.UTXO> usedUtxos) throws Exception {
        try {
            if (address == null || amount == null || from == null || feeString == null || from.size() < 1) {
                throw new Exception("参数不能为空");
            }

            if (from.size() > 1) {
                throw new Exception("from 只能存在一个地址和其对应的私钥");
            }

            String fromaddress = null;
            List<ECKey> ecKeyList = new ArrayList<>();
            StringBuffer fromAddressStr = new StringBuffer();
            try {
                if (address.startsWith("SL")) {
                    Address.fromBase58(CurrentNetParams.getNetParams(), address);
                } else {
                    if (address.length() == 40) {
                        address = AddressUtil.hash160toSlu(address);
                        Address.fromBase58(CurrentNetParams.getNetParams(), address);
                    }
                }
                for (Map.Entry<String,String> fromAddress : from.entrySet()) {
                    fromaddress = fromAddress.getKey();
                    Address.fromBase58(CurrentNetParams.getNetParams(), fromAddress.getKey());
                    ecKeyList.add(ECKeyUtils.fromPrivateKey(fromAddress.getValue()));
                    if (StringUtils.isEmpty(fromAddressStr.toString())) {
                        fromAddressStr.append(fromAddress.getKey());
                    } else {
                        fromAddressStr.append("," + fromAddress.getKey());
                    }
                }
            } catch (AddressFormatException a) {
                throw new Exception("地址不合法");
            }

            SrcBalance balances = Generator.executeSync(Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl()).getAddrSrcBalance(contractAddress, fromaddress));


            if (amount.compareTo(balances.getBalances().get(0).getBalance()) == 1) {
                throw new Exception("TOKEN余额不足");
            }
            List<com.spark.bc.wallet.api.entity.slu.UTXO> unspentOutputs = Generator.executeSync(Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl()).getAddrUTXOs(fromAddressStr.toString()));
            if(unspentOutputs == null || unspentOutputs.size() == 0){
                throw new Exception("slu可用余额不足");
            }
            UTXOUtils.getValidUTXODESCAmount(unspentOutputs, minUtxoAmount,usedUtxos);

            List<org.web3j.abi.datatypes.Type> inputParameters = new ArrayList<>();

            inputParameters.add(new org.web3j.abi.datatypes.Address(AddressUtil.SLUtoHash160(fromaddress)));

            inputParameters.add(new Uint256(amount.multiply(
                    new BigDecimal(
                            Math.pow(10, new Double(decimals).doubleValue()
                            )
                    )
            ).toBigInteger()));
            Function fn = new Function("transfer", inputParameters, Collections.singletonList(new TypeReference<Uint256>() {
            }));
            String hexData = FunctionEncoder.encode(fn);
            if (hexData.startsWith("0x")) {
                hexData = hexData.substring(2);
            }

            return ContractUtils.createTransactionHash(hexData, contractAddress, unspentOutputs, ecKeyList, 500000, 10, feeString, sluAmount);
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * 正常构建token交易
     *
     * @param from      Map<address,privateKey>
     * @param address
     * @param address
     * @param feeString
     * @return true
     * @author shenzucai
     * @time 2018.12.05 10:56
     */
    public static TransactionCheck createSrc20Tx(final Map<String, String> from, String contractAddress, String address, BigDecimal amount, final String feeString, BigDecimal sluAmount, BigDecimal minUtxoAmount,List<com.spark.bc.wallet.api.entity.slu.UTXO> usedUtxos) throws Exception {
        try {
            if (address == null || amount == null || from == null || feeString == null || from.size() < 1) {
                throw new Exception("参数不能为空");
            }

            if (from.size() > 1) {
                throw new Exception("from 只能存在一个地址和其对应的私钥");
            }

            String fromaddress = null;
            List<ECKey> ecKeyList = new ArrayList<>();
            StringBuffer fromAddressStr = new StringBuffer();
            try {
                if (address.startsWith("SL")) {
                    Address.fromBase58(CurrentNetParams.getNetParams(), address);
                } else {
                    if (address.length() == 40) {
                        address = AddressUtil.hash160toSlu(address);
                        Address.fromBase58(CurrentNetParams.getNetParams(), address);
                    }
                }
                for (Map.Entry<String,String> fromAddress : from.entrySet()) {
                    fromaddress = fromAddress.getKey();
                    Address.fromBase58(CurrentNetParams.getNetParams(), fromAddress.getKey());
                    ecKeyList.add(ECKeyUtils.fromPrivateKey(fromAddress.getValue()));
                    if (StringUtils.isEmpty(fromAddressStr.toString())) {
                        fromAddressStr.append(fromAddress.getKey());
                    } else {
                        fromAddressStr.append("," + fromAddress.getKey());
                    }
                }
            } catch (AddressFormatException a) {
                throw new Exception("地址不合法");
            }

            SrcBalance balances = Generator.executeSync(Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl()).getAddrSrcBalance(contractAddress, fromaddress));

            Contract contract = Generator.executeSync(Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl()).getContract(contractAddress));
            if (amount.compareTo(balances.getBalances().get(0).getBalance()) == 1) {
                throw new Exception("TOKEN余额不足");
            }
            List<com.spark.bc.wallet.api.entity.slu.UTXO> unspentOutputs = Generator.executeSync(Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl()).getAddrUTXOs(fromAddressStr.toString()));
            if(unspentOutputs == null || unspentOutputs.size() == 0){
                throw new Exception("slu可用余额不足");
            }
            UTXOUtils.getValidUTXO(unspentOutputs, minUtxoAmount,usedUtxos);

            List<org.web3j.abi.datatypes.Type> inputParameters = new ArrayList<>();

            inputParameters.add(new org.web3j.abi.datatypes.Address(AddressUtil.SLUtoHash160(address)));

            inputParameters.add(new Uint256(amount.multiply(
                    new BigDecimal(
                            Math.pow(10, new Double(contract.getDecimals()).doubleValue()
                            )
                    )
            ).toBigInteger()));
            Function fn = new Function("transfer", inputParameters, Collections.singletonList(new TypeReference<Uint256>() {
            }));
            String hexData = FunctionEncoder.encode(fn);
            if (hexData.startsWith("0x")) {
                hexData = hexData.substring(2);
            }

            return ContractUtils.createTransactionHash(hexData, contractAddress, unspentOutputs, ecKeyList, 500000, 10, feeString, sluAmount);
        } catch (Exception e) {
            throw e;
        }

    }


    /**
     * 正常构建token function交易
     *
     * @param from
     * @param contractAddress
     * @param data
     * @param feeString
     * @param sluAmount
     * @param minUtxoAmount
     * @return true
     * @author shenzucai
     * @time 2018.12.06 16:31
     */
    public static TransactionCheck createSrc20MethodTx(final Map<String, String> from, String contractAddress, String data, String feeString, BigDecimal sluAmount, BigDecimal minUtxoAmount,List<com.spark.bc.wallet.api.entity.slu.UTXO> usedUtxos) throws Exception {
        try {
            if (from == null || feeString == null || from.size() < 1) {
                throw new Exception("参数不能为空");
            }

            if (from.size() > 1) {
                throw new Exception("from 只能存在一个地址和其对应的私钥");
            }

            List<ECKey> ecKeyList = new ArrayList<>();
            StringBuffer fromAddressStr = new StringBuffer();
            try {
                for (Map.Entry<String,String> fromAddress : from.entrySet()) {
                    Address.fromBase58(CurrentNetParams.getNetParams(), fromAddress.getKey());
                    ecKeyList.add(ECKeyUtils.fromPrivateKey(fromAddress.getValue()));
                    if (StringUtils.isEmpty(fromAddressStr.toString())) {
                        fromAddressStr.append(fromAddress.getKey());
                    } else {
                        fromAddressStr.append("," + fromAddress.getKey());
                    }
                }
            } catch (AddressFormatException a) {
                throw new Exception("地址不合法");
            }

            List<com.spark.bc.wallet.api.entity.slu.UTXO> unspentOutputs = Generator.executeSync(Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl()).getAddrUTXOs(fromAddressStr.toString()));
            if(unspentOutputs == null || unspentOutputs.size() == 0){
                throw new Exception("slu可用余额不足");
            }
            UTXOUtils.getValidUTXO(unspentOutputs, minUtxoAmount,usedUtxos);
            return ContractUtils.createTransactionHash(data, contractAddress, unspentOutputs, ecKeyList, 500000, 10, feeString, sluAmount);
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * 正常构建token 1->N 交易 需要借助multisend合约，测试链合约地址为9cafe0206525fd14243968be3c75372d3b7579f9，正式链合约地址为324556bc96839bf2a1c26df208b0d8afd15b26ab
     *
     * @param from
     * @param toContractAddress
     * @param contractAddress
     * @param addresses
     * @param amountList
     * @param feeString
     * @param sluAmount
     * @param minUtxoAmount
     * @return true
     * @author shenzucai
     * @time 2018.12.06 11:02
     */
    public static TransactionCheck createMultiSendSrc20Tx(final Map<String, String> from, String toContractAddress, String contractAddress, final Set<String> addresses, final List<BigDecimal> amountList, final String feeString, BigDecimal sluAmount, BigDecimal minUtxoAmount,List<com.spark.bc.wallet.api.entity.slu.UTXO> usedUtxos) throws Exception {
        try {

            if (addresses == null || amountList == null || addresses.size() != amountList.size()) {
                throw new Exception("输出地址个数和金额个数不匹配");
            }

            List<Address> toAddressList = new ArrayList<>();

            if (from == null || feeString == null || from.size() < 1) {
                throw new Exception("参数不能为空");
            }

            if (from.size() > 1) {
                throw new Exception("from 只能存在一个地址和其对应的私钥");
            }

            String fromaddress = null;
            List<ECKey> ecKeyList = new ArrayList<>();
            StringBuffer fromAddressStr = new StringBuffer();
            try {
                for (String address : addresses) {
                    if (address.startsWith("SL")) {
                        toAddressList.add(Address.fromBase58(CurrentNetParams.getNetParams(), address));
                    } else {
                        if (address.length() == 40) {
                            address = AddressUtil.hash160toSlu(address);
                            toAddressList.add(Address.fromBase58(CurrentNetParams.getNetParams(), address));
                        }
                    }


                }
                for (Map.Entry<String,String> fromAddress : from.entrySet()) {
                    fromaddress = fromAddress.getKey();
                    Address.fromBase58(CurrentNetParams.getNetParams(), fromAddress.getKey());
                    ecKeyList.add(ECKeyUtils.fromPrivateKey(fromAddress.getValue()));
                    if (StringUtils.isEmpty(fromAddressStr.toString())) {
                        fromAddressStr.append(fromAddress.getKey());
                    } else {
                        fromAddressStr.append("," + fromAddress.getKey());
                    }
                }
            } catch (AddressFormatException a) {
                throw new Exception("地址不合法");
            }


            BigDecimal amount = new BigDecimal("0.0");
            for (BigDecimal amountStr : amountList) {
                amount = amount.add(amountStr);
            }
            Contract contract = Generator.executeSync(Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl()).getContract(contractAddress));

            BigDecimal allowance = getAllowance(fromaddress, contractAddress, toContractAddress, contract.getDecimals());


            if (amount.compareTo(allowance) == 1) {
                throw new Exception("TOKEN 授信额度不足");
            }
            List<com.spark.bc.wallet.api.entity.slu.UTXO> unspentOutputs = Generator.executeSync(Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl()).getAddrUTXOs(fromAddressStr.toString()));
            if(unspentOutputs == null || unspentOutputs.size() == 0){
                throw new Exception("slu可用余额不足");
            }
            UTXOUtils.getValidUTXODESCAmount(unspentOutputs, minUtxoAmount,usedUtxos);

            List<org.web3j.abi.datatypes.Type> inputParameters = new ArrayList<>();
            inputParameters.add(new org.web3j.abi.datatypes.Address(contractAddress));
            List<org.web3j.abi.datatypes.Address> listAddress = new ArrayList<>();
            List<Uint256> amountse = new ArrayList<>();


            for (int i = 0; i < toAddressList.size(); i++) {
                //listAddress.add(new MyAddress(toAddressArr[i]));
                listAddress.add(new org.web3j.abi.datatypes.Address(org.bouncycastle.util.encoders.Hex.toHexString(toAddressList.get(i).getHash160())));
            }
            for (int i = 0; i < amountList.size(); i++) {
                //listAddress.add(new MyAddress(toAddressArr[i]));
                amountse.add(new Uint256(amountList.get(i).multiply(new BigDecimal(
                        Math.pow(10, new Double(contract.getDecimals()).doubleValue())
                )).toBigInteger()));
            }

            inputParameters.add(new DynamicArray(listAddress));

            inputParameters.add(new DynamicArray(amountse));

            Function fn = new Function("multiTransferFrom", inputParameters, Collections.<TypeReference<?>>emptyList());
            String hexData = FunctionEncoder.encode(fn);
            if (hexData.startsWith("0x")) {
                hexData = hexData.substring(2);
            }

            return ContractUtils.createTransactionHash(hexData, toContractAddress, unspentOutputs, ecKeyList, 6000000, 10, feeString, sluAmount);
        } catch (Exception e) {
            throw e;
        }

    }


    /**
     * 正常构建token 1->N 交易 需要借助multisend合约，测试链合约地址为9cafe0206525fd14243968be3c75372d3b7579f9，正式链合约地址为324556bc96839bf2a1c26df208b0d8afd15b26ab
     *
     * @param from
     * @param toContractAddress
     * @param contractAddress
     * @param addresses
     * @param amountList
     * @param feeString
     * @param sluAmount
     * @param minUtxoAmount
     * @return true
     * @author shenzucai
     * @time 2018.12.06 11:02
     */
    public static TransactionCheck createMultiSendSrc20Tx(final Map<String, String> from, String toContractAddress, String contractAddress, final Set<String> addresses, final List<BigDecimal> amountList, final String feeString, BigDecimal sluAmount, BigDecimal minUtxoAmount, String decimals,List<com.spark.bc.wallet.api.entity.slu.UTXO> usedUtxos) throws Exception {
        try {

            if (addresses == null || amountList == null || addresses.size() != amountList.size()) {
                throw new Exception("输出地址个数和金额个数不匹配");
            }

            List<Address> toAddressList = new ArrayList<>();

            if (from == null || feeString == null || from.size() < 1) {
                throw new Exception("参数不能为空");
            }

            if (from.size() > 1) {
                throw new Exception("from 只能存在一个地址和其对应的私钥");
            }

            String fromaddress = null;
            List<ECKey> ecKeyList = new ArrayList<>();
            StringBuffer fromAddressStr = new StringBuffer();
            try {
                for (String address : addresses) {
                    if (address.startsWith("SL")) {
                        toAddressList.add(Address.fromBase58(CurrentNetParams.getNetParams(), address));
                    } else {
                        if (address.length() == 40) {
                            address = AddressUtil.hash160toSlu(address);
                            toAddressList.add(Address.fromBase58(CurrentNetParams.getNetParams(), address));
                        }
                    }


                }
                for (Map.Entry<String,String> fromAddress : from.entrySet()) {
                    fromaddress = fromAddress.getKey();
                    Address.fromBase58(CurrentNetParams.getNetParams(), fromAddress.getKey());
                    ecKeyList.add(ECKeyUtils.fromPrivateKey(fromAddress.getValue()));
                    if (StringUtils.isEmpty(fromAddressStr.toString())) {
                        fromAddressStr.append(fromAddress.getKey());
                    } else {
                        fromAddressStr.append("," + fromAddress.getKey());
                    }
                }
            } catch (AddressFormatException a) {
                throw new Exception("地址不合法");
            }


            BigDecimal amount = new BigDecimal("0.0");
            for (BigDecimal amountStr : amountList) {
                amount = amount.add(amountStr);
            }

            BigDecimal allowance = getAllowance(fromaddress, contractAddress, toContractAddress, decimals);


            if (amount.compareTo(allowance) == 1) {
                throw new Exception("TOKEN 授信额度不足");
            }
            List<com.spark.bc.wallet.api.entity.slu.UTXO> unspentOutputs = Generator.executeSync(Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl()).getAddrUTXOs(fromAddressStr.toString()));
            if(unspentOutputs == null || unspentOutputs.size() == 0){
                throw new Exception("slu可用余额不足");
            }
            UTXOUtils.getValidUTXO(unspentOutputs, minUtxoAmount,usedUtxos);

            List<org.web3j.abi.datatypes.Type> inputParameters = new ArrayList<>();
            inputParameters.add(new org.web3j.abi.datatypes.Address(contractAddress));
            List<org.web3j.abi.datatypes.Address> listAddress = new ArrayList<>();
            List<Uint256> amountse = new ArrayList<>();


            for (int i = 0; i < toAddressList.size(); i++) {
                //listAddress.add(new MyAddress(toAddressArr[i]));
                listAddress.add(new org.web3j.abi.datatypes.Address(org.bouncycastle.util.encoders.Hex.toHexString(toAddressList.get(i).getHash160())));
            }
            for (int i = 0; i < amountList.size(); i++) {
                //listAddress.add(new MyAddress(toAddressArr[i]));
                amountse.add(new Uint256(amountList.get(i).multiply(new BigDecimal(
                        Math.pow(10, new Double(decimals).doubleValue())
                )).toBigInteger()));
            }

            inputParameters.add(new DynamicArray(listAddress));

            inputParameters.add(new DynamicArray(amountse));

            Function fn = new Function("multiTransferFrom", inputParameters, Collections.<TypeReference<?>>emptyList());
            String hexData = FunctionEncoder.encode(fn);
            if (hexData.startsWith("0x")) {
                hexData = hexData.substring(2);
            }

            return ContractUtils.createTransactionHash(hexData, toContractAddress, unspentOutputs, ecKeyList, 6000000, 10, feeString, sluAmount);
        } catch (Exception e) {
            throw e;
        }

    }


    /**
     * 减少无用的UTXO构建交易
     *
     * @param froms     Map<address,privateKey>
     * @param addresses
     * @return true
     * @author shenzucai
     * @time 2018.12.05 10:56
     */
    public static List<String> createManyTx(final Map<String, String> froms, final Set<String> addresses, BigDecimal minUtxoAmount, Integer size,List<com.spark.bc.wallet.api.entity.slu.UTXO> usedUtxos) throws Exception {
        try {

            List<Address> toAddressList = new ArrayList<>();
            List<Address> fromAddressList = new ArrayList<>();
            List<ECKey> ecKeyList = new ArrayList<>();

            StringBuffer fromAddressStr = new StringBuffer();
            try {
                for (String address : addresses) {
                    toAddressList.add(Address.fromBase58(CurrentNetParams.getNetParams(), address));
                }
                for (Map.Entry<String,String> address : froms.entrySet()) {
                    fromAddressList.add(Address.fromBase58(CurrentNetParams.getNetParams(), address.getKey()));
                    ecKeyList.add(ECKeyUtils.fromPrivateKey(address.getValue()));
                    if (StringUtils.isEmpty(fromAddressStr.toString())) {
                        fromAddressStr.append(address.getKey());
                    } else {
                        fromAddressStr.append("," + address.getKey());
                    }
                }
            } catch (AddressFormatException a) {
                throw new Exception("地址不合法");
            }

            if (fromAddressList.size() != ecKeyList.size() || fromAddressList.size() == 0 || ecKeyList.size() == 0) {
                throw new Exception("地址和私钥不匹配");
            }


            BigDecimal overFlow = new BigDecimal("0.0");


            List<com.spark.bc.wallet.api.entity.slu.UTXO> unspentOutputs = Generator.executeSync(Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl()).getAddrUTXOs(fromAddressStr.toString()));
            if(unspentOutputs == null || unspentOutputs.size() == 0){
                throw new Exception("slu可用余额不足");
            }
            UTXOUtils.getValidUTXO(unspentOutputs, minUtxoAmount,usedUtxos);

            List<com.spark.bc.wallet.api.entity.slu.UTXO> usefulUxtos = new ArrayList<>();
            List<String> hexTxs = new ArrayList<>();
            int index = 1;
            for (int i = 0; i < unspentOutputs.size(); i++) {
                if (i % size == 0 && i > 0) {
                    usefulUxtos.add(unspentOutputs.get(unspentOutputs.size() - index));
                    overFlow = overFlow.add(unspentOutputs.get(unspentOutputs.size() - index).getAmount());
                    String hexTx = buildTransaction(toAddressList, ecKeyList, usefulUxtos, overFlow);
                    index++;
                    hexTxs.add(hexTx);
                    overFlow = new BigDecimal("0.0");
                    usefulUxtos = new ArrayList<>();
                }
                overFlow = overFlow.add(unspentOutputs.get(i).getAmount());
                usefulUxtos.add(unspentOutputs.get(i));
                if (i == unspentOutputs.size() - index) {
                    String hexTx = buildTransaction(toAddressList, ecKeyList, usefulUxtos, overFlow);
                    hexTxs.add(hexTx);
                }
            }
            return hexTxs;
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * 没有找零的交易
     *
     * @param toAddressList
     * @param ecKeyList
     * @param usefulUxtos
     * @param amount
     * @return true
     * @author shenzucai
     * @time 2018.12.05 16:03
     */
    private static String buildTransaction(List<Address> toAddressList, List<ECKey> ecKeyList, List<com.spark.bc.wallet.api.entity.slu.UTXO> usefulUxtos, BigDecimal amount) throws Exception {
        BigDecimal minFee = new BigDecimal("0.0001").multiply(new BigDecimal(Double.toString(Math.ceil(usefulUxtos.size() / 5.0))));
        // 最大交易手续费为0.5
        minFee = minFee.compareTo(new BigDecimal("0.5"))==1?new BigDecimal("0.5"):minFee;
        amount = amount.subtract(minFee);
        BigDecimal bitcoin = new BigDecimal(100000000);
        Transaction transaction = new Transaction(CurrentNetParams.getNetParams());

        for (int i = 0; i < toAddressList.size(); i++) {
            transaction.addOutput(Coin.valueOf((long) (amount.multiply(bitcoin).doubleValue())), toAddressList.get(i));
        }

        for (com.spark.bc.wallet.api.entity.slu.UTXO unspentOutput : usefulUxtos) {
            for (ECKey deterministicKey : ecKeyList) {
                if (deterministicKey.toAddress(CurrentNetParams.getNetParams()).toString().equals(unspentOutput.getAddress())) {
                    Sha256Hash sha256Hash = Sha256Hash.wrap(Utils.parseAsHexOrBase58(unspentOutput.getTxid()));
                    TransactionOutPoint outPoint = new TransactionOutPoint(CurrentNetParams.getNetParams(), unspentOutput.getVout(), sha256Hash);
                    Script script = new Script(Utils.parseAsHexOrBase58(unspentOutput.getScriptPubKey()));
                    transaction.addSignedInput(outPoint, script, deterministicKey, Transaction.SigHash.ALL, true);

                }
            }
        }

        transaction.getConfidence(new Context(CurrentNetParams.getNetParams())).setSource(TransactionConfidence.Source.SELF);
        transaction.setPurpose(Transaction.Purpose.USER_PAYMENT);
        byte[] bytes = transaction.unsafeBitcoinSerialize();
        int txSizeInkB = (int) Math.ceil(bytes.length / 1024.);
        BigDecimal minimumFee = (FeeUtil.getEstimateFeePerKb(amount.doubleValue()).multiply(new BigDecimal(txSizeInkB)));
        logger.info("最小手续费 {} vinsize {} vousize {} transaction size kb {}", minimumFee.toString(), usefulUxtos.size(), toAddressList.size(), txSizeInkB);
        // if (minimumFee.doubleValue() > 0.1441) {
        //     throw new Exception("手续费不足");
        // }
        return Hex.toHexString(bytes);
    }

    /**
     * 获取授信额度
     *
     * @param from
     * @param contractAddress
     * @param spenderAddress
     * @return true
     * @author shenzucai
     * @time 2018.12.06 11:07
     */
    private static BigDecimal getAllowance(String from, String contractAddress, String spenderAddress, String decimals) {
        List<org.web3j.abi.datatypes.Type> inputParameters = new ArrayList<>();
        inputParameters.add(new org.web3j.abi.datatypes.Address(AddressUtil.SLUtoHash160(from)));
        inputParameters.add(new org.web3j.abi.datatypes.Address(spenderAddress));
        Function fn = new Function("allowance", inputParameters, Collections.singletonList(new TypeReference<Uint256>() {
        }));
        String hexData = FunctionEncoder.encode(fn);
        if (hexData.startsWith("0x")) {
            hexData = hexData.substring(2);
        }
        SilubiumService rpcService = Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl());
        CallResult callContractResult = Generator.executeSync(rpcService.contractCall(contractAddress, hexData, from));
        System.out.println(callContractResult);
        if (callContractResult != null) {
            if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())) {
                List list = FunctionReturnDecoder.decode(callContractResult.getExecutionResult().getOutput(), fn.getOutputParameters());
                if (list != null && !list.isEmpty()) {
                    Uint256 uint256 = (Uint256) list.get(0);

                    return new BigDecimal(uint256.getValue())
                            .divide(
                                    new BigDecimal(
                                            Math.pow(10, new Double(decimals).doubleValue()
                                            )
                                    )
                            );
                } else {
                    return BigDecimal.ZERO;
                }
            } else {
                return BigDecimal.ZERO;
            }
        } else {
            return BigDecimal.ZERO;
        }
    }


    /**
     * 解析并忽略不大于amount的金额
     *
     * @param txsBean
     * @param amount
     * @return true
     * @author shenzucai
     * @time 2018.12.06 18:29
     */
    public static List<SluTransferResult> analysis(TxsBean txsBean, BigDecimal amount) {

        List<SluTransferResult> sluTransferResults = new ArrayList<>();
        Map<String, BigDecimal> vin = new HashMap<>();
        Map<String, BigDecimal> vout = new HashMap<>();
        for (VinBean vinBean : txsBean.getVin()) {
            if (StringUtils.isNotEmpty(vinBean.getAddr())) {
                if (vin.containsKey(vinBean.getAddr())) {
                    vin.put(vinBean.getAddr(), vin.get(vinBean.getAddr()).add(vinBean.getValue()));
                } else {
                    vin.put(vinBean.getAddr(), vinBean.getValue());
                }
            } else {
                continue;
            }
        }
        for (VoutBean voutBean : txsBean.getVout()) {
            if (voutBean.getScriptPubKey().getAddresses() == null || voutBean.getScriptPubKey().getAddresses().size() == 0) {
                continue;
            } else {
                if(StringUtils.isEmpty(voutBean.getScriptPubKey().getAddresses().get(0))){
                    continue;
                }
                if (vout.containsKey(voutBean.getScriptPubKey().getAddresses().get(0))) {
                    vout.put(voutBean.getScriptPubKey().getAddresses().get(0), vout.get(voutBean.getScriptPubKey().getAddresses().get(0)).add(voutBean.getValue()));
                } else {
                    vout.put(voutBean.getScriptPubKey().getAddresses().get(0), voutBean.getValue());
                }
            }
        }


        for (Map.Entry<String,BigDecimal> inAddess : vin.entrySet()) {

            if (!vout.containsKey(inAddess.getKey())) {
                vout.put(inAddess.getKey(), BigDecimal.ZERO.subtract(inAddess.getValue()));
            }else{
                for (Map.Entry<String,BigDecimal> outAddess : vout.entrySet()) {
                    if (StringUtils.equalsIgnoreCase(inAddess.getKey(),outAddess.getKey())) {
                        vout.put(outAddess.getKey(), outAddess.getValue().subtract(inAddess.getValue()));
                    }
                }
            }

        }

        for (Map.Entry<String,BigDecimal> addess : vout.entrySet()) {
            if(amount != null) {
                if (addess.getValue().compareTo(amount) == 1) {
                    SluTransferResult sluTransferResult = new SluTransferResult();
                    sluTransferResult.setAddress(addess.getKey());
                    sluTransferResult.setAmount(addess.getValue());
                    sluTransferResults.add(sluTransferResult);
                }
            }else{
                SluTransferResult sluTransferResult = new SluTransferResult();
                sluTransferResult.setAddress(addess.getKey());
                sluTransferResult.setAmount(addess.getValue());
                sluTransferResults.add(sluTransferResult);
            }
        }
        return sluTransferResults;
    }
}
