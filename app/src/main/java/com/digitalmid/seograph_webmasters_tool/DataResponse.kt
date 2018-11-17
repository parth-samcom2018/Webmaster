package com.digitalmid.seograph_webmasters_tool

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.net.URL

data class DataResponse(
        @SerializedName("error")
        val error: Boolean,
        @SerializedName("message")
        val message: String,
        @SerializedName("websitedetails")
        val websitedetails: String
) {
    inner class Websitedetails(
            @SerializedName("currentweek")
            @Expose
            val currentweek: List<Currentweek>,
            @SerializedName("diff")
            @Expose
            val diff: List<Int>,
            @SerializedName("lastweek")
            @Expose
            val lastweek: List<Lastweek>
    ) {
        inner class Currentweek(
                @SerializedName("clicks")
                @Expose
                val clicks: Int,
                @SerializedName("date")
                @Expose
                val date: String,
                @SerializedName("keyword")
                @Expose
                val keyword: String,
                @SerializedName("country")
                @Expose
                val country: String,
                @SerializedName("page")
                @Expose
                val page: URL,
                @SerializedName("ctr")
                @Expose
                val ctr: Int,
                @SerializedName("position")
                @Expose
                val position: Int,
                @SerializedName("impressions")
                @Expose
                val impressions: Int
        )

        inner class Lastweek(
                @SerializedName("clicks")
                @Expose
                val clicks: Int,
                @SerializedName("date")
                @Expose
                val date: String,
                @SerializedName("keyword")
                @Expose
                val keyword: String,
                @SerializedName("country")
                @Expose
                val country: String,
                @SerializedName("page")
                @Expose
                val page: URL,
                @SerializedName("ctr")
                @Expose
                val ctr: Int,
                @SerializedName("position")
                @Expose
                val position: Int,
                @SerializedName("impressions")
                @Expose
                val impressions: Int
        )
    }
}