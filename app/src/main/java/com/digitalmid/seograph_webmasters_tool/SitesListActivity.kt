package com.digitalmid.seograph_webmasters_tool

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.api.services.webmasters.Webmasters
import com.google.api.services.webmasters.model.SitesListResponse
import com.google.api.services.webmasters.model.WmxSite
import kotlinx.android.synthetic.main.activity_sites_list.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.doAsync
import org.json.JSONArray
import android.content.SharedPreferences
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.add_site_dialog_layout.*


class SitesListActivity : DrawerActivity(),
        SearchView.OnQueryTextListener {

    private val TAG = "Sitelist"

    //swipe Container
    private val swipeContainer: SwipeRefreshLayout by lazy {
        findViewById<SwipeRefreshLayout>(R.id.swipe_container)
    }//end swipe container

    //global sitesListData data holder for filtering sake
    var sitesListData: ArrayList<MutableMap<String, String>> = ArrayList()

    //get Recycletview
    private val recyclerView: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.sites_list_recycler_view)
    }


    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        setContentView(R.layout.activity_sites_list)

        //note call only oncreate here after the setContentView
        //because the DrawerActivity dont call the setContenView method
        super.onCreate(savedInstanceState)

        var token = FirebaseInstanceId.getInstance().token
        Log.d(TAG,"Token : "+token)
        var bodyMessage= intent.getStringExtra("Notification")
        if(bodyMessage != null){
            body_text_view.text = bodyMessage

            Log.d(TAG, "token: " + token)
        }

        //lets register the add site dialog
        val addSiteDialog = WMTools().addSiteDialog(this)

        //open add new site on fab click
        add_new_site.setOnClickListener { addSiteDialog.show() }

        //lets set fixed size
        this.recyclerView.setHasFixedSize(true)


        val mLayoutManager = LinearLayoutManager(this)

        //set the layout manager
        this.recyclerView.layoutManager = mLayoutManager

        //item touch helper
        var itemTouchHelper = ItemTouchHelper(
                SitesListItemTouchHelper(this, this.recyclerView)
        )//end touch helper

        //add the item touch helper to recyclerView
        itemTouchHelper.attachToRecyclerView(this.recyclerView)


        //////////////End Recycler Stuff////

        //on refresh listener
        swipeContainer.setOnRefreshListener {

            //set refreshing to  true
            swipeContainer.setRefreshing(true)

            //fetch Sites List From COnsole
            fetchSitesListFromConsole()

        }//end on swipeListener

        //set color scheme,reload colors
        swipeContainer.setColorSchemeResources(
                R.color.green,
                R.color.purple,
                R.color.pink,
                R.color.colorPrimary)

        //on initial load, lets check if user has sites, saved
        //we show that
        var dbSitesList = getSharedPref(this, "sites_list")

        //if we found some data
        if (dbSitesList is String) {

            //decode gson to mutableSet
            var decodedSitesData = JSONArray(dbSitesList) as ArrayList<MutableMap<String, String>>

            //lets render the view
            renderSitesListView(decodedSitesData)

        } else {

            //lets fetch the sites
            fetchSitesListFromConsole()
        }//end if

        try {
            Handler().postDelayed({
                MobileAds.initialize(this, "ca-app-pub-8019445966160525~4662817286")

                val adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()
                adView.loadAd(adRequest)
            }, 60000)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }


    }//end onCreate

    //hide refresh spinner
    fun hideResfreshSpinner() {

        //if its still refreshing, we will stop refreshing
        //is still refreshing
        val isRefreshing = swipeContainer.isRefreshing

        //if its still refreshing, we will stop refreshing
        if (isRefreshing != null && isRefreshing == true) {

            //set refreshing to false
            swipeContainer.setRefreshing(false)

        }//end if refreshing

    }//end hide spinner


    //menu inflator
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        //inflate menu
        menuInflater.inflate(R.menu.sites_list_menu, menu)

        //lets add search service
        var searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchMenuItem: MenuItem = menu.findItem(R.id.action_search)

        //get search btn and cast to searchView
        val searchView: SearchView = searchMenuItem.actionView as SearchView

        //set current activity as the searchable activity
        searchView.setSearchableInfo(
                searchManager
                        .getSearchableInfo(componentName)
        )//end

        //set listeners
        searchView.setOnQueryTextListener(this)

        return true
    }//end method


    //on Menu Item select
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemId: Int = item.itemId

        if (itemId == R.id.refresh_list) {
            //set refreshing to  true
            swipeContainer.setRefreshing(true)

            //fetch Sites List From COnsole
            fetchSitesListFromConsole()
        }

        //default action
        else {
            super.onOptionsItemSelected(item)
        }//end if

        return true
    }//end method

    //onTextChange for the search view
    override fun onQueryTextChange(newText: String): Boolean {

        //run proccessor
        execFilterSiteListData(newText)

        return true
    }//end

    //onQueryTextSubmit
    override fun onQueryTextSubmit(query: String?): Boolean {

        //run proccessor
        execFilterSiteListData(query)

        return true
    }//end on query Text Submit


    /**
     * filter method for filter
     * @param text String
     */
    fun execFilterSiteListData(filterText: String?): Boolean {

        var tmpDataSet: ArrayList<MutableMap<String, String>> = ArrayList()

        //if text is empty, we will set,the adapter to full data set
        if (filterText.isNullOrEmpty()) {
            this.recyclerView.adapter = SitesListAdapter(this.sitesListData)

            return true
        }//end if empty

        //lets convert it to charsquence required by .contains
        filterText as CharSequence

        //lets loop and find possible guess
        for (siteData in this.sitesListData) {

            var siteUrl = siteData["site_url"]

            siteUrl = siteUrl?.replace("https?:\\/\\/", "")

            val matchFound = siteUrl?.contains(filterText, true)

            //if we have our search query
            if (matchFound != null && matchFound) {
                //add to temp data set
                tmpDataSet.add(siteData)
            }//end if

        }//end loop

        // Log.e("LO",tmpDataSet.toString())

        //lets now set the search data
        this.recyclerView?.setAdapter(SitesListAdapter(tmpDataSet))

        return true
    }//end filter


    //fetch sites
    fun fetchSitesListFromConsole() {

        try {

            //init webmasters service
            val service: Webmasters = WMTools().apiService(this)

            //prepare the request
            val request = service.sites().list()


            //get sites in background mode
            doAsync {

                //fetch the sites requests in background
                val siteListResponse: Deferred<SitesListResponse> = bg {
                    request.execute()
                }


                //run blocking
                runBlocking {

                    //proccess the list
                    processSiteList(siteListResponse.await())

                }//end
                //}//end if

            }//end async

        } catch (e: Exception) {

            //hide
            hideResfreshSpinner()

            //Log.e("Biggie Error", e.toString())
            e.printStackTrace()

        }//end catch error

    }//end fetch sites


    //proccess site list
    suspend fun processSiteList(sitesListResponse: SitesListResponse) {

        //get verified sites
        val ProccessedSitesData = ArrayList<MutableMap<String, String>>()

        var index = 0;

        //loop to get only verified sites
        for (CurrentSite: WmxSite in sitesListResponse.getSiteEntry()) {

            //get site info
            var SiteInfo = mutableMapOf<String, String>()
            SiteInfo.put("site_url", CurrentSite.siteUrl)
            SiteInfo.put("permission_level", CurrentSite.permissionLevel)

            //lets insert the data
            ProccessedSitesData.add(index, SiteInfo)

            //increment key
            index++

        }//end loop

        //lets now convert data to jsonString and save
        val ProccessedSitesDataStr = JSONArray(ProccessedSitesData)

        //lets now save the data to shared preference
        saveSharedPref(this, "sites_list", ProccessedSitesDataStr)

        //we need to run this on the UI thread
        //else the listview wont work
        runOnUiThread {
            //lets renderSitesListView
            renderSitesListView(ProccessedSitesData)
        }//end run on ui thread


    }//end proccess site list


    /**
     * renderSitesList
     */
    fun renderSitesListView(sitesListData: ArrayList<MutableMap<String, String>>) {

        //make the data set global
        this.sitesListData = sitesListData

        //Create adapter
        val adapter = SitesListAdapter(sitesListData)

        //set adapter
        recyclerView.setAdapter(adapter)

        //hide refrshing spinner
        hideResfreshSpinner()

        Log.d("siteList", sitesListData.toString())

    }//end render sitesList


}//end class