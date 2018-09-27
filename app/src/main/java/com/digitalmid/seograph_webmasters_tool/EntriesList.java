package com.digitalmid.seograph_webmasters_tool;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EntriesList {


    @SerializedName("countPerTypes")
    private List<CountPerTypesEntity> countPerTypes;

    public List<CountPerTypesEntity> getCountPerTypes() {
        return countPerTypes;
    }

    public void setCountPerTypes(List<CountPerTypesEntity> countPerTypes) {
        this.countPerTypes = countPerTypes;
    }

    public static class CountPerTypesEntity {
        /**
         * category : notFound
         * entries : [{"count":"1","timestamp":"2018-08-09T20:12:41.028Z"}]
         * platform : web
         */

        @SerializedName("category")
        private String category;
        @SerializedName("platform")
        private String platform;
        @SerializedName("entries")
        private List<EntriesEntity> entries;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public List<EntriesEntity> getEntries() {
            return entries;
        }

        public void setEntries(List<EntriesEntity> entries) {
            this.entries = entries;
        }

        public static class EntriesEntity {
            /**
             * count : 1
             * timestamp : 2018-08-09T20:12:41.028Z
             */

            @SerializedName("count")
            private String count;
            @SerializedName("timestamp")
            private String timestamp;

            public String getCount() {
                return count;
            }

            public void setCount(String count) {
                this.count = count;
            }

            public String getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(String timestamp) {
                this.timestamp = timestamp;
            }
        }
    }
}
