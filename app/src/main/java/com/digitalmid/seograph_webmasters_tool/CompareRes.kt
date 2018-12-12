package com.digitalmid.seograph_webmasters_tool

import com.google.gson.annotations.SerializedName

data class CompareRes(
        @SerializedName("currentweek")
        var currentweek: List<Currentweek>,
        @SerializedName("diff")
        var diff: List<Diff>,
        @SerializedName("error")
        var error: Boolean,
        @SerializedName("lastweek")
        var lastweek: List<Lastweek>,
        @SerializedName("message")
        var message: String
) {
    data class Currentweek(
            @SerializedName("clicks")
            var clicks: String,
            @SerializedName("date")
            var date: String

    )

    data class Lastweek(
            @SerializedName("clicks")
            var clicks: String,
            @SerializedName("date")
            var date: String
    )

    data class Diff(
            @SerializedName("different")
            var different: Int
    )
}