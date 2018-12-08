package com.spark.bc.wallet.api.entity.slu.history;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2018.12.05 18:50
 */
@NoArgsConstructor
@Data
public class ScriptSigBean {
    /**
     * hex : 473044022011039a801331307ea967f2a65b04e3fdb1b9bbef34596cbf239fecd5c799d57402205fa77a51ffa14460b2bd0c09d3a9bfaecdd0442206519e5313c65366e8d01369012103f56d823e53e69be9137b0d20702d682cc948b10b33e7c6f07a68922b26a8488a
     * asm : 3044022011039a801331307ea967f2a65b04e3fdb1b9bbef34596cbf239fecd5c799d57402205fa77a51ffa14460b2bd0c09d3a9bfaecdd0442206519e5313c65366e8d01369[ALL] 03f56d823e53e69be9137b0d20702d682cc948b10b33e7c6f07a68922b26a8488a
     */

    private String hex;
    private String asm;
}
