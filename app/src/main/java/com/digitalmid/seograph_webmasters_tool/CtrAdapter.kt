package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.digitalmid.seograph_webmasters_tool.R
import com.google.api.services.webmasters.model.ApiDataRow

class CtrAdapter(val dataList: FragmentActivity, val items: MutableList<ApiDataRow>) : RecyclerView.Adapter<CtrAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CtrAdapter.ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.compare_ctr_one, parent, false)
        return ViewHolder(v)
    }


    //this method is binding the data on the list
    override fun onBindViewHolder(holder: CtrAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position])

    }


    override fun getItemCount():
            Int {
        return items.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(user: ApiDataRow) {
            val tvCtr = itemView.findViewById<TextView>(R.id.tvCtr)
            val textKeys = itemView.findViewById<TextView>(R.id.tvKeys)
            val tv_change = itemView.findViewById<TextView>(R.id.tv_change)


            // show data
            tvCtr.text = user.ctr.toString()

            try {
                textKeys?.text = "CTR"
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
    }


}