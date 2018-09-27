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
import com.google.api.services.webmasters.model.ApiDataRow


class GraphMarkerViewBar(val ctx: Context,
                         val layoutRes: Int = R.layout.graph_marker_view,
                         val xAxisDates: ArrayList<String>,
                         val dataSet: MutableList<ApiDataRow>) :

        MarkerView(ctx, layoutRes) {

    val date: TextView = findViewById<TextView>(R.id.date)
    val clicks: TextView = findViewById<TextView>(R.id.clicks)
    val ctr: TextView = findViewById<TextView>(R.id.ctr)
    val imp: TextView = findViewById<TextView>(R.id.imp)
    val position: TextView = findViewById<TextView>(R.id.position)

    var mOffset:MPPointF? = null


    /**
     * refresh Content override
     */
    override fun refreshContent(e: Entry, highlight: Highlight?) {

        var xAxisPos = e.x.toInt()

        val dataRow = dataSet[xAxisPos]

        //val dateText = "Date: ${xAxisDates[xAxisPos]}"
        val clickText = "Clicks: ${dataRow.clicks}"
        val ctrFloat = "%.002f".format(dataRow.ctr.toFloat())
        val ctrText = "CTR: $ctrFloat"
        val impText = "IMP: ${dataRow.impressions}"
        val positionFloat = "%.002f".format(dataRow.position.toFloat())
        val positionText = "Position: $positionFloat"

        //set date
        //date.text = dateText
        clicks.text = clickText
        ctr.text = ctrText
        imp.text = impText
        position.text = positionText


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