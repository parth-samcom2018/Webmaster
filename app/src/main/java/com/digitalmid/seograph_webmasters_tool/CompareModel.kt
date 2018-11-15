package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import com.google.gson.annotations.SerializedName
import java.net.URL
import java.util.*

class CompareModel {

    @SerializedName("clicks")
    var clicks: Int? = 0

    @SerializedName("date")
    var date: Date? = null

    @SerializedName("keyword")
    var keyword: String? = null

    @SerializedName("country")
    var country: String? = null

    @SerializedName("page")
    var page: URL? = null
}