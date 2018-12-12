import com.google.gson.annotations.SerializedName

data class CompareData(
        @SerializedName("error")
        val error: Boolean,
        @SerializedName("message")
        val message: String,
        @SerializedName("websitedetails")
        val websitedetails: Websitedetails
) {
    data class Websitedetails(
            @SerializedName("currentweek")
            val currentweek: List<Any>,
            @SerializedName("diff")
            val diff: List<Any>,
            @SerializedName("lastweek")
            val lastweek: List<Lastweek>
    ) {
        data class Lastweek(
                @SerializedName("clicks")
                val clicks: String,
                @SerializedName("date")
                val date: String
        )
    }
}