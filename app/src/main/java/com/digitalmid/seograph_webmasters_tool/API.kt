package com.digitalmid.seograph_webmasters_tool

import retrofit.Callback
import retrofit.client.Response
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

public interface API {


    @FormUrlEncoded
    @POST("/api/checkLogin")
    fun postData(@Field("accountID") accountId: String,
                 @Field("userName") userName: String,
                 @Field("email") email: String,
                 @Field("userDeviceModel") userDeviceModel: String,
                 @Field("serverAuthCode") serverAuthCode: String,
                 @Field("idToken") idToken: String,
                 @Field("profilePicUrl") profilePicUrl: String,
                 @Field("accessToken") accessToken: String,
                 @Field("refreshToken") refreshToken: String,
                 @Field("tokenExpiry") tokenExpiry: String,
                 @Field("jsontoken") jsontoken: String,
                 @Field("googleAccessTokenResponse") googleAccessTokenResponse: String,

                 callback: Callback<Response>)


    @FormUrlEncoded
    @POST("/api/getNewWebsite")
    fun postWebsite(@Field("email") email: String,
                    @Field("websiteURL") websiteURL: String,
                    callback: Callback<Response>)

    @FormUrlEncoded
    @POST("/api/deleteWebsite")
    fun deleteWebsite(@Field("email") email: String,
                      @Field("websiteURL") websiteURL: String,
                      callback: Callback<Response>)


    @FormUrlEncoded
    @POST("/api/getAnalyticsClicks")
    fun getAnalyticsClicks(@Field("firstDate") firstDate: String,
                           @Field("secondDate") secondDate: String,
                           @Field("websiteURL") websiteURL: String,
                           @Field("thirdDate") thirdDate: String,
                           @Field("fourDate") fourDate: String,
                           @Field("groupBy") groupBy: String,
                           @Field("keyword") keyword: String,
                           callback: Any)


}
