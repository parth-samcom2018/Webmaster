package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class CompareData {


    @SerializedName("error")
    private var error: Boolean = false
    @SerializedName("message")
    private var message: String? = null

    @SerializedName("currentweek")
    var currentweek: ArrayList<CurrentWeek>? = null
}