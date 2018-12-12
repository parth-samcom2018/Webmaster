package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.digitalmid.seograph_webmasters_tool.R

class CompareListAdapter(val dataList: FragmentActivity, val items: ArrayList<CompareData>) : RecyclerView.Adapter<CompareListAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CompareListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.compare_solo, parent, false)
        return ViewHolder(v)
    }


    //this method is binding the data on the list
    override fun onBindViewHolder(holder: CompareListAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position])

    }


    override fun getItemCount():
            Int {
        return items.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(user: CompareData) {
            val textClicks = itemView.findViewById<TextView>(R.id.tvClicks)
            val textClicksOld = itemView.findViewById<TextView>(R.id.tv_clicks_old)
            val textKeys = itemView.findViewById<TextView>(R.id.tvKeys)
            val textChange = itemView.findViewById<TextView>(R.id.tv_change_data)

            /*textClicks.text = user.currentweek.toString()
            textKeys.text = user.lastweek.toString()
            textChange.text = user.diff.toString()*/

            // show data
            /*textClicks.text = user.lastweek.toString()
            textClicksOld.text = user.diff.toString()
            textChange.text = user.currentweek.toString()

            try {
                //textKeys?.text = user.getKeys().toString().replace("[", "").replace("]", "");
                textKeys?.text = user.lastweek.toString()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }*/
        }
    }


}
