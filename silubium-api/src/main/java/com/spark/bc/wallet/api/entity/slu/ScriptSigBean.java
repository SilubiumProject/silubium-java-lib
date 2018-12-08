package com.spark.bc.wallet.api.entity.slu;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2018.12.06 14:20
 */
@NoArgsConstructor
@Data
public class ScriptSigBean {
    /**
     * hex : 483045022100e9d1ab39ee96e605fe3c8846c6fb53b05ace64ea7e3d13ecc176a23a4c89828102207837ac1e185ca70acbac317d851643cca908c475c020a8913f4dc04ddae54f6c01
     * asm : 3045022100e9d1ab39ee96e605fe3c8846c6fb53b05ace64ea7e3d13ecc176a23a4c89828102207837ac1e185ca70acbac317d851643cca908c475c020a8913f4dc04ddae54f6c[ALL]
     */

    private String hex;
    private String asm;
}
