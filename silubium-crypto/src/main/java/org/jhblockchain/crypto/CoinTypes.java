package org.jhblockchain.crypto;

import org.jhblockchain.crypto.exceptions.NotSupportException;

public enum CoinTypes {
	Bitcoin(0, "BTC"), 
	BitcoinTest(1, "BTC"), 
	Litecoin(2, "LTC"), 
	Dogecoin(3, "DOGE"),
	Reddcoin(4, "RDD"),
	DASH(5, "DASH"),

	Ethereum(60, "ETH"),
	EtherClassic(61,"ETC"),

	Ripple(144,"XRP"),
	BitcoinCash(145,"BCH"),

	EOS(194, "EOS"),
	NEO(888,"NEO"),

	QTUM(2301,"QTUM"),

	IOTA(4218,"IOTA"),

	SLIUBIUM(5920,"SLIUBIUM");

	private int coinType;
	private String coinName;

	CoinTypes(int coinType, String coinName) {
		this.coinType = coinType;
		this.coinName = coinName;
	}

	public int coinType() {
		return coinType;
	}

	public String coinName() {
		return coinName;
	}

	public static CoinTypes parseCoinType(int type) throws NotSupportException {
		for(CoinTypes e : CoinTypes.values()) {
			if(e.coinType == type) {
				return e;
			}
		}
		throw new NotSupportException("The currency is not supported for the time being");
	}
}
