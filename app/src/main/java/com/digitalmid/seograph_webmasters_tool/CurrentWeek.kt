package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import com.google.gson.annotations.SerializedName

class CurrentWeek {

    //var clicks: Int = 0
    //var date: Date? = null

    /*@SerializedName("clicks")
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
    var page: URL? = null*/


    @SerializedName("clicks")
    private var clicks: String? = null
    @SerializedName("ctr")
    private var ctr: String? = null
    @SerializedName("position")
    private var position: String? = null
    @SerializedName("impressions")
    private var impressions: String? = null
    @SerializedName("date")
    private var date: String? = null
    @SerializedName("keyword")
    private var keyword: String? = null
    @SerializedName("country")
    private var country: String? = null

    fun getImpressions(): String? {
        return impressions
    }

    fun setImpressions(impressions: String) {
        this.impressions = impressions
    }

    fun getPosition(): String? {
        return position
    }

    fun setPosition(position: String) {
        this.position = position
    }

    fun getCtr(): String? {
        return ctr
    }

    fun setCtr(ctr: String) {
        this.ctr = ctr
    }


    fun getClicks(): String? {
        return clicks
    }

    fun setClicks(clicks: String) {
        this.clicks = clicks
    }

    fun getDate(): String? {
        return date
    }

    fun setDate(date: String) {
        this.date = date
    }

    fun getKeyword(): String? {
        return keyword
    }

    fun setKeyword(keyword: String) {
        this.keyword = keyword
    }

    fun getCountry(): String? {
        return country
    }

    fun setCountry(country: String) {
        this.country = country
    }
}