package com.digitalmid.seograph_webmasters_tool

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.net.URL


/**
 * Created by dr_success on 7/22/2017.
 */
open class SitesListAdapter(context: Context,
                       layoutResourceId: Int,
                       sitesListData: ArrayList<MutableMap<String,String>>):
        ArrayAdapter<MutableMap<String,String>>(context, layoutResourceId, sitesListData){

        //initializer
        val mcontext: Context
        val layoutResourceId: Int
        val sitesListData: ArrayList<MutableMap<String,String>>

        //constructor
        init{
            this.mcontext = context
            this.layoutResourceId = layoutResourceId
            this.sitesListData = sitesListData
        }//end constructor


    //getView
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val listRow: View

        if(convertView == null) {

            //inflate layout
             listRow = LayoutInflater
                    .from(this.context)
                    .inflate(this.layoutResourceId, parent, false)
        }else{
             listRow = convertView
        }

        return listRow
    }//end fun

}//end class

