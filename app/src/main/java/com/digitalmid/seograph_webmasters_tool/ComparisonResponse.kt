package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

class ComparisonResponse {

    /*@SerializedName("error")
    var error: Boolean? = false

    @SerializedName("message")
    var message: String? = null

    @SerializedName("websitedetails")
    var websitedetails: String? = null

    @SerializedName("currentweek")
    var currentweek: ArrayList<Diff>? = null

    @SerializedName("lastweek")
    var lastweek: ArrayList<Diff>? = null*/

    @SerializedName("error")
    private var error: Boolean = false
    @SerializedName("message")
    private var message: String? = null
    @SerializedName("currentweek")
    private var currentWeeks: List<CurrentWeek>? = null
    @SerializedName("lastweek")
    private var lastWeeks: List<LastWeek>? = null
    @SerializedName("diff")
    private var diff: List<Diff>? = null

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


    fun getCurrentWeeks(): List<CurrentWeek>? {
        return currentWeeks
    }

    fun setCurrentWeeks(currentWeeks: List<CurrentWeek>) {
        this.currentWeeks = currentWeeks
    }

    fun getLastWeeks(): List<LastWeek>? {
        return lastWeeks
    }

    fun setLastWeeks(lastWeeks: List<LastWeek>) {
        this.lastWeeks = lastWeeks
    }

    fun getDiff(): List<Diff>? {
        return diff
    }

    fun setDiff(diff: List<Diff>) {
        this.diff = diff
    }
}