package com.spark.bc.wallet.api.entity.slu;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author shenzucai
 * @time 2018.12.05 09:31
 */
@NoArgsConstructor
@Data
public class Transaction {

    private int pagesTotal;
    private List<TxsBean> txs;
}
