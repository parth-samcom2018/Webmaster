package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import com.google.gson.annotations.SerializedName
import java.net.URL
import java.util.*

class CurrentWeek {

    //var clicks: Int = 0
    //var date: Date? = null

    @SerializedName("clicks")
    var clicks: Int = 0

    @SerializedName("ctr")
    var ctr: Int = 0

    @SerializedName("impressions")
    var impressions: Int = 0

    @SerializedName("position")
    var position: Int = 0

    @SerializedName("date")
    var date: Date? = null

    @SerializedName("country")
    var country: String? = null

    @SerializedName("keyword")
    var keyword: String? = null

    @SerializedName("page")
    var page: URL? = null
}