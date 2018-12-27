# silubium-java-lib

超级节点使用说明
## 交易所使用说明

1. 该工具包运行环境最低为 jdk1.8 直接下载release jar包 [silubium-api](https://github.com/daring5920/silubium-java-lib/releases/download/V1.0.2/silubium-api.jar) 项目结构为pom结构

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
    
3. 正常启动项目后,可以参考[README.MD](https://github.com/daring5920/silubium-java-lib/blob/master/silubium-api/README.MD)进行地址生成，slu转账，src20 合约转账等操作

4. 如有疑问可在[issue](https://github.com/daring5920/silubium-java-lib/issues)中进行反馈
