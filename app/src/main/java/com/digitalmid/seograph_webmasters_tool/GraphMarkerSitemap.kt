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
import com.google.api.services.webmasters.model.WmxSitemap


class GraphMarkerSitemap(val ctx: Context,
                         val layoutRes: Int = R.layout.graph_marker_view_site_maps,
                         val xAxisDates: ArrayList<String>,
                         val dataSet: MutableList<WmxSitemap>) :

        MarkerView(ctx, layoutRes) {

    val tv_index: TextView = findViewById<TextView>(R.id.index)
    val tv_submitted: TextView = findViewById<TextView>(R.id.submitted)
    val tv_type: TextView = findViewById<TextView>(R.id.path)
    val tv_indexvalue: TextView = findViewById<TextView>(R.id.index_value)
    val tv_submittedvalue: TextView = findViewById<TextView>(R.id.submitted_value)

    var mOffset: MPPointF? = null


    /**
     * refresh Content override
     */
    override fun refreshContent(e: Entry, highlight: Highlight?) {

        try {
            var xAxisPos = e.x.toInt()

            val dataRow = dataSet[xAxisPos]

            val pathText = "Path: ${dataRow.path}"

            val indexText = "Indexed: ${dataRow.contents.get(0).indexed}"
            val submittedFloat = "Submitted: ${dataRow.contents[0].submitted}"
            val indexedValue = "Indexed1: ${dataRow.contents.get(1).indexed}"
            val submittedValue = "Submitted1: ${dataRow.contents.get(1).submitted}"

            //set date
            //date.text = dateText
            tv_index.text = indexText
            tv_submitted.text = submittedFloat
            tv_type.text = pathText
            tv_indexvalue.text = indexedValue
            tv_submittedvalue.text = submittedValue
        }

        catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

        //part 2 working

        /*if (e is CandleEntry) {

            date.setText("Date: " + Utils.formatNumber(e.getHigh(), 0, true))
            clicks.setText("Clicks: " + Utils.formatNumber(e.getHigh(), 0, true))
            ctr.setText("ctr: " + Utils.formatNumber(e.getHigh(), 0, true))
            imp.setText("Impression: " + Utils.formatNumber(e.getHigh(), 0, true))
            position.setText("Position: " + Utils.formatNumber(e.getHigh(), 0, true))
        } else {

            date.setText("Date: " + Utils.formatNumber(e.getY(), 0, true))
            clicks.setText("clicks: " + Utils.formatNumber(e.getY(), 0, true))
            ctr.setText("ctr: " + Utils.formatNumber(e.getY(), 0, true))
            imp.setText("Impression: " + Utils.formatNumber(e.getY(), 0, true))
            position.setText("Position: " + Utils.formatNumber(e.getY(), 0, true))
        }*/

        super.refreshContent(e, highlight)
    }//end

    //screen size
    fun screenSize(): Display {

        val display: Display = (ctx as Activity).windowManager.defaultDisplay
        val size: Point =  Point();
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
}