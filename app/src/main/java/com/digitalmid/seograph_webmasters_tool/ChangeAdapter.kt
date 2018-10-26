package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import android.annotation.SuppressLint
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.digitalmid.seograph_webmasters_tool.R
import com.google.api.services.webmasters.model.ApiDataRow


class ChangeAdapter(val dataList: FragmentActivity, val items: MutableList<ApiDataRow>) : RecyclerView.Adapter<ChangeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChangeAdapter.ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.change_one, parent, false)
        return ViewHolder(v)
    }


    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ChangeAdapter.ViewHolder, position: Int) {

        /*val `object` = items.get(position)

        when (holder.itemViewType) {

        }*/
        holder.bindItems(items[position])
    }


    override fun getItemCount():
            Int {
        return items.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("ResourceAsColor")
        fun bindItems(user: ApiDataRow) {
            val textClicks = itemView.findViewById<TextView>(R.id.tv_change)
            val textKeys = itemView.findViewById<TextView>(R.id.tvKeys)


            // show data
            textClicks.text = user.clicks.toString()

            try {
                //textClicks.text = user.getKeys()
                textKeys?.text = user.getKeys().toString().replace("[", "").replace("]", "");
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }


        }
    }


}
