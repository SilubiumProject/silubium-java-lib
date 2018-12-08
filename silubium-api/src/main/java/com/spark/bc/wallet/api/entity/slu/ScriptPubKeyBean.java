package com.spark.bc.wallet.api.entity.slu;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author shenzucai
 * @time 2018.12.05 09:31
 */
@Data
@NoArgsConstructor
public class ScriptPubKeyBean {
    /**
     * hex :
     * asm :
     */

    private String hex;
    private String asm;


    /**
     * addresses : ["SLSQhfPRJckfRqer5b95b4uohNsKeAUXEL45"]
     * type : pubkeyhash
     */

    private String type;
    private List<String> addresses;
}
