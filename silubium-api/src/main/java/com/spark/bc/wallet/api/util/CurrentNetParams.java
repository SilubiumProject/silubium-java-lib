package com.spark.bc.wallet.api.util;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.SilubiumMainNetParams;
import org.bitcoinj.params.SilubiumTestNetParams;

public class CurrentNetParams {

    public  CurrentNetParams(){}

    private static Boolean USE_MAIN_NET = false;

    private static String baseUrl = "http://172.16.0.90:3001";

    private static Integer default_confirm = 6;

    public static NetworkParameters getNetParams(){
        return USE_MAIN_NET? SilubiumMainNetParams.get() : SilubiumTestNetParams.get();
    }

    public static Boolean getUseMainNet() {
        return USE_MAIN_NET;
    }

    public static void setUseMainNet(Boolean useMainNet) {
        USE_MAIN_NET = useMainNet;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String baseUrl) {
        CurrentNetParams.baseUrl = baseUrl;
    }

    public static Integer getDefault_confirm() {
        return default_confirm;
    }

    public static void setDefault_confirm(Integer default_confirm) {
        CurrentNetParams.default_confirm = default_confirm;
    }
}
