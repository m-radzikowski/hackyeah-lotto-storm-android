package com.blasthack.storm.lottostorm.service

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
    @POST("/rest/v1/notify")
    fun sendNotification(@Body data: NotifyFriendBody): Single<IdResponse>
}