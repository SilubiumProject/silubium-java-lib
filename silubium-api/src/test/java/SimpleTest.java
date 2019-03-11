import com.google.gson.reflect.TypeToken;
import com.spark.bc.wallet.api.entity.TransactionCheck;
import com.spark.bc.wallet.api.entity.slu.Balance;
import com.spark.bc.wallet.api.entity.slu.SendRawTransactionRequest;
import com.spark.bc.wallet.api.entity.slu.SendResult;
import com.spark.bc.wallet.api.entity.slu.UTXO;
import com.spark.bc.wallet.api.entity.slu.history.Transaction;
import com.spark.bc.wallet.api.entity.src20.CallResult;
import com.spark.bc.wallet.api.entity.src20.Contract;
import com.spark.bc.wallet.api.entity.src20.SrcBalance;
import com.spark.bc.wallet.api.exception.ApiException;
import com.spark.bc.wallet.api.service.SilubiumService;
import com.spark.bc.wallet.api.util.*;
import org.bitcoinj.core.Address;
import org.bouncycastle.util.encoders.Hex;
import org.jhblockchain.crypto.CoinTypes;
import org.jhblockchain.crypto.ECKeyPair;
import org.jhblockchain.crypto.bip32.ExtendedKey;
import org.jhblockchain.crypto.bip39.MnemonicCode;
import org.jhblockchain.crypto.bip39.MnemonicException;
import org.jhblockchain.crypto.bip44.AddressIndex;
import org.jhblockchain.crypto.bip44.BIP44;
import org.jhblockchain.crypto.bip44.CoinPairDerive;
import org.jhblockchain.crypto.exceptions.ValidationException;
import org.jhblockchain.crypto.params.SLUNetworkParameters;
import org.junit.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Security;
import java.util.*;

public class SimpleTest {


    static {
        // 加载BC加密驱动
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        // 交易确认数
        CurrentNetParams.setDefault_confirm(0);
        // 是否主链，默认true
        CurrentNetParams.setUseMainNet(false);
        // 接口主机地址和端口
        CurrentNetParams.setBaseUrl("https://sluapi2.silubium.org");
    }


    /**
     * 测试获取slu余额
     * @author shenzucai
     * @time 2018.12.05 19:20
     * @param
     * @return true
     */
    @Test
    public void testGetAddrBalance() throws UnsupportedEncodingException {

        SilubiumService rpcService = Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl());
        List<Balance> balances = Generator.executeSync(rpcService.getAddrBalance("SLUZgb6DTuyZDtzhMq28otomHnUfQt68mRcr"));
        //List<Balance> balances1 = Generator.executeSync(rpcService.getAddrBalance("SLSSFpE5Gbbg84v2FqFkZAasrmqfNNNZqvwr,SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm"));
        System.out.println(balances.get(0).getBalance());
    }

    /**
     * 测试获取块的交易
     * @author shenzucai
     * @time 2018.12.05 19:20
     * @param
     * @return true
     */
    @Test
    public void testListTransaction() throws UnsupportedEncodingException {

        SilubiumService rpcService = Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl());
        com.spark.bc.wallet.api.entity.slu.Transaction transaction = Generator.executeSync(rpcService.listTransaction("84553",1,10));
        System.out.println(transaction);
    }

    /**
     * 测试获取交易根据交易hash值
     * @author shenzucai
     * @time 2018.12.05 19:20
     * @param
     * @return true
     */
    @Test
    public void testGetTransaction() throws UnsupportedEncodingException {

        SilubiumService rpcService = Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl());
        Transaction transaction = Generator.executeSync(rpcService.getTransaction("99ef1b55b6a625e60677d71a6a15661c6992a021b3cd6e29e32ba935a6ff0522"));
        System.out.println(transaction);
    }

    /**
     * 测试获取获取token余额
     * @author shenzucai
     * @time 2018.12.05 19:20
     * @param
     * @return true
     */
    @Test
    public void testGetAddrSrcBalance() throws UnsupportedEncodingException {

        SilubiumService rpcService = Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl());
        SrcBalance balances = Generator.executeSync(rpcService.getAddrSrcBalance("1e88227d9f21cd26ee06f0f1473e119bbf392fc0", "SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm"));
        SrcBalance balances1 = Generator.executeSync(rpcService.getAddrSrcBalance("1e88227d9f21cd26ee06f0f1473e119bbf392fc0", "SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm,SLSQWyo7Ceqgirz8W2dHS7aGcXfef74J68Wx"));
        System.out.println(balances);
    }

    /**
     * 测试获取未花费输出
     * @author shenzucai
     * @time 2018.12.05 19:20
     * @param
     * @return true
     */
    @Test
    public void testGetAddrUTXOs() throws UnsupportedEncodingException {

        SilubiumService rpcService = Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl());
        List<UTXO> utxos = Generator.executeSync(rpcService.getAddrUTXOs("SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm",new BigDecimal("400000"),CurrentNetParams.getDefault_confirm()));
        // List<UTXO> utxos1 = Generator.executeSync(rpcService.getAddrUTXOs("SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm,SLSQWyo7Ceqgirz8W2dHS7aGcXfef74J68Wx",null,CurrentNetParams.getDefault_confirm()));
        BigDecimal sum = new BigDecimal("0");
        for(UTXO utxo:utxos){
            sum = sum.add(utxo.getAmount());
        }
        System.out.println(sum.toPlainString());
        System.out.println(utxos);
    }

    /**
     * 测试创建发送交易
     * @author shenzucai
     * @time 2018.12.05 19:20
     * @param
     * @return true
     */
    @Test
    public void testCreateTx() throws UnsupportedEncodingException {
        try {
            SendRawTransactionRequest sendRawTransactionRequest = new SendRawTransactionRequest();
            sendRawTransactionRequest.setAllowAbsurdFees(true);
            Set<String> toAddresses = new HashSet<>();
            toAddresses.add("SLSSFpE5Gbbg84v2FqFkZAasrmqfNNNZqvwr");
            List<BigDecimal> bigDecimals = new ArrayList<>();
            bigDecimals.add(new BigDecimal("9125437.83989612"));
            // Map<String, String> <=> Map<address, privateKey>
            Map<String, String> map = new HashMap();
            map.put("SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm","cRAcYoB4RgbpyEkPEaxqs4C4y63b3MMXfw34boDVggfEHP9o1qct");
            //map.put("SLSRi1eaWgiUBpcFvrnsRyamjPLnGamojXCz","cRjSXKEdjQhD1KBWUrShm5QGYNhE2jfwwBoRiw3iNKnPnGMaUiVc");
            //map.put("SLSS9M2SNQB3FywcpRjCcQszpxKKdwPrddmS","cT7Ahj7uKNZv67JftAju3iUZUNUvE3UXxtF7Xq3QamE5UpvGQqXy");
            //map.put("SLSgJKZhg6isMBBUZnjLLEYreFRfuhchunXs", "cRpYDyRsa9n2rYr3RMZ9fo1Du6JdXtbmszVAg7ytmLzJCAAqbuZf");
            try {
                sendRawTransactionRequest.setRawtx(TransactionUtil.createTx(map, toAddresses, bigDecimals, "0.0001",BigDecimal.ZERO,null).getTransactionBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
            SilubiumService rpcService = Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl());
            SendResult utxos = Generator.executeSync(rpcService.sendRawTransaction(sendRawTransactionRequest));
            System.out.println(utxos);
        }catch (ApiException e){
            System.out.println(e.getError());
        }
    }

    /**
     * 测试创建token交易
     * @author shenzucai
     * @time 2018.12.05 19:20
     * @param
     * @return true
     */
    @Test
    public void testCreateSrcTx() throws UnsupportedEncodingException {
        try {
            SendRawTransactionRequest sendRawTransactionRequest = new SendRawTransactionRequest();
            sendRawTransactionRequest.setAllowAbsurdFees(true);
            String toAddress = "SLSQWxnqf7fZ7ZPYYYE1Q9a7DQvU8NpnMK8o";
            BigDecimal bigDecimal = new BigDecimal("100");
            // Map<String, String> <=> Map<address, privateKey>
            Map<String, String> map = new HashMap(1);
            String contractAddress = "1e88227d9f21cd26ee06f0f1473e119bbf392fc0";
            // 该map只能只能放一个
            map.put("SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm","cRAcYoB4RgbpyEkPEaxqs4C4y63b3MMXfw34boDVggfEHP9o1qct");
            //map.put("SLSRi1eaWgiUBpcFvrnsRyamjPLnGamojXCz","cRjSXKEdjQhD1KBWUrShm5QGYNhE2jfwwBoRiw3iNKnPnGMaUiVc");
            //map.put("SLSS9M2SNQB3FywcpRjCcQszpxKKdwPrddmS","cT7Ahj7uKNZv67JftAju3iUZUNUvE3UXxtF7Xq3QamE5UpvGQqXy");
            //map.put("SLSgJKZhg6isMBBUZnjLLEYreFRfuhchunXs", "cRpYDyRsa9n2rYr3RMZ9fo1Du6JdXtbmszVAg7ytmLzJCAAqbuZf");
            try {
                sendRawTransactionRequest.setRawtx(TransactionUtil.createSrc20Tx(map, contractAddress,toAddress, bigDecimal, "0.0001",new BigDecimal("0"),BigDecimal.ZERO,null).getTransactionBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
            SilubiumService rpcService = Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl());
            SendResult utxos = Generator.executeSync(rpcService.sendRawTransaction(sendRawTransactionRequest));
            System.out.println(utxos);
        }catch (ApiException e){
            System.out.println(e.getError());
        }
    }


    /**
     * 测试token构建token 1->N 交易
     * @author shenzucai
     * @time 2018.12.05 19:20
     * @param
     * @return true
     */
    @Test
    public void testCreateMultiSendSrc20Tx() throws UnsupportedEncodingException {
        try {
            SendRawTransactionRequest sendRawTransactionRequest = new SendRawTransactionRequest();
            sendRawTransactionRequest.setAllowAbsurdFees(true);
            Set<String> toAddresses = new HashSet<>();
            toAddresses.add("SLSatkvYJEtLnAB1WQ5MLgZemm9qCAYZDULV");
            toAddresses.add("SLSevyzoQrj3f3HnqAE7QxSCjE1qNw3Jm7Es");
            toAddresses.add("SLSRf3nXyC4B7VpHvJa6sAk9mMLorLewWJXr");
            toAddresses.add("SLSdiM1GMUnxnL4HpSo7jrpsdMmFEX4XCkzb");
            toAddresses.add("SLSX3rfdH8rXCKjZaEJ4AwcNxcFn9A5z97nU");
            List<BigDecimal> bigDecimals = new ArrayList<>();
            bigDecimals.add(new BigDecimal("1"));
            bigDecimals.add(new BigDecimal("1"));
            bigDecimals.add(new BigDecimal("1"));
            bigDecimals.add(new BigDecimal("1"));
            bigDecimals.add(new BigDecimal("1"));

            // Map<String, String> <=> Map<address, privateKey>
            Map<String, String> map = new HashMap(1);
            String contractAddress = "1e88227d9f21cd26ee06f0f1473e119bbf392fc0";
            String toContractAddress = "9cafe0206525fd14243968be3c75372d3b7579f9";
            // 该map只能只能放一个
            map.put("SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm","cRAcYoB4RgbpyEkPEaxqs4C4y63b3MMXfw34boDVggfEHP9o1qct");
            //map.put("SLSRi1eaWgiUBpcFvrnsRyamjPLnGamojXCz","cRjSXKEdjQhD1KBWUrShm5QGYNhE2jfwwBoRiw3iNKnPnGMaUiVc");
            //map.put("SLSS9M2SNQB3FywcpRjCcQszpxKKdwPrddmS","cT7Ahj7uKNZv67JftAju3iUZUNUvE3UXxtF7Xq3QamE5UpvGQqXy");
            //map.put("SLSgJKZhg6isMBBUZnjLLEYreFRfuhchunXs", "cRpYDyRsa9n2rYr3RMZ9fo1Du6JdXtbmszVAg7ytmLzJCAAqbuZf");
            try {
                sendRawTransactionRequest.setRawtx(TransactionUtil.createMultiSendSrc20Tx(map, toContractAddress,contractAddress, toAddresses,bigDecimals, "0.0001",new BigDecimal("0"),BigDecimal.ZERO,null).getTransactionBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
            SilubiumService rpcService = Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl());
            SendResult utxos = Generator.executeSync(rpcService.sendRawTransaction(sendRawTransactionRequest));
            System.out.println(utxos);
        }catch (ApiException e){
            System.out.println(e.getError());
        }
    }


    /**
     * 测试根据utxo数量批量创建交易
     * @author shenzucai
     * @time 2018.12.05 19:20
     * @param
     * @return true
     */
    @Test
    public void testCreateManyTx() throws UnsupportedEncodingException {
        try {
            SendRawTransactionRequest sendRawTransactionRequest = new SendRawTransactionRequest();
            sendRawTransactionRequest.setAllowAbsurdFees(true);
            Set<String> toAddresses = new HashSet<>();
            toAddresses.add("SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm");
            Map<String, String> map = new HashMap();
            map.put("SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm","cRAcYoB4RgbpyEkPEaxqs4C4y63b3MMXfw34boDVggfEHP9o1qct");
            //map.put("SLSRi1eaWgiUBpcFvrnsRyamjPLnGamojXCz","cRjSXKEdjQhD1KBWUrShm5QGYNhE2jfwwBoRiw3iNKnPnGMaUiVc");
            //map.put("SLSS9M2SNQB3FywcpRjCcQszpxKKdwPrddmS","cT7Ahj7uKNZv67JftAju3iUZUNUvE3UXxtF7Xq3QamE5UpvGQqXy");
            //map.put("SLSgJKZhg6isMBBUZnjLLEYreFRfuhchunXs", "cRpYDyRsa9n2rYr3RMZ9fo1Du6JdXtbmszVAg7ytmLzJCAAqbuZf");
            List<String> hexTransactions = null;
            try {
                hexTransactions = TransactionUtil.createManyTx(map, toAddresses,BigDecimal.ZERO,10,null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(hexTransactions != null && hexTransactions.size()>0) {
                for(String hex:hexTransactions) {
                    sendRawTransactionRequest.setRawtx(hex);
                    SilubiumService rpcService = Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl());
                    SendResult utxos = Generator.executeSync(rpcService.sendRawTransaction(sendRawTransactionRequest));
                    System.out.println(utxos);
                }
            }
        } catch (ApiException e) {
            System.out.println(e.getError());
        }
    }

    /**测试获取合约信息
     * @author shenzucai
     * @time 2018.12.05 19:20
     * @param
     * @return true
     */
    @Test
    public void testContractMethod() throws UnsupportedEncodingException {

        String spenderAddress = "ab4e6392cfff27eb4c7dbab9dd6d66af0d09f953";
        BigDecimal amount = new BigDecimal("10000000000");

        SendRawTransactionRequest sendRawTransactionRequest = new SendRawTransactionRequest();
        sendRawTransactionRequest.setAllowAbsurdFees(true);
        Map<String, String> map = new HashMap(1);
        // 合约地址
        String contractAddress = "bfb6a2b3e75358169efa7889a6c0d800700bc1c5";
        // 该map只能只能放一个  发送地址和私钥
        map.put("BCYXrfGMXnYycNDABfu8jScH6eAY3gknBLrE","L4gyiicVwy16acDLo62Nuz3hKRuW49aytkBawQrQya76XvdN1CCD");

        List<org.web3j.abi.datatypes.Type> inputParameters = new ArrayList<>();
        inputParameters.add(new org.web3j.abi.datatypes.Address(spenderAddress));
        inputParameters.add(new Uint256(amount.multiply(new BigDecimal(
                Math.pow(10,new Double(8).doubleValue())
        )).toBigInteger()));

        Function fn = new Function("approve", inputParameters, Collections.<TypeReference<?>>emptyList());
        String hexData = FunctionEncoder.encode(fn);
        if(hexData.startsWith("0x")){
            hexData = hexData.substring(2);
        }
        try {
            TransactionCheck transactionCheck = TransactionUtil.createSrc20MethodTx(map,contractAddress,hexData,"0.01",BigDecimal.ZERO,BigDecimal.ZERO,null);
            sendRawTransactionRequest.setRawtx(transactionCheck.getTransactionBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SilubiumService rpcService = Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl());
        // 广播交易 返回交易hash
        SendResult sendResult = Generator.executeSync(rpcService.sendRawTransaction(sendRawTransactionRequest));
        System.out.println(sendResult.getTxid());

    }

    /**测试获取合约信息
     * @author shenzucai
     * @time 2018.12.05 19:20
     * @param
     * @return true
     */
    @Test
    public void testGetContract() throws UnsupportedEncodingException {
        SilubiumService rpcService = Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl());
        Contract contract = Generator.executeSync(rpcService.getContract("1e88227d9f21cd26ee06f0f1473e119bbf392fc0"));
        System.out.println(contract);
    }

    /**测试获取合约信息
     * @author shenzucai
     * @time 2018.12.05 19:20
     * @param
     * @return true
     */
    @Test
    public void testContractCall() throws UnsupportedEncodingException {

        String from = "SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm";
        String contractAddress = "1e88227d9f21cd26ee06f0f1473e119bbf392fc0";
        String spenderAddress = "9cafe0206525fd14243968be3c75372d3b7579f9";
        List<org.web3j.abi.datatypes.Type> inputParameters = new ArrayList<>();
        inputParameters.add(new org.web3j.abi.datatypes.Address(AddressUtil.SLUtoHash160(from)));
        inputParameters.add(new org.web3j.abi.datatypes.Address(spenderAddress));
        Function fn = new Function("allowance", inputParameters, Collections.singletonList(new TypeReference<Uint256>() {}));
        String hexData = FunctionEncoder.encode(fn);
        if(hexData.startsWith("0x")){
            hexData = hexData.substring(2);
        }
        SilubiumService rpcService = Generator.createService(SilubiumService.class, CurrentNetParams.getBaseUrl());
        CallResult callContractResult = Generator.executeSync(rpcService.contractCall(contractAddress,hexData,from));
        Contract contract = Generator.executeSync(rpcService.getContract("1e88227d9f21cd26ee06f0f1473e119bbf392fc0"));
        System.out.println(callContractResult);
        if (callContractResult != null) {
            if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())) {
                List list = FunctionReturnDecoder.decode(callContractResult.getExecutionResult().getOutput(), fn.getOutputParameters());
                if (list != null && !list.isEmpty()) {
                    Uint256 uint256 = (Uint256) list.get(0);

                    System.out.println("allowance:"+new BigDecimal(uint256.getValue())
                            .divide(
                                    new BigDecimal(
                                            Math.pow(10,new Double(contract.getDecimals()).doubleValue()
                                            )
                                    )
                            ));
                } else {
                    System.out.println("null");
                }
            } else {
                System.out.println("execute error");
            }
        }else{
            System.out.println("request null");
        }
    }


    /**测试
     * @author shenzucai
     * @time 2018.12.05 19:20
     * @param
     * @return true
     */
    @Test
    public void test() throws UnsupportedEncodingException {
        Address address = Address.fromBase58(CurrentNetParams.getNetParams(), "SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm");
        System.out.println(Hex.toHexString(address.getHash160()));
    }


    @Test
    public void testbip44EthereumEcKey() throws MnemonicException.MnemonicLengthException, ValidationException {

        List<String> mnemonicWordsInAList = new ArrayList<>();
        mnemonicWordsInAList.add("cupboard");
        mnemonicWordsInAList.add("shed");
        mnemonicWordsInAList.add("accident");
        mnemonicWordsInAList.add("simple");
        mnemonicWordsInAList.add("marble");
        mnemonicWordsInAList.add("drive");
        mnemonicWordsInAList.add("put");
        mnemonicWordsInAList.add("crew");
        mnemonicWordsInAList.add("marine");
        mnemonicWordsInAList.add("mistake");
        mnemonicWordsInAList.add("shop");
        mnemonicWordsInAList.add("chimney");
        mnemonicWordsInAList.add("plate");
        mnemonicWordsInAList.add("throw");
        mnemonicWordsInAList.add("cable");
        byte[] seed = MnemonicCode.toSeed(mnemonicWordsInAList, "123");


        ExtendedKey extendedKey = ExtendedKey.create(seed);
        CoinPairDerive coinKeyPair = new CoinPairDerive(extendedKey);
        AddressIndex address = BIP44.m().purpose44().coinType(CoinTypes.SLIUBIUM).account(1).external().address(1);
        ECKeyPair master = coinKeyPair.derive(address, new SLUNetworkParameters(), true);
        System.out.println(address.toString());
        try {
            System.out.println("address" + "..." + "SL"+master.getAddress());
            System.out.println("privateKey" + "..." + master.getPrivateKey());
            System.out.println("publicKey" + "..." + master.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
