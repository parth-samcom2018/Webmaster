package com.digitalmid.seograph_webmasters_tool

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.support.v4.util.PatternsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDialog
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.json.JSONObject
import java.util.*
import java.util.regex.Matcher


/**
 * Created by dr_success on 7/10/2017.
 */

//var sharedPrefConn: SharedPreferences? = null
/**
 * getSharedPref : returns the shared preference item
 * @param context  Context: the App Context
 * @param itemName String : the key of the item we wish to retrieve
 */
fun getSharedPref(context: Context, itemName: String?): Any? {

    //get the preference
    val sharedPrefConn = context.getSharedPreferences(sharedPrefDBName, Context.MODE_PRIVATE)


    //get all the items
    val dbDataSet = sharedPrefConn.all

    //if null or empty, we send all the db data
    if (itemName.isNullOrEmpty()) {
        return dbDataSet
    }//end if

    //else lets get the data
    return dbDataSet[itemName]
}//end get sharedPref

/**
 * removeSharedPrefItem
 * @param activity
 * @param key
 */
fun removeSharedPrefItem(activity: Activity, itemKey: String): Boolean {

    //lets remove user account details and logout
    val pref: SharedPreferences = activity.getSharedPreferences(
            sharedPrefDBName,
            Context.MODE_PRIVATE
    )

    //remove user data
    pref.edit().remove(itemKey).commit()

    return true
}//end fun

/**
 * saveSharedPref
 * @param context
 * @param key
 * @param value
 */
fun saveSharedPref(context: Context, key: String, value: Any) {

    //get the preference
    //if null create it
    val sharedPrefConn = context.getSharedPreferences(sharedPrefDBName, Context.MODE_PRIVATE)

    //editor
    val editor: SharedPreferences.Editor = sharedPrefConn.edit()

    //test and insert data
    when (value) {
        is Int -> editor.putInt(key, value)
        is Boolean -> editor.putBoolean(key, value)
        is String -> editor.putString(key, value)
        is Float -> editor.putFloat(key, value)
        is Long -> editor.putLong(key, value)

    }//end if

    //commit changes
    editor.commit()
}//edn save


/**
 * hideStatusBar
 * @param context
 */
fun hideStatusBar(activity: AppCompatActivity) {

    //jellybean and lower
    if (Build.VERSION.SDK_INT < 16) {
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    } else {//else if greater than android 4

        //get decor view
        val decorView: View = activity.getWindow().decorView

        //fullscreen flag
        val uiOptions: Int = View.SYSTEM_UI_FLAG_FULLSCREEN

        //set ui visibility to full screen
        decorView.setSystemUiVisibility(uiOptions)
    }//end if

}//end function

/**
 * minimizeApp
 * @param activity
 */
fun minimizeApp(activity: Activity) {
    val i = Intent(Intent.ACTION_MAIN);
    i.addCategory(Intent.CATEGORY_HOME);
    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
    activity.startActivity(i);
}//end minimize app


/*
fun calculateNoOfColumns(context: Context,colSize: Int): Int {
    val displayMetrics = context.resources.displayMetrics
    val dpWidth = displayMetrics.widthPixels / displayMetrics.density
    val noOfColumns = (dpWidth / colSize).toInt()
    return noOfColumns
}
*/

/**
 *   alertDialog
 *   @param context Context
 **/
fun progressDialog(
        activity: Activity,
        text: Any = "Loading ..",
        isCancellable: Boolean = true): AppCompatDialog {


    val dialog = AppCompatDialog(activity)

    dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

    //root view
    val rootView = activity.window.decorView.rootView as ViewGroup

    val progressLayout = activity
            .layoutInflater
            .inflate(R.layout.indeterminate_progress_bar,
                    rootView, false)

    val progressBar = progressLayout
            .findViewById<ProgressBar>(R.id.progress_bar)

    val progressBarTextView = progressLayout
            .findViewById<TextView>(R.id.progress_bar_text)

    if (text is Int) {
        progressBarTextView.text = activity.getString(text)
    } else if (text is String) {
        progressBarTextView.text = text
    }

    progressBar.isIndeterminate = true

    //progressBar.set

    dialog.setContentView(progressLayout)

    dialog.window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
    )

    dialog.setCancelable(isCancellable)

    return dialog
}//end alert Dialog


//isURLValid
fun isURLValid(url: String): Boolean {

    val pattern = PatternsCompat.WEB_URL

    val regex: Matcher = pattern.matcher(url.toLowerCase())

    return regex.matches()
}//end isURLValid


//run in bg mode
fun <T> runInBg(
        activity: Activity,
        task: () -> T,
        beforeTask: (() -> Unit)? = null,
        afterTask: ((activity: Activity, result: T?) -> Unit)? = null,
        onError: ((error: Exception) -> Unit)? = null
) {

    try {

        if (beforeTask != null) {
            beforeTask?.invoke()
        }

        async(UI) {

            //lets run in asyc
            val deferred: Deferred<T> = bg {

                //run task
                task()
            }//end bg mode

            //now lets update ui if possible
            afterTask?.invoke(activity, deferred.await())

        }//end async

    } catch (e: Exception) {

        if (onError != null) {
            //execute on error
            onError?.invoke(e)

            e.printStackTrace()

            varDump(e)
        }

    }//end error catching

}//end runInBg

//Var Dump
fun <T> varDump(obj: T): String {
    return GsonBuilder().setPrettyPrinting().create().toJson(obj);
}


//set header info
fun setDrawerHeaderInfo(context: Context, headerView: View) {
    //lets get userInfo
    val userInfo = getUserInfo(context)

    //if user info is available
    if (userInfo != null) {

        val displayName = userInfo.getString("display_name")

        val userEmail = userInfo.getString("email")


        //Log.e("USERDATA", varDump(userInfo))

        //set display Name
        headerView
                .findViewById<TextView>(R.id.display_name)
                .text = displayName

        //set email
        headerView
                .findViewById<TextView>(R.id.user_email)
                .text = userEmail


        if (userInfo.has("profile_pic_url")) {

            var profilePicUrl = userInfo.getString("profile_pic_url")

            //profile pic image view
            val profilePicImageView = headerView
                    .findViewById<ImageView>(R.id.profile_pic)

            // Log.e("ProfilePic",profilePicUrl)

            //using glide lets set the profile pic url
            Picasso
                    .with(context)
                    .load(profilePicUrl.toString())
                    .fit()
                    .placeholder(R.drawable.ic_user)
                    .into(profilePicImageView)

        }//end if profile pic is available



    }//end if user info is not empty


}//end


/**
 *startNewActivity
 **/
fun <T> startClassActivity(
        activity: Activity,
        ActivityClass: Class<T>,
        clearActivityStack: Boolean = false) {

    //leave this intent to auth intent
    val i = Intent(activity, ActivityClass)

    //if clear activity Stack is true
    if (clearActivityStack) {
        i.flags = (
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                )
    }//end if

    //start activity
    activity.startActivity(i)
}//end fun


//getDayStart - Start of the day
fun getDayStart(cal: Calendar): Calendar {

    cal.set(Calendar.HOUR, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    return cal
}//end

//getEnd Day , End of the day
fun getDayEnd(cal: Calendar): Calendar {

    cal.set(Calendar.HOUR, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MILLISECOND, 999)

    return cal
}

