package com.spark.bc.wallet.api.entity.slu;

import com.spark.bc.wallet.api.entity.src20.ReceiptBean;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author shenzucai
 * @time 2018.12.05 09:31
 */
@NoArgsConstructor
@Data
public class TxsBean {
    private String txid;
    private int version;
    private long locktime;
    private boolean issrc20Transfer;
    private String blockhash;
    private int blockheight;
    private int confirmations;
    private long time;
    private long blocktime;
    private boolean isCoinBase;
    private BigDecimal valueOut;
    private int size;
    private BigDecimal valueIn;
    private BigDecimal fees;
    private List<VinBean> vin;
    private List<VoutBean> vout;
    private List<ReceiptBean> receipt;
}
