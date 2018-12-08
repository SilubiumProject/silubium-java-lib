package org.jhblockchain.crypto.neo;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.crypto.SignatureScheme;
import com.ripple.encodings.common.B16;
import org.bouncycastle.util.encoders.Hex;
import org.jhblockchain.crypto.ECKeyPair;
import org.jhblockchain.crypto.Key;
import org.jhblockchain.crypto.exceptions.ValidationException;
import org.jhblockchain.crypto.ripple.XrpECKeyPair;

import java.math.BigInteger;

/**
 * @author shenzucai
 * @time 2018.11.22 17:50
 */
public class NEOECKeyPair extends ECKeyPair{



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


    public static NEOECKeyPair parse(Key keyPair) {
        return new NEOECKeyPair(keyPair);
    }



    protected NEOECKeyPair() {
        super();
    }

    public NEOECKeyPair(byte[] p, boolean compressed) throws ValidationException {
        super(p, compressed);
    }

    public NEOECKeyPair(BigInteger priv, boolean compressed) {
        super(priv, compressed);
    }

    protected NEOECKeyPair(Key keyPair) {
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
        try {
            Account account = new Account(getRawPrivateKey(), SignatureScheme.SHA256WITHECDSA);
            return account.getAddressU160().toBase58();
        } catch (Exception e) {
           return null;
        }
    }

    @Override
    public <T> T sign(byte[] messageHash) throws ValidationException {
        return super.sign(messageHash);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
