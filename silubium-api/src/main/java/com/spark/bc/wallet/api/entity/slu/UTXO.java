package com.spark.bc.wallet.api.entity.slu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author shenzucai
 * @time 2018.12.04 20:26
 */
@NoArgsConstructor
@Data
public class UTXO {

    /**
     * address : SLSZt8DT8u2JTyA8y37kKLWW79UfTFj7NXFS
     * txid : 981715df1e35efbcbfd032c8ebab7c2734b899567ee1e458b934855ba6c0bd87
     * vout : 1
     * scriptPubKey : 76a9148a15a5ea811f057f945dff6e70773b81afb3fb2388ac
     * amount : 3.1749
     * satoshis : 317490000
     * isStake : false
     * height : 75428
     * confirmations : 8595
     */

    private String address;
    private String txid;
    private int vout;
    private String scriptPubKey;
    private BigDecimal amount;
    private long satoshis;
    private boolean isStake;
    private int height;
    private int confirmations;

    @JsonIgnore
    public boolean isOutputAvailableToPay() {
        if (isStake) {
            return confirmations > 100;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UTXO utxo = (UTXO) o;
        if(Objects.equals(utxo.vout,this.vout) && StringUtils.equalsIgnoreCase(utxo.txid,this.txid)){
            return true;
        }else{
            return false;
        }

    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (txid != null ? txid.hashCode() : 0);
        result = 31 * result + vout;
        return result;
    }
}
