package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import com.google.gson.annotations.SerializedName

class Diff {

    @SerializedName("different")
    private var different: String? = null

    fun getDifferent(): String? {
        return different
    }

    fun setDifferent(different: String) {
        this.different = different
    }
}