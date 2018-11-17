package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import java.util.ArrayList

class DataModel {

    private var error: Boolean = false

    private var message: String? = null

    private var currentWeek: List<CurrentWeek> = ArrayList<CurrentWeek>()

    private var lastWeek: List<LastWeek> = ArrayList<LastWeek>()


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

    fun setData(data: List<CurrentWeek>): List<CurrentWeek> {
       return currentWeek
    }


    fun getData(): List<CurrentWeek> {
        return currentWeek
    }

}