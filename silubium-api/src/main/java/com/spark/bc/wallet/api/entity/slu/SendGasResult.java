package com.spark.bc.wallet.api.entity.slu;

import lombok.Data;
import org.bitcoinj.core.Address;

import java.math.BigDecimal;

/**
 * @author shenzucai
 * @time 2018.12.06 17:46
 */
@Data
public class SendGasResult {

    private Address address;

    private BigDecimal amount;

    public SendGasResult(Address address, BigDecimal amount) {
        this.address = address;
        this.amount = amount;
    }
}
