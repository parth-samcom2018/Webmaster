package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.digitalmid.seograph_webmasters_tool.DataResponse
import com.digitalmid.seograph_webmasters_tool.R

class UserListAdapter(var activity: Activity, var items: ArrayList<UserData>): BaseAdapter() {

    private class ViewHolder(row: View?) {
        var txtName: TextView? = null
        var txtComment: TextView? = null
        var txtComment1: TextView? = null
        var txtChange: TextView? = null
        init {
            this.txtName = row?.findViewById<TextView>(R.id.txtName)
            this.txtComment = row?.findViewById<TextView>(R.id.txtComment)
            this.txtComment1 = row?.findViewById<TextView>(R.id.txtComment1)
            this.txtChange = row?.findViewById<TextView>(R.id.txtChange)

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
        viewHolder.txtName?.text = userDto!!.name
        viewHolder.txtComment?.text = userDto!!.comment
        viewHolder.txtComment1?.text = userDto!!.comment
        viewHolder.txtChange?.text = userDto!!.change
        return view as View
    }
    override fun getItem(i: Int): UserData {
        return items[i]
    }
    override fun getItemId(i: Int): Long {
        return i.toLong()
    }
    override fun getCount(): Int {
        return items.size
    }
}