package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.digitalmid.seograph_webmasters_tool.R
import com.google.api.services.webmasters.model.ApiDataRow

class PositionAdapter(val dataList: FragmentActivity, val items: MutableList<ApiDataRow>) : RecyclerView.Adapter<PositionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PositionAdapter.ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.compare_position_one, parent, false)
        return ViewHolder(v)
    }


    //this method is binding the data on the list
    override fun onBindViewHolder(holder: PositionAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position])

    }


    override fun getItemCount():
            Int {
        return items.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(user: ApiDataRow) {
            val tvPos = itemView.findViewById<TextView>(R.id.tvPos)
            val textKeys = itemView.findViewById<TextView>(R.id.tvKeys)
            val tv_change = itemView.findViewById<TextView>(R.id.tv_change)


            // show data
            tvPos.text = user.position.toString()

            try {
                textKeys?.text = "position"
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
    }


}