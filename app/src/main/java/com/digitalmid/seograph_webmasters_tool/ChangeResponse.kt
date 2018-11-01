package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class ChangeResponse {
    @SerializedName("error")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("websitedetails")
    var websitedetails: String? = null

    @SerializedName("currentweek")
    var currentweek: ArrayList<Analytics>? = null
}