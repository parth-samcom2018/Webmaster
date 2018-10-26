package com.digitalmid.seograph_webmasters_tool

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView

/**
 * Created by dr_success on 9/4/2017.
 */
class CountryPickerListAdapter(
        val mcontext: Context,
        val countryList: List<Country>
) : ArrayAdapter<Country>(
        mcontext,
        -1,
        countryList
) {


    val resourceId: Int = R.layout.country_picker_list_adapter_row

    /**
     * getView
     */
    override fun getView(position: Int,
                         convertView: View?,
                         parent: ViewGroup): View {

        val countryData = countryList[position]

        //Log.e("Country", varDump(countryData))

        var rowView = convertView

        if (convertView == null) {

            val inflater = mcontext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            rowView = inflater.inflate(resourceId, parent, false)
        }

        //country flag
        val countryFlagView = rowView!!
                .findViewById<ImageView>(R.id.country_flag)

        //set flag
        countryFlagView.setImageResource(countryData.flag)

        //country name
        val countryNameView = rowView
                .findViewById<AppCompatTextView>(R.id.country_name)

        //set we set the isocodes as tags
        //we dont wat to use position cos of undesirable behaviour
        //during filtering
        rowView.tag = position

        //set country name
        countryNameView.text = countryData.name

        return rowView
    }//end get viw

}//end class