package com.digitalmid.seograph_webmasters_tool

import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.api.services.webmasters.model.WmxSitemap

class SitemapAdapter(val dataList: FragmentActivity, val items: MutableList<WmxSitemap>) : RecyclerView.Adapter<SitemapAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SitemapAdapter.ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.sitemap_one, parent, false)
        return SitemapAdapter.ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: SitemapAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }


    override fun getItemCount():
            Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(user: WmxSitemap) {

            val textpath = itemView.findViewById<TextView>(R.id.tv_path)
            val texttype = itemView.findViewById<TextView>(R.id.tv_type)
            val textindex = itemView.findViewById<TextView>(R.id.tv_index)
            val textsubmitted = itemView.findViewById<TextView>(R.id.tv_submmitted)


            try {
                textpath.text = user.path
                texttype.text = user.contents[0].type.toString()
                textindex.text = user.contents[0].indexed.toString()
                textsubmitted.text = user.contents[0].submitted.toString()
            }
            catch (e: NullPointerException) {
                e.printStackTrace()
            }


        }
    }

}