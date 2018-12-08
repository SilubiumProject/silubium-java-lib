package com.spark.bc.wallet.api.entity.src20;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2018.12.05 19:17
 */
@NoArgsConstructor
@Data
public class Contract {

    /**
     * contract_address : 1e88227d9f21cd26ee06f0f1473e119bbf392fc0
     * total_supply : 1000000000000000000
     * decimals : 8
     * name : Chinese money Yuan Token
     * symbol : CNYT
     * version :
     * transfers_count : 2115
     * holders_count : 1010
     * tx_hash : aba00c76611276ca2f6ec9606b687f772ca38b41a8bcb45d12fd6944b7722392
     * updated_at : 2018-11-26T10:06:58.478Z
     * block_height : 79195
     * contract_address_base : SLSQ5SPQMp8jV4bXQyXDkNswai2MGuJwZUw1
     * exception : false
     * created_at : 2018-11-26T10:06:57.971Z
     * tx_date_time : 2018-11-26T10:07:12.000Z
     * tx_time : 1543226832
     */

    private String contract_address;
    private String total_supply;
    private String decimals;
    private String name;
    private String symbol;
    private String version;
    private int transfers_count;
    private int holders_count;
    private String tx_hash;
    private String updated_at;
    private int block_height;
    private String contract_address_base;
    private boolean exception;
    private String created_at;
    private String tx_date_time;
    private int tx_time;
}
