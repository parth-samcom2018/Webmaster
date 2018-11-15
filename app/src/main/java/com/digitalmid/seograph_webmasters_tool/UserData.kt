package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import java.net.URL
import java.util.*

class UserData {
    var name: String = ""
    var comment: String = ""

    constructor() {}

    constructor(name: String, comment: String) {
        this.name = name
        this.comment = comment
    }

    var error: String? = null
    var message: String? = null
    var websitedetails: String? = null
    var currentweek: List<CurrentWeek> = ArrayList<CurrentWeek>()
    var clicks: Int? = null
    var page: URL? = null
    var date: Date? = null
    var country: String? = null
    var devices: String? = null
}