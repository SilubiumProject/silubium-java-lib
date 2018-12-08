package com.spark.bc.wallet.api.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author shenzucai
 * @time 2018.07.13 16:12
 */
public class AmountFormate {

    /**
     * 将16进制金额转成10进制
     * @author shenzucai
     * @time 2018.07.14 7:50
     * @param hexAmount
     * @param decimal
     * @return true
     */
    public static BigDecimal amount(String hexAmount,String decimal){
                    BigInteger bigInteger = new BigInteger(hexAmount, 16);
                    return new BigDecimal(bigInteger)
                            .divide(
                                    new BigDecimal(
                                            Math.pow(10,new Double(decimal).doubleValue()
                                            )
                                    )
                            );
    }


    /**
     * 将10进制金额转成16进制
     * @author shenzucai
     * @time 2018.07.14 7:50
     * @param amount
     * @param decimal
     * @return true
     */
    public static String toHexAmount(BigDecimal amount,String decimal){

        String strAmount = amount.multiply(
                new BigDecimal(
                        Math.pow(10,new Double(decimal).doubleValue())
                )
        ).toBigInteger().toString();
        String hexAmount = new BigInteger(strAmount,10).toString(16);

        return org.apache.commons.lang3.StringUtils.leftPad(hexAmount,64,"0");
    }
}
