package com.digitalmid.seograph_webmasters_tool

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.find
import java.net.URL
import java.util.*
import kotlin.concurrent.timerTask

/**
 * Created by dr_success on 7/23/2017.
 */
open class SitesListAdapter(
        var sitesListData: ArrayList<MutableMap<String, String>>
) : RecyclerView.Adapter<SitesListAdapter.ViewHolder>() {

    var context: Context? = null

    //items Pending Removal
    var itemsPendingRemoval: ArrayList<MutableMap<String, String>> = ArrayList()

    //pending removal timeout
    val pendingRemovalTimeout = 5000

    //sites to delete timer jobs
    var itemsToDeleteTimerJobs:
            MutableMap<String, Timer> = mutableMapOf<String, Timer>()

    /**
     * SitesLisrAdapter.ViewHolder inner class
     */
    open class ViewHolder(val layout: View) : RecyclerView.ViewHolder(layout) {

        //for regular list row layout
        //this is what all the list view will
        //use
        var normalRowLayout: LinearLayout

        var siteHostTextView: TextView
        var protocolImageView: ImageView
        var protocolText: TextView
        var permissionLevelTextView: TextView
        var permissionLevelImageView: ImageView


        /**swiped View Layout,
        after the list item is swipped,
        this is the layout to be used
         **/
        var swipeRowLayout: RelativeLayout

        var undoSiteDelete: TextView

        /**
         * constructor
         */
        init {

            //swiped view layout
            this.swipeRowLayout = layout
                    .findViewById(R.id.swipe_row_layout)

            //undo site delete
            this.undoSiteDelete = layout
                    .findViewById(R.id.undo_delete)

            //regular sitelist row layout
            this.normalRowLayout = layout
                    .findViewById(R.id.normal_row_layout)

            //get views from layout
            this.siteHostTextView = this.layout.find(R.id.site_host)

            //sitePreview
            this.protocolImageView = this.layout.find(R.id.protocol_icon)

            this.protocolText = this.layout.find(R.id.protocol_text)

            this.permissionLevelTextView = this.layout.find(R.id.permission_level_text)

            this.permissionLevelImageView = this.layout.find(R.id.permission_level_icon)
        }//end constructor

    }//end inner class


    /**
     * onCreateViewHolder
     * @param parent
     */
    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): SitesListAdapter.ViewHolder {

        this.context = parent.context

        //inflate view
        val layout: View = LayoutInflater
                .from(parent.context)
                .inflate(
                        R.layout.sites_list_row_layout,
                        parent,
                        false
                )
        val layoutView: ViewHolder = ViewHolder(layout)

        return layoutView
    }//end onCreateViewHolder

    /**
     * pendingRemoval
     */
    fun itemPendingRemoval(position: Int) {

        val siteData = this.sitesListData[position]

        //skip if item is pending deletion
        if (itemsPendingRemoval.contains(siteData)) {
            return
        }//end if

        //lets add item to pending list
        itemsPendingRemoval.add(siteData)

        //pending item id
        // val itemPedingRemovalId =  itemsPendingRemoval.size - 1

        //lets notify itemChange so that ,the undo will be
        //shown
        notifyItemChanged(position)

        val timer = Timer()

        //delay exec for 5secs
        timer.schedule(timerTask {

            //lets now remove data from itemPendingRemoval list
            itemsPendingRemoval.remove(siteData)

            //lets also remove from siteDataList
            sitesListData.remove(siteData)

            //notfiy item removed from view
            notifyItemRemoved(position)

            val activity = context as Activity

            val siteUrl = siteData["site_url"].toString()

            //lets delete site now
            WMTools().deleteSite(activity, siteUrl)


        }, this.pendingRemovalTimeout.toLong())

        //lets save the task so that we can cancel it on cancel
        itemsToDeleteTimerJobs.put(siteData["site_url"].toString(), timer)

    }//end item pending removal

    /**
     * onBindViewHolder
     * @param holder: view holder
     * @param position: an Integer
     */
    override fun onBindViewHolder(siteViewHolder: ViewHolder, position: Int) {

        //lets get data row
        val siteData = this.sitesListData[position]

        var siteUrl = siteData["site_url"]

        //before we move on, lets check if the item is actually
        //pending
        if (this.itemsPendingRemoval.contains(siteData)) {

            //hide the normal row layout
            siteViewHolder.normalRowLayout.visibility = View.GONE

            //show the swiped view layout
            siteViewHolder.swipeRowLayout.visibility = View.VISIBLE


            //now lets also respond to the undo text click
            siteViewHolder.undoSiteDelete.setOnClickListener({

                //run task in a coroutine else screen will crash
                launch(CommonPool) {

                    //lets cancel the timed method which wil delete
                    //the site
                    if (itemsToDeleteTimerJobs.containsKey(siteUrl)) {


                        val timer = itemsToDeleteTimerJobs[siteUrl]

                        //cancel timer
                        timer?.cancel()

                        //purge timer queue
                        timer?.purge()

                        //remove it from our queue too
                        itemsToDeleteTimerJobs.remove(siteUrl)


                    }//end if timer exists

                    //remove the item from the items Pending Removal list
                    itemsPendingRemoval.remove(siteData)

                    //lets renotify item change
                    notifyItemChanged(position)

                }//end run it in a coroutine
                //Log.d("site", "undourl : " + siteUrl)
            })//end

            //stop excution
            return
        }//end if item is pending deletion

        else {//else lets hide swipe view and show normal view

            //show the normal row layout
            siteViewHolder.normalRowLayout.visibility = View.VISIBLE

            //hide the swiped view layout
            siteViewHolder.swipeRowLayout.visibility = View.GONE

            //dont return, continue proccessing
        }//end if

        //set tag , the tag wil act as the unique site id
        siteViewHolder.normalRowLayout.tag = siteUrl

        //set on click listener
        siteViewHolder.normalRowLayout.setOnClickListener({

            //openview site Intent
            //lets open view site activity
            val i = Intent(context, ViewSiteActivity::class.java)
            i.putExtra("site_url", siteUrl)
            context?.startActivity(i)

        })//end click listener

        //permission Level
        val permissionLevel = siteData["permission_level"]

        //lets parse the siteUrl and get the host and protocol
        var parsedSiteUrl: URL = URL(siteUrl)

        //protocol
        var urlProtocol = parsedSiteUrl.protocol

        //siteHost
        var siteUrlHost = parsedSiteUrl.host.toUpperCase()

        //lets bind the data
        siteViewHolder.siteHostTextView.text = siteUrlHost

        //the protocol is already http,
        //but if https we change icon
        if (urlProtocol.toLowerCase() == "https") {

            //change view info
            changeSiteItemViewInfo(
                    siteViewHolder.protocolImageView,
                    siteViewHolder.protocolText,
                    urlProtocol.toUpperCase(),
                    R.drawable.ic_https,
                    R.color.green
            )
        }//end protocol


        //if user doesnt own site
        if (permissionLevel.equals("siteOwner", true)) {

            changeSiteItemViewInfo(
                    siteViewHolder.permissionLevelImageView,
                    siteViewHolder.permissionLevelTextView,
                    R.string.verified,
                    R.drawable.ic_site_verified,
                    R.color.green
            )

        } else {//if not site owner

            changeSiteItemViewInfo(
                    siteViewHolder.permissionLevelImageView,
                    siteViewHolder.permissionLevelTextView,
                    R.string.unverified,
                    R.drawable.ic_unverified,
                    R.color.pink
            )

        }//end if its site owner or not


    }//end onBindViewHolder

    /**
     * change view color and text
     */
    fun changeSiteItemViewInfo(
            imageView: ImageView,
            textView: TextView,
            newText: Any,
            newIcon: Int,
            iconColor: Int) {


        //smart casting
        if (newText is String) {

            //set text
            textView.text = newText
        } else if (newText is Int) {

            //get string resource
            textView.text = this.context?.getString(newText)
        }//end


        //now we will change icon and icon color
        imageView
                .setImageDrawable(
                        ContextCompat.getDrawable(
                                this.context,
                                newIcon)
                )//end set drawable

        //get color
        val color = ContextCompat.getColor(this.context, iconColor)

        //change color
        imageView
                .setColorFilter(
                        color,
                        PorterDuff.Mode.SRC_IN
                )
    }//end changeSiteItemViewIcon


    /**
     * getItemCount
     */
    override fun getItemCount(): Int {
        return this.sitesListData.size
    }///end get item count


}//end main class