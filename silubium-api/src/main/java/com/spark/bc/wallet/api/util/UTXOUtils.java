package com.spark.bc.wallet.api.util;

import com.spark.bc.wallet.api.entity.slu.UTXO;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author shenzucai
 * @time 2018.12.05 12:50
 */
public class UTXOUtils {


    /**
     * 删选有效的UTXO
     *
     * @param utxos
     * @param amount 不低于此金额
     * @return true
     * @author shenzucai
     * @time 2018.12.05 12:57
     */
    public static void getValidUTXODESCAmount(List<UTXO> utxos, BigDecimal amount,List<UTXO> usedUtxos) {

        for (Iterator<UTXO> iterator = utxos.iterator(); iterator.hasNext(); ) {
            UTXO unspentOutput = iterator.next();
            if (!unspentOutput.isOutputAvailableToPay()/* || unspentOutput.getConfirmations()==0*/) {
                iterator.remove();
            } else {
                if (unspentOutput.getConfirmations() < CurrentNetParams.getDefault_confirm()) {
                    iterator.remove();
                } else {
                    if (unspentOutput.getAmount().compareTo(amount) == -1) {
                        iterator.remove();
                    }
                }
            }
        }
        // 用于支持并发处理
        if(usedUtxos != null && usedUtxos.size()>0){
            utxos.removeAll(usedUtxos);
        }
        Collections.sort(utxos, new Comparator<UTXO>() {
            @Override
            public int compare(UTXO unspentOutput, UTXO t1) {
                return unspentOutput.getAmount().doubleValue() < t1.getAmount().doubleValue() ? 1 : unspentOutput.getAmount().doubleValue() > t1.getAmount().doubleValue() ? -1 : 0;
            }
        });


    }

    /**
     * 删选有效的UTXO
     *
     * @param utxos
     * @param amount 不低于此金额
     * @return true
     * @author shenzucai
     * @time 2018.12.05 12:57
     */
    public static void getValidUTXO(List<UTXO> utxos, BigDecimal amount,List<UTXO> usedUtxos) {

        for (Iterator<UTXO> iterator = utxos.iterator(); iterator.hasNext(); ) {
            UTXO unspentOutput = iterator.next();
            if (!unspentOutput.isOutputAvailableToPay()/* || unspentOutput.getConfirmations()==0*/) {
                iterator.remove();
            } else {
                if (unspentOutput.getConfirmations() < CurrentNetParams.getDefault_confirm()) {
                    iterator.remove();
                } else {
                    if (unspentOutput.getAmount().compareTo(amount) == -1) {
                        iterator.remove();
                    }
                }
            }
        }
        // 用于支持并发处理
        if(usedUtxos != null && usedUtxos.size()>0){
            utxos.removeAll(usedUtxos);
        }
        Collections.sort(utxos, new Comparator<UTXO>() {
            @Override
            public int compare(UTXO unspentOutput, UTXO t1) {
                return unspentOutput.getAmount().doubleValue() < t1.getAmount().doubleValue() ? -1 : unspentOutput.getAmount().doubleValue() > t1.getAmount().doubleValue() ? 1 : 0;
            }
        });

    }
}
