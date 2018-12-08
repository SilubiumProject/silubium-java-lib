package org.jhblockchain.crypto;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jhblockchain.crypto.bip32.ExtendedKey;
import org.jhblockchain.crypto.bip39.MnemonicCode;
import org.jhblockchain.crypto.bip39.MnemonicException.MnemonicLengthException;
import org.jhblockchain.crypto.bip44.AddressIndex;
import org.jhblockchain.crypto.bip44.BIP44;
import org.jhblockchain.crypto.bip44.CoinPairDerive;
import org.jhblockchain.crypto.exceptions.ValidationException;
import org.jhblockchain.crypto.params.BCHNetworkParameters;
import org.jhblockchain.crypto.params.DASHNetworkParameters;
import org.jhblockchain.crypto.params.SLUNetworkParameters;
import org.junit.Test;

public class Bip44Test {
    CoinPairDerive coinKeyPair;

    public Bip44Test() {
        Security.addProvider(new BouncyCastleProvider());
        ExtendedKey extendedKey = new Bip32Test().testRandomExtendedKey();
        coinKeyPair = new CoinPairDerive(extendedKey);
    }

    @Test
    public void testbip44EthereumExtendedKey() throws ValidationException {
        Log.log("testbip44EthereumExtendedKey--------->");

        AddressIndex address0 = BIP44.m().purpose44().coinType(CoinTypes.Ethereum).account(0).external().address(0);
        Log.log("address0:" + address0.toString());
        ExtendedKey key0 = coinKeyPair.deriveByExtendedKey(address0);
        AddressIndex address1 = BIP44.m().purpose44().coinType(CoinTypes.Ethereum).account(0).external().address(1);
        Log.log("address1:" + address1.toString());
        ExtendedKey key1 = coinKeyPair.deriveByExtendedKey(address1);
        Log.log(String.valueOf(key0.getParent()));
        Log.log(String.valueOf(key1.getParent()));

    }

    @Test
    public void testbip44EthereumEcKey() throws MnemonicLengthException, ValidationException {

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
            System.out.println("address" + "..." + master.getAddress());
            System.out.println("privateKey" + "..." + master.getPrivateKey());
            System.out.println("publicKey" + "..." + master.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
