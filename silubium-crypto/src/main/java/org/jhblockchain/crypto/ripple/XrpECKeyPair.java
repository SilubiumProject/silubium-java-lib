package org.jhblockchain.crypto.ripple;

import com.ripple.config.Config;
import com.ripple.crypto.ecdsa.Seed;
import com.ripple.encodings.common.B16;
import com.ripple.utils.HashUtils;
import org.bouncycastle.util.encoders.Hex;
import org.jhblockchain.crypto.ECKeyPair;
import org.jhblockchain.crypto.Key;
import org.jhblockchain.crypto.exceptions.ValidationException;

import java.math.BigInteger;

import static com.ripple.config.Config.getB58IdentiferCodecs;

/**
 * @author shenzucai
 * @time 2018.11.09 14:32
 */
public class XrpECKeyPair extends ECKeyPair{


    /*
    *   master1.getRawPrivateKey();//获取十六进制私钥
        master1.getRawPublicKey();//获取十六进制压缩公钥
        master1.getRawPublicKey(false);//获取十六进制公钥 传入参数压缩或者不压缩

        //以下方法直接调用会报错
        master1.getPrivateKey();//获取格式化的私钥
        master1.getPublicKey();//获取格式化的公钥
        master1.getAddress();//获取格式化的地址
        master1.getRawAddress();//获取十六进制格式的地址

    * */


    public static XrpECKeyPair parse(Key keyPair) {
        return new XrpECKeyPair(keyPair);
    }


    protected XrpECKeyPair() {
        super();
    }

    public XrpECKeyPair(byte[] p, boolean compressed) throws ValidationException {
        super(p, compressed);
    }

    public XrpECKeyPair(BigInteger priv, boolean compressed) {
        super(priv, compressed);
    }

    protected XrpECKeyPair(Key keyPair) {
        super(keyPair);
    }

    @Override
    public boolean isCompressed() {
        return super.isCompressed();
    }

    @Override
    public ECKeyPair clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void setPublic(byte[] pub) throws ValidationException {
        super.setPublic(pub);
    }

    @Override
    public byte[] getRawPrivateKey() {
        return super.getRawPrivateKey();
    }

    @Override
    public byte[] getRawPublicKey(boolean isCompressed) {
        return super.getRawPublicKey(isCompressed);
    }

    @Override
    public byte[] getRawPublicKey() {
        return super.getRawPublicKey();
    }

    @Override
    public byte[] getRawAddress() {
        return super.getRawAddress();
    }

    @Override
    public String getPrivateKey() {
        return B16.encode(getRawPrivateKey());
    }

    @Override
    public String getPublicKey() {
        return Hex.toHexString(getRawPublicKey());
    }

    @Override
    public String getAddress() {
        return getB58IdentiferCodecs().encodeAddress(HashUtils.SHA256_RIPEMD160(getRawPublicKey()));
    }

    @Override
    public <T> T sign(byte[] messageHash) throws ValidationException {
        return super.sign(messageHash);
    }
}
