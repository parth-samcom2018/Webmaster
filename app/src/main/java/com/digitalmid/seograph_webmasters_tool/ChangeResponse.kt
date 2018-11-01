package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class ChangeResponse {
    /**
     * error : false
     * message : success
     * websitedetails : {"currentweek":[{"clicks":"12","date":"2018-10-24"},{"clicks":"12","date":"2018-10-25"},{"clicks":"10","date":"2018-10-26"},{"clicks":"12","date":"2018-10-27"},{"clicks":"8","date":"2018-10-28"},{"clicks":"14","date":"2018-10-29"}],"lastweek":[{"clicks":"5","date":"2018-10-17"},{"clicks":"10","date":"2018-10-18"},{"clicks":"8","date":"2018-10-19"},{"clicks":"10","date":"2018-10-20"},{"clicks":"28","date":"2018-10-21"},{"clicks":"26","date":"2018-10-22"},{"clicks":"20","date":"2018-10-23"},{"clicks":"12","date":"2018-10-24"}],"diff":[7,2,2,2,-20,-12]}
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
        var currentweek: List<CurrentweekEntity>? = null
        @SerializedName("lastweek")
        var lastweek: List<LastweekEntity>? = null
        @SerializedName("diff")
        var diff: List<Int>? = null

        class CurrentweekEntity {
            /**
             * clicks : 12
             * date : 2018-10-24
             */

            @SerializedName("clicks")
            var clicks: String? = null
            @SerializedName("date")
            var date: String? = null
        }

        class LastweekEntity {
            /**
             * clicks : 5
             * date : 2018-10-17
             */

            @SerializedName("clicks")
            var clicks: String? = null
            @SerializedName("date")
            var date: String? = null
        }
    }




}