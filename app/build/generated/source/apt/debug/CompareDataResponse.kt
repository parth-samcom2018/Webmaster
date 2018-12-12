import com.google.gson.annotations.SerializedName

data class CompareDataResponse(
        @SerializedName("error")
        var error: Boolean,
        @SerializedName("message")
        var message: String,
        @SerializedName("websitedetails")
        var websitedetails: Websitedetails
) {
    data class Websitedetails(
            @SerializedName("currentweek")
            var currentweek: List<Any>,
            @SerializedName("diff")
            var diff: List<Any>,
            @SerializedName("lastweek")
            var lastweek: List<Lastweek>
    ) {
        data class Lastweek(
                @SerializedName("clicks")
                var clicks: String,
                @SerializedName("date")
                var date: String
        )
    }
}