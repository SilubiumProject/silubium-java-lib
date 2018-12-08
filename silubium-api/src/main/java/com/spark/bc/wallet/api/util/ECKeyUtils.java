package com.spark.bc.wallet.api.util;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.wallet.Wallet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shenzucai
 * @time 2018.12.05 11:00
 */
public class ECKeyUtils {

    public static ECKey fromPrivateKey(String privateKey) throws Exception{

        if (privateKey != null && privateKey.trim().length() == 52) {
            try {
                byte[] decode = Base58.decode(privateKey);
                boolean isCompressed = false;
                if (decode.length == 38) {
                    isCompressed = true;
                }
                //判断私钥格式
                byte[] check = new byte[4];
                System.arraycopy(decode, decode.length - 4, check, 0, check.length);
                byte[] privateCheck = Sha256Hash.hashTwice(decode, 0, decode.length - 4);
                for (int i = 0; i < 4; i++) {
                    if (check[i] != privateCheck[i]) {

                    }
                }
                byte[] bytes = new byte[32];
                System.arraycopy(decode, 1, bytes, 0, bytes.length);
                ECKey ecKey = org.bitcoinj.core.ECKey.fromPrivate(bytes, isCompressed);

                return ecKey;
            }catch (Exception e){
                throw e;
            }
        }else{
            throw new Exception("无效的私钥");
        }
    }
}
