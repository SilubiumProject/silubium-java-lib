package com.spark.bc.wallet.api.entity.slu.history;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author shenzucai
 * @time 2018.12.05 18:50
 */
@NoArgsConstructor
@Data
public class ScriptPubKeyBean {
    /**
     * hex : 540390d003012844a9059cbb000000000000000000000000235be9efcc20ec3238eebe3a9f54158f640523060000000000000000000000000000000000000000000000000000000000989680141e88227d9f21cd26ee06f0f1473e119bbf392fc0c2
     * asm : 4 250000 40 a9059cbb000000000000000000000000235be9efcc20ec3238eebe3a9f54158f640523060000000000000000000000000000000000000000000000000000000000989680 1e88227d9f21cd26ee06f0f1473e119bbf392fc0 OP_CALL
     * addresses : ["SLSQ5SPQMp8jV4bXQyXDkNswai2MGuJwZUw1"]
     * type : pubkeyhash
     */

    private String hex;
    private String asm;
    private String type;
    private List<String> addresses;
}
