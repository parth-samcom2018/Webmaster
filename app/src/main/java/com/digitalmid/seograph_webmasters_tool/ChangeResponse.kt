package com.digitalmid.seograph_webmasters_tool

data class ChangeResponse(
        val error: Boolean,
        val message: String,
        val websitedetails: Websitedetails
) {
    data class Websitedetails(
            val currentweek: List<Currentweek>,
            val diff: List<Int>,
            val lastweek: List<Lastweek>
    ) {
        data class Currentweek(
                val clicks: String,
                val date: String
        )

        data class Lastweek(
                val clicks: String,
                val date: String
        )
    }
}