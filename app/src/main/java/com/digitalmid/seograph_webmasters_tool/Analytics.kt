package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import com.google.gson.annotations.SerializedName

class Analytics {

    @SerializedName("clicks")
    var clicks: Int = 0

    @SerializedName("date")
    var date: String? = null

    @SerializedName("ctr")
    var ctr: Int = 0

    @SerializedName("position")
    var position: Int = 0

    @SerializedName("impressions")
    var impressions: Int = 0
}