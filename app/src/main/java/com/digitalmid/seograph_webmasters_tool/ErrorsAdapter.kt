package com.digitalmid.seograph_webmasters_tool

import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.api.services.webmasters.model.UrlCrawlErrorCountsPerType
import java.text.SimpleDateFormat
import java.util.*

class ErrorsAdapter(val dataList: FragmentActivity?, val items: MutableList<UrlCrawlErrorCountsPerType>) : RecyclerView.Adapter<ErrorsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ErrorsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.error_one, parent, false)
        return ErrorsAdapter.ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ErrorsAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount():
            Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(user: UrlCrawlErrorCountsPerType) {

            val textTimeStamp = itemView.findViewById<TextView>(R.id.tv_date_errors)
            val textCount = itemView.findViewById<TextView>(R.id.tv_count_errors)
            val text_category = itemView.findViewById<TextView>(R.id.tv_category)

            /*textCount.text = user.count.toString()
            textTimeStamp.text = user.timestamp.toString()*/

            text_category.text = user.category.toString()
            //textTimeStamp.text = user.getEntries()[0].timestamp.toString()
            textTimeStamp.text = "" + user.getEntries()[0].timestamp.toString().getDateTime()
            textCount.text = user.getEntries()[0].count.toInt().toString()

        }
    }

}

fun String.getDateTime(): String? {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val date = Date()
    return dateFormat.format(date)
}

