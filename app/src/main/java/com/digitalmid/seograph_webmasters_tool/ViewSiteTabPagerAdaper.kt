package com.digitalmid.seograph_webmasters_tool

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by dr_success on 8/15/2017.
 */
class ViewSiteTabPagerAdaper(
        mcontext: Context,
        fm: FragmentManager
) : FragmentPagerAdapter(fm) {

    //pages title list
    //arrangement order is very important
    val pagesTitleList = listOf<String>(
            mcontext.getString(R.string.analytics),
            mcontext.getString(R.string.site_errors),
            mcontext.getString(R.string.comparison_stats),
            mcontext.getString(R.string.sitemaps)
    )


    /**
     * getCount
     */
    override fun getCount(): Int {
        return pagesTitleList.size
    }//end fun


    /**
     * getItem
     */
    override fun getItem(position: Int): Fragment {

        //val pageTitle = pagesTitleList[position]

        val pageFragment = when (position) {
            0 -> AnalyticsFragment()
            1 -> SiteErrorsFragment()
            2 -> ComparisonFragment()
            3 -> SitemapsFragment()

            else -> AnalyticsFragment()
        }

        return pageFragment
    }//end getItem


    /**
     * getPageTitle - returning the page title as
     * tab title or name
     */
    override fun getPageTitle(position: Int): CharSequence {
        return pagesTitleList[position]
    }//end fun


}//end class