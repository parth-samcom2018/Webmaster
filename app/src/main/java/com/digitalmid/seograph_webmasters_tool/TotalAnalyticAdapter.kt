package com.digitalmid.seograph_webmasters_tool

import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.api.services.webmasters.model.ApiDataRow
import java.lang.NullPointerException
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONArray




class TotalAnalyticAdapter(val dataList: FragmentActivity, val items: MutableList<ApiDataRow>) : RecyclerView.Adapter<TotalAnalyticAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TotalAnalyticAdapter.ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.total_analytics_one, parent, false)
        return ViewHolder(v)
    }


    //this method is binding the data on the list
    override fun onBindViewHolder(holder: TotalAnalyticAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }


    override fun getItemCount():
            Int {
        return items.size
    }



    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){


        fun bindItems(user: ApiDataRow){

            val textClicks = itemView.findViewById<TextView>(R.id.tvClicks)
            val textPosition = itemView.findViewById<TextView>(R.id.tvPosition)
            val textTotal_data = itemView.findViewById<TextView>(R.id.tvTotal_data)
            val textper_data = itemView.findViewById<TextView>(R.id.tvper_data)



            // show data
            textClicks.text = user.clicks.toString()
            textPosition.setText(String.format("%.2f", user.position));

            val calculate = textClicks.text.toString() + textPosition.text.toString()

            //tvTotal.setText(" " + calculate);
            textTotal_data.setText(" " + calculate);

            val per = textTotal_data.text.toString()

        }
    }

}
