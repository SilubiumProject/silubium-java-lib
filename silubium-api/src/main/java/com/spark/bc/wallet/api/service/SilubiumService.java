package com.spark.bc.wallet.api.service;

import com.spark.bc.wallet.api.entity.slu.Balance;
import com.spark.bc.wallet.api.entity.slu.SendRawTransactionRequest;
import com.spark.bc.wallet.api.entity.slu.SendResult;
import com.spark.bc.wallet.api.entity.slu.UTXO;
import com.spark.bc.wallet.api.entity.slu.history.Transaction;
import com.spark.bc.wallet.api.entity.src20.CallResult;
import com.spark.bc.wallet.api.entity.src20.Contract;
import com.spark.bc.wallet.api.entity.src20.SrcBalance;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * @author shenzucai
 * @time 2018.12.04 10:33
 */
public interface SilubiumService {

    @GET("/silubium-api/addrs/balance/{addrs}")
    Call<List<Balance>> getAddrBalance(@Path("addrs") String addrs);

    @GET("/silubium-api/src/{contractAddress}/balance/{addrs}")
    Call<SrcBalance> getAddrSrcBalance(@Path("contractAddress") String contractAddress,@Path("addrs") String addrs);

    @GET("/silubium-api/addrs/{addrs}/utxo")
    Call<List<UTXO>> getAddrUTXOs(@Path("addrs") String addrs);

    @GET("/silubium-api/txs")
    Call<com.spark.bc.wallet.api.entity.slu.Transaction> listTransaction(@Query("block") String block,@Query("pageNum") Integer pageNum,@Query("pageLength") Integer pageLength);

    @GET("/silubium-api/tx/{txid}")
    Call<Transaction> getTransaction(@Path("txid") String txid);

    @POST("/silubium-api/tx/send")
    Call<SendResult> sendRawTransaction(@Body SendRawTransactionRequest sendRawTransactionRequest);

    @GET("/silubium-api/src20/{contractAddress}")
    Call<Contract> getContract(@Path("contractAddress") String contractAddress);


    /**
     *
     20:20:11
     ￼
     callcontract "address" "data" ( address )

     Argument:
     1. "address"          (string, required) The account address
     2. "data"             (string, required) The data hex string
     3. address              (string, optional) The sender address hex string
     4. gasLimit             (string, optional) The gas limit for executing the contract

     * @param contractAddress
     * @param contractHash
     * @param from
     * @return
     */
    @GET("/silubium-api/contracts/{contractAddress}/hash/{contractHash}/call")
    Call<CallResult> contractCall(@Path("contractAddress") String contractAddress, @Path("contractHash") String contractHash, @Query("from") String from);
}
