package com.digitalmid.seograph_webmasters_tool

import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.api.services.webmasters.model.ApiDataRow
import java.lang.NullPointerException

class CustomAdapter(val dataList: FragmentActivity, val items: MutableList<ApiDataRow>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomAdapter.ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.analytics_one, parent, false)
        return ViewHolder(v)
    }


    //this method is binding the data on the list
    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
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
            val textKeys = itemView.findViewById<TextView>(R.id.tvKeys)
            val tvTotal = itemView.findViewById<TextView>(R.id.tvKeys)


            //hide data
            val textctr = itemView.findViewById<TextView>(R.id.tvCtr)
            val textImpression = itemView.findViewById<TextView>(R.id.tvImpression)

            textctr.setText(String.format("%.2f", user.ctr));
            textImpression.text = user.impressions.toString()


            // show data
            textClicks.text = user.clicks.toString()
            textPosition.setText(String.format("%.2f", user.position));

            /*val calculate = textClicks.text.toString() + textClicks.text.toString()

            tvTotal.setText(" " + calculate);*/



            try {
                textKeys?.text = user.getKeys().toString().replace("[","").replace("]","");
            }
            catch (e: NullPointerException){
                e.printStackTrace()
            }
        }
    }



}
