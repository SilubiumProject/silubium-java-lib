package org.jhblockchain.crypto;

import java.util.List;

import org.jhblockchain.crypto.bip39.MnemonicCode;
import org.jhblockchain.crypto.bip39.MnemonicException.MnemonicChecksumException;
import org.jhblockchain.crypto.bip39.MnemonicException.MnemonicLengthException;
import org.jhblockchain.crypto.bip39.MnemonicException.MnemonicWordException;
import org.jhblockchain.crypto.bip39.RandomSeed;
import org.jhblockchain.crypto.bip39.Words;
import org.jhblockchain.crypto.bip39.wordlists.ChineseSimplified;
import org.jhblockchain.crypto.utils.HexUtils;
import org.junit.Test;

public class Bip39Test {
	@Test
	public void testEnglish() throws MnemonicLengthException, MnemonicWordException, MnemonicChecksumException {
		MnemonicCode mnemonicCode = new MnemonicCode();
		//参数是助记词的个数
		byte[] random = RandomSeed.random(Words.TWELVE); 
		Log.log("random: " + HexUtils.toHex(random));
		List<String> mnemonicWordsInAList = mnemonicCode.toMnemonic(random);
		Log.log(mnemonicWordsInAList.toString());
		//恢复随机数 
		byte[] bytes = mnemonicCode.toEntropy(mnemonicWordsInAList);
		Log.log("restored random: " + HexUtils.toHex(bytes));
		
	}
	
	@Test
	public void testChinese() throws MnemonicLengthException, MnemonicWordException, MnemonicChecksumException {
		MnemonicCode mnemonicCode = new MnemonicCode(ChineseSimplified.INSTANCE);
		byte[] random = RandomSeed.random(Words.TWELVE); 
		Log.log("random: " + HexUtils.toHex(random));
		List<String> mnemonicWordsInAList = mnemonicCode.toMnemonic(random);
		Log.log(mnemonicWordsInAList.toString());
		byte[] bytes = mnemonicCode.toEntropy(mnemonicWordsInAList);
		Log.log("restored random: " + HexUtils.toHex(bytes));
		
	}
}
