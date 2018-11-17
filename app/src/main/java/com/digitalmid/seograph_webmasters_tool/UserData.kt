package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import com.google.gson.annotations.SerializedName
import java.util.*

class UserData {

    var name: String = ""
    var comment: String = ""
    var comment1: String = ""
    var change: String = ""


    constructor(name: String, comment: String, comment1: String, change: String) {
        this.name = name
        this.comment = comment
        this.comment1 = comment1
        this.change = change
    }

    /*var error: String? = null
    var message: String? = null*/
    var websitedetails: String? = null
    //var currentweek: List<CurrentWeek> = ArrayList<CurrentWeek>()
    var lastweek: List<LastWeek> = ArrayList<LastWeek>()

   /* @SerializedName("error")
    private var error: Boolean = false
    @SerializedName("message")
    private var message: String? = null

    @SerializedName("currentweek")
    var currentweek: ArrayList<CurrentWeek>? = null*/



}