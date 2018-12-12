package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.digitalmid.seograph_webmasters_tool.R

class CtrAdapter(activity: FragmentActivity?, val currentWeek: List<CurrentWeek>?, val lastWeek: List<LastWeek>, val diff: List<Diff>) : RecyclerView.Adapter<CtrAdapter.ListItemViewHolder>() {

    var activity: FragmentActivity? = null
    var currentWeekList: List<CurrentWeek>? = null
    var lastWeekList: List<LastWeek>? = null
    var diffList: List<Diff>? = null
    var mContext: Context? = null
    var results: ComparisonResponse? = null


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ListItemViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.clicks_one, parent, false)
        return ListItemViewHolder(v)
    }


    //this method is binding the data on the list
    override fun onBindViewHolder(viewHolder: ListItemViewHolder, position: Int) {


        val list = currentWeekList?.get(position)
        val list1 = lastWeekList?.get(position)
        val difflist = diffList?.get(position)

        //viewHolder.clicks!!.setText(currentWeekList!!.getClicks())
        //viewHolder.itemView.findViewById<TextView>(R.id.tv_key).text = "" + currentWeek!!.get(position)

        /*if (currentWeekList != null) {
            viewHolder.bindItems(currentWeekList.getClicks()!!.get(position))
        }*/


        viewHolder.bindItems(currentWeek?.get(position))
        viewHolder.bindItemsLastWeek(lastWeek?.get(position))
        viewHolder.bindItemsDiff(diff?.get(position))
        //viewHolder.bindData(currentWeek?.get(position))

    }


    /*override fun getItemCount():
            Int {
        return items?.size!!
    }*/

    @Suppress("SENSELESS_COMPARISON")
    override fun getItemCount(): Int {
        /*return if (currentWeek == null || lastWeek == null)
            0
        else
            currentWeek.size
        lastWeek.size
        diff.size*/
        return if (currentWeek == null || lastWeek == null || diff == null) {
            0
        } else {
            currentWeek.size
            lastWeek.size
            diff.size
        }
    }


    class ListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var ctr: TextView? = null
        var diffence: TextView? = null
        var date: TextView? = null

        var keyword: TextView? = null


        @SuppressLint("ResourceAsColor")
        fun bindItems(get: CurrentWeek?) {
            ctr = itemView.findViewById<TextView>(R.id.tv_values)

            date = itemView.findViewById<TextView>(R.id.tv_key)


            // show data
            date!!.text = get!!.getDate().toString()

            ctr!!.text = get!!.getCtr().toString()

        }

        fun bindItemsLastWeek(get: LastWeek?) {
            ctr = itemView.findViewById<TextView>(R.id.tv_values1)

            date = itemView.findViewById<TextView>(R.id.tv_key1)

            // show data
            date!!.text = get!!.getDate().toString()

            ctr!!.text = get!!.getClicks().toString()

        }

        fun bindItemsDiff(get: Diff?) {
            diffence = itemView.findViewById<TextView>(R.id.tv_diff)

            if (get!!.getDifferent()!!.contains("-")) {
                diffence!!.text = get!!.getDifferent().toString()
                diffence!!.setTextColor(Color.RED);
            } else {
                diffence!!.text = get!!.getDifferent().toString()
                diffence!!.setTextColor(Color.GREEN)
            }
        }

        fun bindItemsKeyword(get: CurrentWeek?) {
            ctr = itemView.findViewById<TextView>(R.id.tv_values)

            keyword = itemView.findViewById<TextView>(R.id.tv_key)

            ctr!!.text = get!!.getClicks().toString()
            keyword!!.text = get!!.getKeyword().toString()
        }

    }


}

