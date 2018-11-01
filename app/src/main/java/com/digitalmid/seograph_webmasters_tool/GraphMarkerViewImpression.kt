package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.Display
import android.widget.TextView
import com.digitalmid.seograph_webmasters_tool.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.google.api.services.webmasters.model.ApiDataRow
import java.util.*

class GraphMarkerViewImpression(val ctx: Context,
                                val layoutRes: Int = R.layout.graph_marker_view_impression,
                                val xAxisDates: ArrayList<String>,
                                val dataSet: MutableList<ApiDataRow>,
                                val dataSet1: MutableList<ApiDataRow>) :

        MarkerView(ctx, layoutRes) {

    val date: TextView = findViewById<TextView>(R.id.date_clicks)
    val clicks: TextView = findViewById<TextView>(R.id.clicks)
    val impression: TextView = findViewById<TextView>(R.id.tv_impression)
    val impressionOld: TextView = findViewById<TextView>(R.id.tv_impressionOld)

    var mOffset: MPPointF? = null


    /**
     * refresh Content override
     */
    override fun refreshContent(e: Entry, highlight: Highlight?) {

        try {
            var xAxisPos = e.x.toInt()

            val dataRow = dataSet[xAxisPos]
            val dataRow1 = dataSet1[xAxisPos]

            val dateText = "Date: ${xAxisDates[xAxisPos]}"

            val clickText = "Clicks: ${dataRow.clicks}"
            val impressionText = "Impression: ${dataRow.impressions}"
            val impressionOldText = "ImpressionPrevious: ${dataRow1.impressions}"


            //set date
            date.text = dateText
            clicks.text = clickText
            impression.text = impressionText
            impressionOld.text = impressionOldText

            super.refreshContent(e, highlight)
        } catch (e: Exception) {
            e.printStackTrace()
        }

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