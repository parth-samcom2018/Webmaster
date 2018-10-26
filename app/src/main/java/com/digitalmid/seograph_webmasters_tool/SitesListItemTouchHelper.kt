package com.digitalmid.seograph_webmasters_tool

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * Created by dr_success on 8/5/2017.
 */
class SitesListItemTouchHelper(
        val context: Context,
        val recyclerView: RecyclerView?,
        dragDir: Int = 0,
        swipeDir: Int = ItemTouchHelper.LEFT or
                ItemTouchHelper.RIGHT) :
        ItemTouchHelper.SimpleCallback(dragDir, swipeDir) {

    //swipped item background
    val background: ColorDrawable = ColorDrawable()

    //delete icon to  draw
    val deleteIcon: Drawable = ContextCompat
            .getDrawable(context, R.drawable.ic_action_delete)

    //we dont need move
    override fun onMove(
            recyclerView: RecyclerView,
            srcViewHolder: RecyclerView.ViewHolder,
            targetViewHolder: RecyclerView.ViewHolder): Boolean {

        return false
    }//end on move


    //onswiped
    override fun onSwiped(
            viewHolder: RecyclerView.ViewHolder,
            direction: Int
    ) {

        //lets get the swipping item position
        val position: Int = viewHolder.adapterPosition

        //get the adapter
        val adapter: SitesListAdapter = recyclerView?.adapter as SitesListAdapter

        //lets add  the item to pending removal list
        adapter.itemPendingRemoval(position)
    }//end get onSwiped

    /**
     * onChildDraw
     * dX /  dY	float:
     * The amount of horizontal displacement caused by user's action
     */
    override fun onChildDraw(c: Canvas,
                             recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder,
                             dX: Float,
                             dY: Float,
                             actionState: Int,
                             isCurrentlyActive: Boolean) {

        //lets get the item which is being swiped
        val itemView = viewHolder.itemView


        //get the swipped item height
        val itemHeight = itemView.height

        val itemWidth = itemView.width

        //Log.e("dX",swipedPercent.toString()+"%")

        //set the background color
        this.background
                .color = ContextCompat.getColor(context, R.color.red)

        //if dX is 0 , we set alpha to 0
        if (Math.abs(dX.toInt()) == 0) {
            this.background.alpha = 0
        }//end if

        //set the position of the background
        //equal to the  swiped item container
        this.background
                .setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                )


        //draw background
        this.background.draw(c)

        val deleteIconWidth = this.deleteIcon.intrinsicWidth

        val deleteIconHeight = this.deleteIcon.intrinsicHeight

        var deleteIconPosLeft: Int

        val deleteIconPosRight: Int

        //lets get icon position based of the swipe direction
        //if its right
        //a negative number is swiping to left side (less than 0)
        //which means we will show the icon at right
        if (dX.toInt() < 0) {

            deleteIconPosLeft = itemView.right - deleteIconWidth

            deleteIconPosRight = itemView.right

        } else {

            //if its left
            deleteIconPosLeft = 1

            deleteIconPosRight = itemView.left + deleteIconWidth
        }//end if its left or right


        val deleteIconPosTop = itemView.top + (itemHeight - deleteIconHeight) / 2

        val deleteIconPosBottom = itemView.bottom - (itemHeight - deleteIconHeight) / 2

        //position delete btn
        this.deleteIcon.setBounds(
                deleteIconPosLeft,
                deleteIconPosTop,
                deleteIconPosRight,
                deleteIconPosBottom)

        //draw icon
        this.deleteIcon.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }//end onChild Dr


    //lets get swipedirections
    override fun getSwipeDirs(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
    ): Int {

        return super.getSwipeDirs(recyclerView, viewHolder)
    }//end getSwipeDir


}//end class