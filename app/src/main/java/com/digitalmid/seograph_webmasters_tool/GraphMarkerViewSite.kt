package com.digitalmid.seograph_webmasters_tool

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.Display
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.google.api.services.webmasters.model.UrlCrawlErrorCountsPerType

class GraphMarkerViewSite(val ctx: Context,
                          val layoutRes: Int = R.layout.graph_marker_view_site_error,
                          val xAxisDates: ArrayList<String>,
                          val dataSet: MutableList<UrlCrawlErrorCountsPerType>) :

        MarkerView(ctx, layoutRes) {

    val timeStamp: TextView = findViewById<TextView>(R.id.timestamp)
    val count: TextView = findViewById<TextView>(R.id.count)
    val category: TextView = findViewById<TextView>(R.id.category)

    var mOffset: MPPointF? = null


    /**
     * refresh Content override
     */
    override fun refreshContent(e: Entry, highlight: Highlight?) {

        var xAxisPos = e.x.toInt()

        val dataRow = dataSet[xAxisPos]

        val timeStamptext = "Date: ${dataRow.getEntries()[0].timestamp.toString().getDateTime()}"

        val countText = "Count: ${dataRow.getEntries()[0].count}"
        val categoryText = "Category: ${dataRow.category}"

        //set date
        timeStamp.text = timeStamptext
        count.text = countText
        category.text = categoryText

        super.refreshContent(e, highlight)
    }//end

    //screen size
    fun screenSize(): Display {

        val display: Display = (ctx as Activity).windowManager.defaultDisplay
        val size: Point = Point();
        display.getSize(size);

        return display
    }//end

    override fun getOffset(): MPPointF {
        //   super.getOffset()

        if (mOffset == null) {
            ///center the marker horizontally and vertically
            mOffset = MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
        }

        return mOffset!!

    }//end get the offset

}//end class