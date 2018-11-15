package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.digitalmid.seograph_webmasters_tool.R

class UserListAdapter(var activity: Activity, var items: ArrayList<CurrentWeek>): BaseAdapter() {

    private class ViewHolder(row: View?) {
        var txtName: TextView? = null
        var txtComment: TextView? = null
        init {
            this.txtName = row?.findViewById<TextView>(R.id.txtName)
            this.txtComment = row?.findViewById<TextView>(R.id.txtComment)

        }
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.user_list_row, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        var userDto = items[position]
        viewHolder.txtName?.text = userDto!!.clicks.toString()
        viewHolder.txtComment?.text = userDto.date.toString()
        return view as View
    }
    override fun getItem(i: Int): CurrentWeek {
        return items[i]
    }
    override fun getItemId(i: Int): Long {
        return i.toLong()
    }
    override fun getCount(): Int {
        return items.size
    }
}