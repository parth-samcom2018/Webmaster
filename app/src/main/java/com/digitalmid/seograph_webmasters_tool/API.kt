package com.digitalmid.seograph_webmasters_tool

import retrofit.Callback
import retrofit.client.Response
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

public interface API {

    @FormUrlEncoded
    @POST("/api/checkLogin")
    fun postData(@Field("userName") userName: String,
                 @Field("email") emailid: String?,
                 @Field("userDeviceModel") userdevicemodel: String,
                 @Field("websiteURL") websiteurl: String,
                 @Field("idToken") idtoken: String,
                 @Field("profilePicUrl") profileurl: String?,
                 @Field("accessToken") accesstoken: String,
                 @Field("refreshToken") refreshtoken: String,
                 callback: Callback<Response>)


    /*@POST("/api/checkLogin")
    fun postData(@Body loginModel: LoginModel,
                 callback: Callback<LoginModel>)*/
}
