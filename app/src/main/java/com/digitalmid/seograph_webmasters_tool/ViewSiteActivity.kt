package com.digitalmid.seograph_webmasters_tool

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.View
import java.net.URL


class ViewSiteActivity : DrawerActivity() {

    val tabLayout: TabLayout by lazy {
        findViewById<TabLayout>(R.id.tab_layout)
    }

    val viewPager: ViewPager by lazy {
        findViewById<ViewPager>(R.id.view_pager)
    }

    //site url
    lateinit var siteUrl: String

    var limit: Int? = null


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        setContentView(R.layout.activity_view_site)

        //note call only oncreate here after the setContentView
        //because the DrawerActivity dont call the setContenView method
        super.onCreate(savedInstanceState)

        //show tab, hidden by default
        tabLayout.visibility = View.VISIBLE

        //lets get site info sent from intent
        val intent = getIntent()

        siteUrl = intent.getStringExtra("site_url")

        val sitePartsInfo = URL(siteUrl)

        //lets set title
        supportActionBar?.title = sitePartsInfo.host.toUpperCase()

        //adapter
        val adapter = ViewSiteTabPagerAdaper(this, supportFragmentManager)

        //setView pager
        viewPager.adapter = adapter

        viewPager.adapter.notifyDataSetChanged()

        //set tablayout
        tabLayout.setupWithViewPager(viewPager)

        //for fragment call will active that fragment only
        val limit = if (adapter.getCount() > 1) adapter.getCount() - 1 else 1
        viewPager.setOffscreenPageLimit(limit)

    }//end onCreate


    //fetch site Url
    fun fetchSiteUrl(): String {
        return this.siteUrl
    }//end


}//end class

