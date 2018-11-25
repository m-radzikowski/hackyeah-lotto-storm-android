package com.blasthack.storm.lottostorm.service

import com.blasthack.storm.lottostorm.dto.Balance
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface StormRepository {

    @Headers("Accept: application/json", "Accept: application/json")
    @POST("/rest/v1/login")
    fun register(@Body data: PushClient): Single<IdResponse>

    @Headers("Accept: application/json", "Accept: application/json")
    @POST("/rest/v1/friend/find")
    fun find(@Body data: FriendUserName): Single<IdResponse>

    @Headers("Accept: application/json", "Accept: application/json")
    @POST("/rest/v1/friend/notify")
    fun sendNotification(@Body data: NotifyFriendBody): Single<IdResponse>

    @Headers("Accept: application/json", "Accept: application/json")
    @GET("/rest/v1/wallet/{id}")
    fun getCoupons(@Path("id") id: Int): Single<Balance>

    @Headers("Accept: application/json", "Accept: application/json")
    @GET("/rest/v1/wallet/{id}/add/{amount}")
    fun updateCoupons(@Path("id") id: Int, @Path("amount") amount: Int): Single<Balance>
}