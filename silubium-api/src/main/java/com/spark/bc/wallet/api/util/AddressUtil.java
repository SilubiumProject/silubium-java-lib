package com.spark.bc.wallet.api.util;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.jhblockchain.crypto.utils.SHA256;

import java.util.Arrays;
import java.util.List;

/**
 * @author shenzucai
 * @time 2018.07.13 15:20
 */
public class AddressUtil {

    /**
     * 将address还原成hash160
     * @author shenzucai
     * @time 2018.07.13 15:22
     * @param address
     * @return true
     */
    public static String SLUtoHash160(String address){

        byte[] bytes = Base58.decode(address);
        byte[] hash160 = new byte[20];
        // 3，截取hash160的字节
        System.arraycopy(bytes,1,hash160,0,20);
        // 4,转换成16进制输出
        String str = Hex.encodeHexString(hash160);
        return str;
    }


    /**
     * 将hash160补充到64个字符
     * @author shenzucai
     * @time 2018.07.13 15:30
     * @param hash160
     * @return true
     */
    public static String to32bytes64char(String hash160){
        if(hash160.length() == 40){
            return "000000000000000000000000"+hash160;
        }else{
            return null;
        }
    }


    public static String Hash160toSlu(String hash160) throws DecoderException {
        // // 第四步，取上一步结果，计算 RIPEMD-160 哈希值
        // // 010966776006953D5567439E5E39F86A0D273BEE
        String address = "";
        // 第五步，取上一步结果，前面加入地址版本号（比特币主网版本号“0x00”）
        // 00010966776006953D5567439E5E39F86A0D273BEE
        // q 120 Q 58 o 115 U68
        address = Integer.toHexString(CurrentNetParams.getNetParams().getAddressHeader())+hash160;
        //address = "009AF1DD0C939624E1984CB56B44B9C5F28E6B21FF";
        String temp = address;

        //
        // 第六步，取上一步结果，计算 SHA-256 哈希值
        // 445C7A8007A93D8733188288BB320A8FE2DEBD2AE1B47F0F50BC10BAE845C094
        //
        // 第七步，取上一步结果，再计算一下 SHA-256 哈希值（哈哈）
        // D61967F63C7DD183914A4AE452C9F6AD5D462CE3D277798075B107615C1A8A30

        address=Hex.encodeHexString(SHA256.doubleSha256(Hex.decodeHex(address.toCharArray())));
        // 第八步，取上一步结果的前4个字节（8位十六进制）
        // D61967F6
        // 第九步，把这4个字节加在第五步的结果后面，作为校验（这就是比特币地址的16进制形态）。
        // 00010966776006953D5567439E5E39F86A0D273BEED61967F6
        address = temp+address.substring(0,8);
        // 第十步，用base58表示法变换一下地址（这就是最常见的比特币地址形态）。
        // 16UwLL9Risc3QfPqBUvKofHmBQ7wMtjvM

        address = Base58.encode(Hex.decodeHex(address.toCharArray()));

        return address;
    }

    /**
     * 多个地址
     * @author shenzucai
     * @time 2018.12.06 14:54
     * @param stringList
     * @return true
     */
    public static Boolean validAddress(List<String> stringList) throws Exception{
        for(String address:stringList){
            Address.fromBase58(CurrentNetParams.getNetParams(), address);
        }
        return true;
    }

    /**
     * 一个地址，或多个由逗号隔开的地址
     * @author shenzucai
     * @time 2018.12.06 14:54
     * @param string
     * @return true
     */
    public static Boolean validAddress(String string) throws Exception{

        List<String> strings = Arrays.asList(string.split(","));
        for(String address:strings){
            Address.fromBase58(CurrentNetParams.getNetParams(), address);
        }
        return true;
    }
}
