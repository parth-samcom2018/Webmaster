package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import com.google.gson.annotations.SerializedName
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class ResponseModel {

    /*@SerializedName("error")
    var error: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("websitedetails")
    var websitedetails: String? = null

    @SerializedName("currentweek")
    var currentweek: ArrayList<CurrentWeek>? = null*/



    /*var clicks: Int = 0
    var position: Int = 0
    var impressions: Int = 0
    var ctr: Int = 0

    var date: Date? = null
    var keyword: String? = null
    var country: String? = null
    var page: URL? = null

    @SerializedName("currentweek")
    var currentweek: ArrayList<CurrentWeek>? = null*/

    /**
     * error : false
     * message : success
     * websitedetails : {"currentweek":[{"clicks":"6","date":"2018-11-06"},{"clicks":"5","date":"2018-11-07"},{"clicks":"15","date":"2018-11-08"},{"clicks":"13","date":"2018-11-09"},{"clicks":"12","date":"2018-11-10"},{"clicks":"22","date":"2018-11-11"}],"lastweek":[{"clicks":"5","date":"2018-10-09"},{"clicks":"8","date":"2018-10-10"},{"clicks":"18","date":"2018-10-11"},{"clicks":"18","date":"2018-10-12"},{"clicks":"8","date":"2018-10-13"},{"clicks":"6","date":"2018-10-14"},{"clicks":"10","date":"2018-10-15"},{"clicks":"10","date":"2018-10-16"}],"diff":[1,-3,-3,-5,4,16]}
     */

    @SerializedName("error")
    private var error: Boolean = false
    @SerializedName("message")
    private var message: String? = null
    @SerializedName("websitedetails")
    private var websitedetails: WebsitedetailsEntity? = null

    fun isError(): Boolean {
        return error
    }

    fun setError(error: Boolean) {
        this.error = error
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun getWebsitedetails(): WebsitedetailsEntity? {
        return websitedetails
    }

    fun setWebsitedetails(websitedetails: WebsitedetailsEntity) {
        this.websitedetails = websitedetails
    }

    class WebsitedetailsEntity {
        @SerializedName("currentweek")
        var currentweek: ArrayList<CurrentweekEntity>? = null
        @SerializedName("lastweek")
        var lastweek: ArrayList<LastweekEntity>? = null
        @SerializedName("diff")
        var diff: ArrayList<Int>? = null

        class CurrentweekEntity {
            /**
             * clicks : 6
             * date : 2018-11-06
             */

            @SerializedName("clicks")
            var clicks: Int? = 0
            @SerializedName("ctr")
            var ctr: Int? = 0
            @SerializedName("position")
            var position: Int? = 0
            @SerializedName("impressions")
            var impressions: Int = 0
            @SerializedName("date")
            var date: Date? = null
            @SerializedName("keyword")
            var keyword: String? = null
            @SerializedName("country")
            var country: String? = null
            @SerializedName("page")
            var page: URL? = null
        }

        class LastweekEntity {
            /**
             * clicks : 5
             * date : 2018-10-09
             */

            @SerializedName("clicks")
            var clicks: Int? = null
            @SerializedName("ctr")
            var ctr: Int? = 0
            @SerializedName("position")
            var position: Int? = 0
            @SerializedName("impressions")
            var impressions: Int = 0
            @SerializedName("date")
            var date: Date? = null
            @SerializedName("keyword")
            var keyword: String? = null
            @SerializedName("country")
            var country: String? = null
            @SerializedName("page")
            var page: URL? = null
        }
    }
}