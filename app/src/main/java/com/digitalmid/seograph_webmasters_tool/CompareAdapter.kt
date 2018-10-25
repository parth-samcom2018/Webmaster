package com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool

import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.digitalmid.seograph_webmasters_tool.R
import com.digitalmid.seograph_webmasters_tool.getDateTime
import com.google.api.services.webmasters.model.ApiDataRow


class CompareAdapter(val dataList: FragmentActivity, val items: MutableList<ApiDataRow>) : RecyclerView.Adapter<CompareAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CompareAdapter.ViewHolder {

        val v = LayoutInflater.from(parent?.context).inflate(R.layout.compare_one, parent, false)

        /*when (viewType) {

            0 -> LayoutInflater.from(parent?.context).inflate(R.layout.compare_one, parent, false)
            1 -> LayoutInflater.from(parent?.context).inflate(R.layout.compare_one_old, parent, false)
        }*/
        return ViewHolder(v)
    }


    //this method is binding the data on the list
    override fun onBindViewHolder(holder: CompareAdapter.ViewHolder, position: Int) {

        //val `object` = items.get(position)

        /*when (holder.itemViewType) {
            0 -> holder.bindItems(items[position])
            1 -> holder.bindItemsCtr(items[position])
        }*/

        holder.bindItems(items[position])


    }


    override fun getItemCount():
            Int {
        return items.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /*fun bindItemsOld(user: ApiDataRow) {
            val textClicks = itemView.findViewById<TextView>(R.id.tvClicksold)

            textClicks.text = user.clicks.toString()
        }*/

        fun bindItems(user: ApiDataRow) {
            val textClicks = itemView.findViewById<TextView>(R.id.tvClicks)
            val textKeys = itemView.findViewById<TextView>(R.id.tvKeys)
            //val textPos = itemView.findViewById<TextView>(R.id.tvPosition)

            // show data
            textClicks.text = user.clicks.toString()


            //val textctr = itemView.findViewById<TextView>(R.id.tvCtr)
            //val textImpression = itemView.findViewById<TextView>(R.id.tvImpression)

            /*textctr.setText(String.format("%.2f", user.ctr));
            textImpression.text = user.impressions.toString()
            textPos.text = user.position.toString()*/

            /*val x = user.clicks.toInt()
            val y = user.clicks.toInt()

            val sub = x - y
            textClicks.setText("$x - $y = $sub")*/
            /*val x = Int(etv.getText().toString())
            val y = Int(etv2.getText().toString())
            val sub = x - y
            result.setText("The ANS of $x - $y = $sub")*/


            try {
                textKeys?.text = user.getKeys().toString().replace("[","").replace("]","");

            } catch (e: NullPointerException) {
                e.printStackTrace()
            }

            //plus minus values
            /*val arrayData1 = ArrayList<Int>()
            val arrayData2 = ArrayList<Int>()

            for (i in arrayData1.indices) {
                val iSum = arrayData1[i] - arrayData1[i]
                textClicks.setText("" + iSum)
            }

            for (i in arrayData1.indices) {
                if (arrayData1[i] > arrayData2[i]) {
                    textClicks.setText("+")
                } else {
                    textClicks.setText("-")
                }
            }*/

        }

    }


}
