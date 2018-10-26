package com.digitalmid.seograph_webmasters_tool

class KeyWord {

    var name: String = ""
    var comment: String = ""

    /* var clicks: String = ""
     var ctr: String = ""
     var impressions: String = ""
     var position: String = ""
     var keys: String = ""

     constructor(clicks: String, ctr: String, impressions: String, position: String, keys: String) { }*/

    constructor(name: String, comment: String) {
        this.name = name
        this.comment = comment
    }
}