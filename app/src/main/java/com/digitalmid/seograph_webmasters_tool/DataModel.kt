package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import java.util.ArrayList

class DataModel {

    private var error: Boolean = false

    private var message: String? = null

    private var currentWeek: List<CurrentWeek> = ArrayList<CurrentWeek>()

    private var lastWeek: List<LastWeek> = ArrayList<LastWeek>()

    private var different: List<Diff> = ArrayList<Diff>()


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

    fun setCurrentWeek(currentWeek: List<CurrentWeek>): List<CurrentWeek> {
       return currentWeek
    }


    fun getCurrentWeek(): List<CurrentWeek> {
        return currentWeek
    }

    fun setLastWeek(lastWeek: List<LastWeek>): List<LastWeek> {
        return lastWeek
    }

    fun getLastWeek(): List<LastWeek> {
        return lastWeek
    }

    fun setDiff(different: List<Diff>): List<Diff> {
        return different
    }

    fun getDiff(): List<Diff> {
        return different
    }

}