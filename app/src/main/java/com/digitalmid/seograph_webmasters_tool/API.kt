package com.digitalmid.seograph_webmasters_tool

import retrofit.Callback
import retrofit.client.Response
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.Header
import retrofit.http.POST

public interface API {


    /*@POST("/api/checkLogin")
    fun postData(@Body loginModel: LoginModel,
                 callback: Callback<LoginModel>)*/


    /*@FormUrlEncoded
    @POST("/api/checkLogin")
    fun postData(@Field("userName") userName: String,
                 @Field("email") email: String,
                 @Field("userDeviceModel") userDeviceModel: String,
                 @Field("serverAuthCode") serverAuthCode: String,
                 @Field("idToken") idToken: String,
                 @Field("accessToken") accessToken: String,
                 @Field("refreshToken") refreshToken: String,
                 @Field("tokenExpiry") tokenExpiry: String,
                 @Field("jsontoken") jsontoken: String,
                 @Field("googleAccessTokenResponse") googleAccessTokenResponse: String,
                 callback: Callback<Response>)*/

    @FormUrlEncoded
    @POST("/api/checkLogin")
    fun postData(@Field("userName") userName: String,
                 @Field("email") email: String,
                 @Field("userDeviceModel") userDeviceModel: String,
                 @Field("serverAuthCode") serverAuthCode: String,
                 @Field("idToken") idToken: String,
                 @Field("accessToken") accessToken: String,
                 @Field("refreshToken") refreshToken: String,
                 @Field("googleAccessTokenResponse") googleAccessTokenResponse: String,
                 @Field("tokenExpiry") tokenExpiry: String,
                 @Field("jsontoken") jsontoken: String,
              callback: Callback<Response>)


    @FormUrlEncoded
    @POST("/api/getNewWebsite")
    fun postWebsite(@Field("email") email: String,
                             @Field("websiteURL") websiteURL: String,
                             callback: Callback<Response>)

    @FormUrlEncoded
    @POST("/api/deleteWebsite ")
    fun deleteWebsite(@Field("email") email: String,
                      @Field("websiteURL") websiteURL: String,
                      callback: Callback<Response>)
}
