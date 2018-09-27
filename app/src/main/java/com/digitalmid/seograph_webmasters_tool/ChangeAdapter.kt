package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import android.annotation.SuppressLint
import android.graphics.Color
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

            // show data
            //textClicks.text = user.clicks.toString()

            //val x = user.clicks.toInt() - user.clicks.toInt()
            //val y = user.position.toInt()

            //val add = x + y
            //textClicks.setText("$x - $y = $sub")

            val x = user.position.toInt() - user.clicks.toInt()

            textClicks.setText("" + "$x")

            if (x < 0) {
                textClicks.setTextColor(Color.RED)
            } else {
                textClicks.setTextColor(Color.BLUE)
            }

            /*val x = Int(etv.getText().toString())
            val y = Int(etv2.getText().toString())
            val sub = x - y
            result.setText("The ANS of $x - $y = $sub")*/
        }
    }


}
