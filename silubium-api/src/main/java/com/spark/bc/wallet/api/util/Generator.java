package com.spark.bc.wallet.api.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.spark.bc.wallet.api.exception.ApiError;
import com.spark.bc.wallet.api.exception.ApiException;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

public class Generator {



	private static Retrofit retrofit;

	private static Retrofit.Builder builder = new Retrofit.Builder();

	static {
		try {
			final TrustManager[] trustAllCerts = new TrustManager[]{
					new X509TrustManager() {
						@Override
						public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
						}

						@Override
						public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {

						}

						@Override
						public java.security.cert.X509Certificate[] getAcceptedIssuers() {
							return new java.security.cert.X509Certificate[]{};
						}
					}
			};

			HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
			httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
			OkHttpClient.Builder client = new OkHttpClient.Builder();
			client.interceptors().add(httpLoggingInterceptor);
			client.readTimeout(180, TimeUnit.SECONDS);
			client.connectTimeout(180, TimeUnit.SECONDS);

			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null, null);

			SSLContext sslContext = SSLContext.getInstance("TLS");

			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, "keystore_pass".toCharArray());
			sslContext.init(null, trustAllCerts, new SecureRandom());
			client.sslSocketFactory(sslContext.getSocketFactory())
					.hostnameVerifier(new HostnameVerifier() {
						@Override
						public boolean verify(String hostname, SSLSession session) {
							return true;
						}
					});

			Gson gson = new GsonBuilder().setLenient().create();

			builder.addConverterFactory(GsonConverterFactory.create(gson)).client(client.build());


		} catch (Exception ignored) {

		}


	}

	public static <T> T createService(Class<T> serviceClass, String baseUrl) {
		retrofit = builder.baseUrl(baseUrl).build();
		return retrofit.create(serviceClass);
	}

	public static <T> T executeSync(Call<T> call) {
		try {
			Response<T> response = call.execute();
			if (response.isSuccessful()) {
				return response.body();
			} else {
				ApiError apiError = null;
				try {
					apiError = getApiError(response);
				}catch (Exception e){
					apiError = new ApiError();
					apiError.setError(StringUtils.isEmpty(response.errorBody().string())?response.message():response.errorBody().string());
				}finally {
					throw new ApiException(apiError);
				}

			}
		} catch (Exception e) {
			if(e instanceof  ApiException){
				throw (ApiException)e;
			}
			throw new ApiException(e);
		}
	}

	private static ApiError getApiError(Response<?> response) throws IOException, ApiException {
		return (ApiError) retrofit.responseBodyConverter(ApiError.class, new Annotation[0]).convert(response.errorBody());
	}
}
