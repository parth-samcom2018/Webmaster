package com.digitalmid.seograph_webmasters_tool

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import org.json.JSONObject

/**
 * Created by dr_success on 7/10/2017.
 */

/**
 * getUserInfo
 */
fun getUserInfo(context: Context): JSONObject?{

    //set UserInfoJsonObj to null
    var UserInfoJsonObj: JSONObject? = null

    //lets get shared pref
     val UserInfoJsonStr = getSharedPref(context, "user_info")

    //lets now convert the data to json object
    if(UserInfoJsonStr is String){
        UserInfoJsonObj = JSONObject(UserInfoJsonStr)
    }

    //return userInfo
    return UserInfoJsonObj
}//end get user info


//revoke account permission
fun removeUserInfo(activity: Activity):Boolean{

    removeSharedPrefItem(activity,"user_info")

    //start class activity
    startClassActivity<AuthActivity>(activity,AuthActivity::class.java,true)

    //Toast.make
    //finish the current activity
    activity.finish()

    return true
}//end remove user info
