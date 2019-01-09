# silubium-java-lib

超级节点使用说明
## 交易所使用说明

1. 该工具包运行环境最低为 jdk1.8 直接下载release jar包 [silubium-api](https://github.com/daring5920/silubium-java-lib/releases/tag/V1.0.3) 项目结构为pom结构

2. 项目直接加载该jar包,然后在程序初始化的时候，进行相关初始操作

        static {
                // 支持加密BC驱动
                Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
                // 交易确认数
                CurrentNetParams.setDefault_confirm(6);
                // 网络模式
                CurrentNetParams.setUseMainNet(true);
                // 正式链 超级节点地址
                CurrentNetParams.setBaseUrl("http://47.107.38.202:6910");
            }
        
    如果项目启动是报加密相关的错误，请下载jdk8专用的无限制策略文件[jce-policy](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)，然后到$JAVA_HOME\jre\lib\security目录下替换对应的jar包
    
3. 正常启动项目后,可以参考[README.MD](https://github.com/SilubiumProject/silubium-java-lib/blob/master/silubium-api/README.MD)进行地址生成，slu转账，src20 合约转账等操作，一般步骤为：

  （1）为用户离线生成地址，SLU地址和SLU代币（如ET）地址可以相同，[bip44](https://github.com/satoshilabs/slips/blob/master/slip-0044.md)
  
        使用bip44协议进行离线地址生成,具体规则可参考bip44 silubium
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
            // 123为默认密码，可自行替换
            byte[] seed = MnemonicCode.toSeed(mnemonicWordsInAList, "123");


            ExtendedKey extendedKey = ExtendedKey.create(seed);
            CoinPairDerive coinKeyPair = new CoinPairDerive(extendedKey);
            // 使用bip44路径生成，建议BIP44.m().purpose44().coinType(CoinTypes.SLIUBIUM).account(1).external().address(此处为地址索引，为遵循bip44规则，其值范围为0到19最佳);
            // 当address索引累计到19后，可以增加account索引以生成更多地址
            //使用bip44路径生成，建议BIP44.m().purpose44().coinType(CoinTypes.SLIUBIUM).account(增加此处索引).external().address(19);
            
            AddressIndex address = BIP44.m().purpose44().coinType(CoinTypes.SLIUBIUM).account(1).external().address(1);
            // 第一个参数为地址索引路径，然后是链参数，是否为测试链
            ECKeyPair master = coinKeyPair.derive(address, new SLUNetworkParameters(), !CurrentNetParams.getUseMainNet());
            System.out.println(address.toString());
            try {
                // 合法的slu地址正式链前三个字符为SLU，测试链前三个字符为SLS，截掉"SL"后地址规则满足base58编码
                String sluAddress = "SL"+master.getAddress();
                System.out.println("privateKey" + "..." + master.getPrivateKey());
                System.out.println("publicKey" + "..." + master.getPublicKey());
            } catch (Exception e) {
                e.printStackTrace();
            }
          }
        
  （2）用户充币
           
           使用定时任务进行周期性取块高度的信息
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
        // 第一个参数为块高度或块hash 建议初始值为接入成功是正式链的最新高度，第二个参数为获取当前页码，第三个参数为页记录数 建议为300-500
        com.spark.bc.wallet.api.entity.slu.Transaction transaction = Generator.executeSync(rpcService.listTransaction("84553",1,10));
        System.out.println(transaction);
    }
    建议30-60s获取一次
    对于获取到的交易会有 
    public class Transaction {
            // 总页数
            private int pagesTotal;
            // 包含的交易数
            private List<TxsBean> txs;
     }
     
     TxsBean：
     public class TxsBean {
             //交易id
            private String txid;
            // 版本号
            private int version;
            private long locktime;
            // 是否为src20 代币交易 如果该值为false则处理方式完全参照bitcoin
            private boolean issrc20Transfer;
            // 块hash
            private String blockhash;
            // 块高度
            private int blockheight;
            // 确认数
            private int confirmations;
            private long time;
            private long blocktime;
            // 因silubium采用silkscreen共识协议（类pos3.0）
            private boolean isCoinBase;
            private BigDecimal valueOut;
            private int size;
            private BigDecimal valueIn;
            // 手续费
            private BigDecimal fees;
            private List<VinBean> vin;
            private List<VoutBean> vout;
            // 如果为src20代币交易，则该参数对应具体的src转账信息，src20采用evm虚拟机，处理方式通erc20
            private List<ReceiptBean> receipt;
        }
        
        // public static final String TRANSFER_TYPE = "ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef"; src20 transfer标识
        
     示例值：
             {
                "txid": "7a6ad2cbea4afa040da271f5017caf0055e5cd27bb23d12eae9c4188d3b84c25",
                "version": 2,
                "locktime": 97602,
                "receipt": [{
                        "blockHash": "3b3a6aa8783c124678e5ddaec7fdfef307449cae67dc843683b5ec9a77cd965b",
                        "blockNumber": 97604,
                        "transactionHash": "7a6ad2cbea4afa040da271f5017caf0055e5cd27bb23d12eae9c4188d3b84c25",
                        "transactionIndex": 4,
                        "from": "SLURkEbmRxBHFz5P5AUvv9fsEqvX7jVYRHNz",
                        "to": "bcc131c009225b8992c4b3c4442322a9068527a6",
                        "cumulativeGasUsed": 21456,
                        "gasUsed": 21456,
                        "contractAddress": "bcc131c009225b8992c4b3c4442322a9068527a6",
                        "excepted": "None",
                        "log": [{
                                "address": "bcc131c009225b8992c4b3c4442322a9068527a6",
                                "topics": ["ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef", "SLURkEbmRxBHFz5P5AUvv9fsEqvX7jVYRHNz", "SLUP4tWemvXpFtAh7xULmo1vtHSZkamWnM7H"],
                                "data": "0000000000000000000000000000000000000000000000000000000b9554c180"
                        }]
                }],
                "issrc20Transfer": true,
                "vin": [{
                        "txid": "0a391b7de04dc60a14fbdd0436528bc88e132cea1e7b542a064034ad3cedde11",
                        "vout": 0,
                        "sequence": 4294967294,
                        "n": 0,
                        "scriptSig": {
                                "hex": "473044022078c74fd0b7256ff7e4d004b9145414cc080094144c7a563f783cb450150e8a9202201a85b4877d5de14348d907686c5ebdc5f2d0b47f2d588dcbab3c5762f5a6af8d012103d8dd7fc24e9825455c2217760b50c6a29b9687f7ac45eb173ad5d426da10e943",
                                "asm": "3044022078c74fd0b7256ff7e4d004b9145414cc080094144c7a563f783cb450150e8a9202201a85b4877d5de14348d907686c5ebdc5f2d0b47f2d588dcbab3c5762f5a6af8d[ALL] 03d8dd7fc24e9825455c2217760b50c6a29b9687f7ac45eb173ad5d426da10e943"
                        },
                        "addr": "SLURkEbmRxBHFz5P5AUvv9fsEqvX7jVYRHNz",
                        "valueSat": 14990000,
                        "value": 0.1499,
                        "doubleSpentTxID": null
                }],
                "vout": [{
                        "value": "0.09980000",
                        "n": 0,
                        "scriptPubKey": {
                                "hex": "76a91429453c1aba63c572fc08d985f54d5904a8bd4f9f88ac",
                                "asm": "OP_DUP OP_HASH160 29453c1aba63c572fc08d985f54d5904a8bd4f9f OP_EQUALVERIFY OP_CHECKSIG",
                                "addresses": ["SLURkEbmRxBHFz5P5AUvv9fsEqvX7jVYRHNz"],
                                "type": "pubkeyhash"
                        },
                        "spentTxId": null,
                        "spentIndex": null,
                        "spentHeight": null
                }, {
                        "value": "0.00000000",
                        "n": 1,
                        "scriptPubKey": {
                                "hex": "01040320a107010a44a9059cbb0000000000000000000000000be3fdef4eed15b7e39ca1e8bf2c273bd346ec510000000000000000000000000000000000000000000000000000000b9554c18014bcc131c009225b8992c4b3c4442322a9068527a6c2",
                                "asm": "4 500000 10 a9059cbb0000000000000000000000000be3fdef4eed15b7e39ca1e8bf2c273bd346ec510000000000000000000000000000000000000000000000000000000b9554c180 bcc131c009225b8992c4b3c4442322a9068527a6 OP_CALL",
                                "addresses": ["SLUfC4RpuJ7EnJmTT8D6MmJYvnkp9xd7gopM"],
                                "type": "pubkeyhash"
                        },
                        "spentTxId": null,
                        "spentIndex": null,
                        "spentHeight": null
                }],
                "blockhash": "3b3a6aa8783c124678e5ddaec7fdfef307449cae67dc843683b5ec9a77cd965b",
                "blockheight": 97604,
                "confirmations": 6,
                "time": 1545877616,
                "blocktime": 1545877616,
                "valueOut": 0.0998,
                "size": 299,
                "valueIn": 0.1499,
                "fees": 0.0501
        }
        
 src20示意：
 
 ![](https://github.com/daring5920/silubium-java-lib/blob/master/silubium-api/src20transfer.png)
           
  （3）用户提币
        
        发送slu
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
            toAddresses.add("SLUjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm");
            List<BigDecimal> bigDecimals = new ArrayList<>();
            bigDecimals.add(new BigDecimal("1"));
            // Map<String, String> <=> Map<address, privateKey>
            Map<String, String> map = new HashMap();
            // map可以放多个输入
            map.put("地址","私钥");
            try {
                TransactionCheck transactionCheck = TransactionUtil.createTx(map, toAddresses, bigDecimals, "0.0001",BigDecimal.ZERO,null);
                sendRawTransactionRequest.setRawtx(transactionCheck.getTransactionBytes());
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
    // 发送代币 ，可以解除合约进行批量发送代币详情见[silubium-api](https://github.com/daring5920/silubium-java-lib/blob/master/silubium-api/README.MD)
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
            String toAddress = "SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm";
            BigDecimal bigDecimal = new BigDecimal("1");
            // Map<String, String> <=> Map<address, privateKey>
            Map<String, String> map = new HashMap(1);
            String contractAddress = "1e88227d9f21cd26ee06f0f1473e119bbf392fc0";
            // 该map只能只能放一个
            map.put("地址","私钥");
            try {
                TransactionCheck transactionCheck = TransactionUtil.createSrc20Tx(map, contractAddress,toAddress, bigDecimal, "0.001",new BigDecimal("0"),BigDecimal.ZERO,null);
                sendRawTransactionRequest.setRawtx(transactionCheck.getTransactionBytes());
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

4. 如有疑问可在[issue](https://github.com/SilubiumProject/silubium-java-lib/issues)中进行反馈
