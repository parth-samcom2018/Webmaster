package com.digitalmid.seograph_webmasters_tool;

import com.google.gson.annotations.SerializedName;

import java.net.URL;
import java.util.List;

public class Dummy {


    /**
     * error : false
     * message : success
     * websitedetails : {"currentweek":[{"clicks":"6","date":"2018-11-06"},{"clicks":"5","date":"2018-11-07"},{"clicks":"15","date":"2018-11-08"},{"clicks":"13","date":"2018-11-09"},{"clicks":"12","date":"2018-11-10"},{"clicks":"22","date":"2018-11-11"}],"lastweek":[{"clicks":"5","date":"2018-10-09"},{"clicks":"8","date":"2018-10-10"},{"clicks":"18","date":"2018-10-11"},{"clicks":"18","date":"2018-10-12"},{"clicks":"8","date":"2018-10-13"},{"clicks":"6","date":"2018-10-14"},{"clicks":"10","date":"2018-10-15"},{"clicks":"10","date":"2018-10-16"}],"diff":[1,-3,-3,-5,4,16]}
     */

    @SerializedName("error")
    private boolean error;
    @SerializedName("message")
    private String message;
    @SerializedName("websitedetails")
    private WebsitedetailsEntity websitedetails;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public WebsitedetailsEntity getWebsitedetails() {
        return websitedetails;
    }

    public void setWebsitedetails(WebsitedetailsEntity websitedetails) {
        this.websitedetails = websitedetails;
    }

    public static class WebsitedetailsEntity {
        @SerializedName("currentweek")
        private List<CurrentweekEntity> currentweek;
        @SerializedName("lastweek")
        private List<LastweekEntity> lastweek;
        @SerializedName("diff")
        private List<Integer> diff;

        public List<CurrentweekEntity> getCurrentweek() {
            return currentweek;
        }

        public void setCurrentweek(List<CurrentweekEntity> currentweek) {
            this.currentweek = currentweek;
        }

        public List<LastweekEntity> getLastweek() {
            return lastweek;
        }

        public void setLastweek(List<LastweekEntity> lastweek) {
            this.lastweek = lastweek;
        }

        public List<Integer> getDiff() {
            return diff;
        }

        public void setDiff(List<Integer> diff) {
            this.diff = diff;
        }

        public static class CurrentweekEntity {
            /**
             * clicks : 6
             * date : 2018-11-06
             */

            @SerializedName("clicks")
            private String clicks;
            @SerializedName("date")
            private String date;
            @SerializedName("keyword")
            private String keyword;
            @SerializedName("country")
            private String country;
            @SerializedName("page")
            private URL page;

            public String getClicks() {
                return clicks;
            }

            public void setClicks(String clicks) {
                this.clicks = clicks;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getKeyword() {
                return keyword;
            }

            public void setKeyword(String keyword) {
                this.keyword = keyword;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public URL getPage() {
                return page;
            }

            public void setPage(URL page) {
                this.page = page;
            }
        }

        public static class LastweekEntity {
            /**
             * clicks : 5
             * date : 2018-10-09
             */

            @SerializedName("clicks")
            private String clicks;
            @SerializedName("date")
            private String date;
            @SerializedName("keyword")
            private String keyword;
            @SerializedName("country")
            private String country;
            @SerializedName("page")
            private URL page;

            public String getClicks() {
                return clicks;
            }

            public void setClicks(String clicks) {
                this.clicks = clicks;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getKeyword() {
                return keyword;
            }

            public void setKeyword(String keyword) {
                this.keyword = keyword;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public URL getPage() {
                return page;
            }

            public void setPage(URL page) {
                this.page = page;
            }
        }
    }
}
