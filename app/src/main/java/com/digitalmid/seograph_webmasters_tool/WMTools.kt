package com.digitalmid.seograph_webmasters_tool

import android.app.Activity
import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDialog
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.webmasters.Webmasters
import com.google.api.services.webmasters.model.*
import org.jetbrains.anko.longToast
import org.json.JSONObject
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response


/**
 * Created by dr_success on 7/30/2017.
 * webmasters tools class
 */
class WMTools {

    var mContext: Context? = null
    /**
     * init webmastersTool
     * @param context
     * initApiCore
     */
    fun apiService(activity: Activity): Webmasters {

        //lets get user Info
        val userInfo = getUserInfo(activity)


        val httpTransport = NetHttpTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()

        //create google credentials
        val credential = GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(googleAouth2WebClientId, googleAoauth2ClientSecret)
                .build()
                .setAccessToken(userInfo!!.getString("access_token"))
                .setRefreshToken(userInfo.getString("refresh_token"))
                .setExpiresInSeconds(userInfo.getLong("token_expiry"))

        // Create a new authorized API client
        val service = Webmasters.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(appName)
                .build()


        return service
    }//end


    /**
     * addSite
     * @param siteUrl
     */
    fun addSiteDialog(activity: AppCompatActivity): AppCompatDialog {

        val parent = activity.window.decorView.rootView as ViewGroup

        val dialogLayout = activity
                .layoutInflater
                .inflate(
                        R.layout.add_site_dialog_layout,
                        parent,
                        false
                )


        val dialog = AppCompatDialog(activity)
        //dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setTitle(R.string.add_site)

        //set layout view
        dialog.setContentView(dialogLayout)

        //set dialog to full screen
        dialog.window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        )


        //lets get fields
        val siteUrlEditText = dialogLayout
                .findViewById<AppCompatEditText>(R.id.site_url)

        //editText Layout
        val inputLayout: TextInputLayout = dialogLayout
                .findViewById<TextInputLayout>(R.id.text_input_layout)


        //add btn
        val saveBtn: AppCompatButton = dialogLayout
                .findViewById<AppCompatButton>(R.id.save_site)

        //cancel btn
        val cancelBtn: AppCompatButton = dialogLayout
                .findViewById<AppCompatButton>(R.id.cancel)

        //on cancel clicked
        cancelBtn.setOnClickListener {
            //close dialog
            dialog.dismiss()
        }//end

        //on save clicked
        saveBtn.setOnClickListener {

            inputLayout.error = ""

            //lets first validate the url first
            val siteUrl: String = siteUrlEditText.text.toString()


            //if empty, send error
            if (siteUrl.isNullOrEmpty()) {
                inputLayout
                        .error = activity.getString(R.string.site_url_required)

                return@setOnClickListener //end excution
            }//end if empty

            //lets check if the url is valid
            if (!isURLValid(siteUrl)) {

                inputLayout
                        .error = activity.getString(R.string.invalid_url)

                return@setOnClickListener
            }//end if url is not valid


            //lets submit new site in bg mode
            submitNewSite(activity, siteUrl)

            //lets set the text edit to null
            //and close dialog
            siteUrlEditText.setText("")


            dialog.dismiss()
        }//end on click listener

        return dialog
    }//end add site dialog


    //lets now proccess add site
    fun submitNewSite(
            activity: AppCompatActivity,
            siteUrl: String) {

        val submitToGoogleTask = {

            //google api service
            val apiService = apiService(activity)

            //prepare the request
            val request = apiService.sites().add(siteUrl)

            request.execute()
        }//end submit to google


        //start progress bar
        activity
                .findViewById<ProgressBar>(R.id.progress_bar)
                .visibility = View.VISIBLE


        //before submit to google task
        val beforeTask = {
            activity.longToast(R.string.adding_site)
        }


        //after task, when resumed from background
        val afterTask = fun(curActivity: Activity,
                            result: Void?): Unit {

            Log.e("FINISH", "saved")

            val parentView = activity
                    .findViewById<CoordinatorLayout>(R.id.parentView)

            //show snackbar
            val sb = Snackbar
                    .make(
                            parentView,
                            siteUrl + " " + curActivity.getString(R.string.site_added_success),
                            Snackbar.LENGTH_INDEFINITE
                    );
            sb.setAction(R.string.ok, { sb.dismiss() })
            sb.show()

            val userInfo = getUserInfo(activity)


            if (userInfo != null) {
                val userEmail = userInfo.getString("email")
                Log.d("user", "email : " + userEmail)

                DM().getApi().postWebsite(userEmail, siteUrl, object : Callback<Response> {
                    override fun success(response: Response, response2: Response) {
                        Log.d("onsuccess", "add website" + response)
                    }

                    override fun failure(error: RetrofitError) {
                        Log.d("onfailed", "failed to add" + error)
                    }
                })
            }


            //stop progress bar
            activity
                    .findViewById<ProgressBar>(R.id.progress_bar)
                    .visibility = View.GONE

            //lets reload sites list
            (activity as SitesListActivity).fetchSitesListFromConsole()
        }//end after task fun


        //lets run in bg
        runInBg<Void?>(
                activity = activity,
                task = submitToGoogleTask,
                beforeTask = beforeTask,
                afterTask = afterTask
        )//end run in background

    }//lets submit site


    //delete site
    fun deleteSite(activity: Activity, siteUrl: String) {

        val deleteSiteTask = {

            //google api service
            val apiService = apiService(activity)

            //prepare the request
            val request = apiService.sites().delete(siteUrl)

            request.execute()
        }//end submit to google

        //after task, when resumed from background
        val afterTask = fun(activity: Activity,
                            result: Void?) {

            Log.e("FINISH", "Tasked Finished")


            //show toast that the item has been deleted
            activity.longToast(R.string.site_delete_success)

            //lets reload sites list
            (activity as SitesListActivity).fetchSitesListFromConsole()

            val userInfo = getUserInfo(activity)


            if (userInfo != null) {
                val userEmail = userInfo.getString("email")
                Log.d("user", "email : " + userEmail)
                Log.d("user", "url : " + siteUrl)

                DM().getApi().deleteWebsite(userEmail, siteUrl, object : Callback<Response> {
                    override fun success(response: Response, response2: Response) {

                        Log.d("onsuccess", "delete website" + response)
                    }

                    override fun failure(error: RetrofitError) {
                        Log.d("onfailed", "failed to delete" + error)
                    }
                })
            }

            //DM().getApi().deleteWebsite()
            //stop progress bar
            // activity
            //       .findViewById<ProgressBar>(R.id.progress_bar)
            //     .visibility =  View.GONE
        }//end afterTask


        //lets run in bg
        runInBg<Void?>(
                activity = activity,
                task = deleteSiteTask,
                afterTask = afterTask
        )//end run in background
    }//end delete site


    //fetch analytics
    fun fetchSiteAnalytics(activity: Activity,
                           siteUrl: String,
                           options: JSONObject,
                           queryFilter: MutableMap<String, String>,
                           queryGrouping: List<String>,
                           afterTask: (activity: Activity,
                                       result: SearchAnalyticsQueryResponse?) -> Unit) {

        val task = {

            //google api service
            val apiService = apiService(activity)

            val queryOpts = SearchAnalyticsQueryRequest()

            //lets loop optsMu
            for (key in options.keys()) {
                queryOpts.set(key, options[key].toString())
            }

            //end if query grouping isnt empty
            if (queryGrouping.isNotEmpty()) {
                queryOpts.dimensions = queryGrouping
            }//end if

            //if the query filters is not empty, lets add it
            if (queryFilter.isNotEmpty()) {

                //all proccessed filters will go here
                var proccessedFilters = mutableListOf<ApiDimensionFilter>()

                //loop and get values
                for ((name, value) in queryFilter) {

                    //set filters
                    var filter = ApiDimensionFilter()
                    filter.dimension = name
                    filter.operator = "contains"
                    filter.expression = value

                    //lets add it to proccessedFilters
                    proccessedFilters.add(filter)
                }//end

                //filter group
                var filterGroup = ApiDimensionFilterGroup()

                //lets add proccessed filter list to the filter group
                filterGroup.filters = proccessedFilters

                //lets add to query options
                queryOpts.dimensionFilterGroups = mutableListOf(filterGroup)
            }//end if  we have query filters

            //Log.e("Filters", varDump(queryOpts))


            //prepare the request
            val request = apiService
                    .searchanalytics()
                    .query(siteUrl, queryOpts)


            request.execute()

        }//end task


        //lets run in bg
        runInBg<SearchAnalyticsQueryResponse>(
                activity = activity,
                task = task,
                afterTask = afterTask
        )//end run in background


    }//end fetch site analytics


    fun fetchSiteComparisonOld(mActivity: Activity,
                               siteUrl: String,
                               queryOptionsOld: JSONObject,
                               queryFilter: MutableMap<String, String>,
                               queryGrouping: MutableList<String>,
            /*queryGroups: MutableList<String>,*/
                               afterTask: (Activity, SearchAnalyticsQueryResponse?) -> Unit) {

        val task = {

            //google api service
            val apiService = apiService(mActivity)

            val queryOpts = SearchAnalyticsQueryRequest()
            val queryElmts = SearchAnalyticsQueryRequest()

            //lets loop optsMu
            for (key in queryOptionsOld.keys()) {
                queryOpts.set(key, queryOptionsOld[key].toString())
            }


            //end if query grouping isnt empty
            if (queryGrouping.isNotEmpty()) {
                queryOpts.dimensions = queryGrouping
            }//end if

            /*if (queryElements.isNotEmpty()) {
                queryElmts.rowLimit = queryElements.size
            }*/

            /*if (queryGroups.isNotEmpty()) {
                queryElmts.rowLimit = queryGroups.size
            }*/

            //if the query filters is not empty, lets add it
            if (queryFilter.isNotEmpty()) {

                //all proccessed filters will go here
                var proccessedFilters = mutableListOf<ApiDimensionFilter>()

                //loop and get values
                for ((name, value) in queryFilter) {

                    //set filters
                    var filter = ApiDimensionFilter()
                    filter.dimension = name
                    filter.operator = "contains"
                    filter.expression = value

                    //lets add it to proccessedFilters
                    proccessedFilters.add(filter)
                }//end

                //filter group
                var filterGroup = ApiDimensionFilterGroup()

                //lets add proccessed filter list to the filter group
                filterGroup.filters = proccessedFilters

                //lets add to query options
                queryOpts.dimensionFilterGroups = mutableListOf(filterGroup)
                //queryElmts.dimensionFilterGroups = mutableListOf(filterGroup)
            }//end if  we have query filters

            //Log.e("Filters", varDump(queryOpts))


            //prepare the request
            val request = apiService
                    .searchanalytics()
                    .query(siteUrl, queryOpts)


            request.execute()

        }//end task

        //lets run in bg
        runInBg<SearchAnalyticsQueryResponse>(
                activity = mActivity,
                task = task,
                afterTask = afterTask
        )//end run in background
    }


    //fetch comparison
    fun fetchSiteComparison(activity: Activity,
                            siteUrl: String,
                            options: JSONObject,
                            queryFilter: MutableMap<String, String>,
                            queryGrouping: List<String>,
            /*queryGroups: MutableList<String>,*/
                            afterTask: (activity: Activity,
                                        result: SearchAnalyticsQueryResponse?) -> Unit) {

        val task = {

            //google api service
            val apiService = apiService(activity)

            val queryOpts = SearchAnalyticsQueryRequest()
            val queryElmts = SearchAnalyticsQueryRequest()

            //lets loop optsMu
            for (key in options.keys()) {
                queryOpts.set(key, options[key].toString())
            }

            //end if query grouping isnt empty
            if (queryGrouping.isNotEmpty()) {
                queryOpts.dimensions = queryGrouping
            }//end if

            /*if (queryElements.isNotEmpty()) {
                queryElmts.rowLimit = queryElements.size
            }*/


            /*if (queryGroups.isNotEmpty()) {
                queryElmts.rowLimit = queryGroups.size
            }*/


            //if the query filters is not empty, lets add it
            if (queryFilter.isNotEmpty()) {

                //all proccessed filters will go here
                var proccessedFilters = mutableListOf<ApiDimensionFilter>()

                //loop and get values
                for ((name, value) in queryFilter) {

                    //set filters
                    var filter = ApiDimensionFilter()
                    filter.dimension = name
                    filter.operator = "contains"
                    filter.expression = value

                    //lets add it to proccessedFilters
                    proccessedFilters.add(filter)
                }//end

                //filter group
                var filterGroup = ApiDimensionFilterGroup()

                //lets add proccessed filter list to the filter group
                filterGroup.filters = proccessedFilters

                //lets add to query options
                queryOpts.dimensionFilterGroups = mutableListOf(filterGroup)
                //queryElmts.rowLimit = mutableListOf(filterGroup)
            }//end if  we have query filters

            //Log.e("Filters", varDump(queryOpts))


            //prepare the request
            val request = apiService
                    .searchanalytics()
                    .query(siteUrl, queryOpts)


            request.execute()

        }//end task

        //lets run in bg
        runInBg<SearchAnalyticsQueryResponse>(
                activity = activity,
                task = task,
                afterTask = afterTask
        )//end run in background
    }


    fun fetchSiteError(mActivity: Activity, siteUrl: String,
                       options: JSONObject,
                       queryFilter: List<String>,
                       afterTask: (Activity, UrlCrawlErrorsCountsQueryResponse?) -> Unit) {
        val task = {

            //google api service
            val apiService = apiService(mActivity)

            val queryOpts = UrlCrawlErrorsCountsQueryResponse()


            //lets loop optsMu
            for (key in options.keys()) {
                queryOpts.set(key, options[key].toString())
            }

            //end if query grouping isnt empty
            if (queryFilter.isNotEmpty()) {
                queryOpts.countPerTypes
            }//end if


            //if the query filters is not empty, lets add it
            if (queryFilter.isNotEmpty()) {

                //var proccessedFilters = mutableListOf<UrlCrawlErrorCount>()
                var proccessedFilters = mutableListOf<UrlCrawlErrorCountsPerType>()

                //loop and get values
                for ((name, value) in queryOpts) {

                    //set filters
                    var filter = UrlCrawlErrorCountsPerType()

                    filter.platform = name
                    filter.category = value.toString()
                    filter.getEntries()[0].count.toInt()
                    filter.getEntries()[0].timestamp.toString()


                    //filter.timestamp = value.toString()
                    //filter.timestamp = value

                    proccessedFilters.add(filter)
                }//end


                var filterGroup = UrlCrawlErrorCountsPerType()

                filterGroup.category = mutableListOf(filterGroup).toString()

                //queryOpts.platform = mutableListOf(filterGroup).toString()
            }


            val request = apiService
                    .urlcrawlerrorscounts()
                    .query(siteUrl)


            request.execute()

        }//end task

        //lets run in bg
        runInBg<UrlCrawlErrorsCountsQueryResponse>(
                activity = mActivity,
                task = task,
                afterTask = afterTask
        )//end run in background
    }

    fun fetchSitemap(mActivity: Activity, siteUrl: String,
                     afterTask: (Activity, SitemapsListResponse?) -> Unit) {
        val task = {

            //google api service
            val apiService = apiService(mActivity)

            val queryOpts = SitemapsListResponse()


            val request = apiService.sitemaps().list(siteUrl)


            /*val request = apiService
                    .urlcrawlerrorscounts()
                    .query(siteUrl)*/


            request.execute()

        }//end task

        //lets run in bg
        runInBg<SitemapsListResponse>(
                activity = mActivity,
                task = task,
                afterTask = afterTask
        )//end run in background
    }


}//end class