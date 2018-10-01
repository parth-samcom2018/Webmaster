package com.digitalmid.seograph_webmasters_tool

import com.google.gson.GsonBuilder
import retrofit.RestAdapter
import retrofit.converter.GsonConverter


public class DM {

    var APIROOT = "http://45.55.85.87/SeographWebmaster"


    fun getAuthString(): String {

        return ""
    }

    fun getRestAdapter(): RestAdapter {
        val gson = GsonBuilder()
                .create()

        val api = APIROOT


        return RestAdapter.Builder()
                .setEndpoint(api)
                .setConverter(GsonConverter(gson))
                .build()
    }

    fun getApi(): API {
        val restAdapter = getRestAdapter()
        return restAdapter.create(API::class.java)
    }


}
