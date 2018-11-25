package com.blasthack.storm.lottostorm.service

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

interface StormBackendService {

    companion object {

        private const val tag = "EXPO_REST"
        private const val baseUrl = "http://192.168.43.245:90/"

        fun <T : Any> create(type: Class<T>): T {
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d(tag, it)
            })
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build()

            val httpUrl = HttpUrl.parse(baseUrl)!!

            return Retrofit.Builder()
                    .baseUrl(httpUrl)
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(type)
        }
    }
}