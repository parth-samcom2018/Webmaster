package com.digitalmid.seograph_webmasters_tool

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val introCompleted: Any? = getSharedPref(this, "intro_completed") as Boolean?

        //if user hasnt finihsed intro
        if (introCompleted != true) {

            //start intro
            startActivity(
                    intentFor<AppIntroActivity>()
                            .clearTop()
                            .newTask()
            )

            //finish activity
            this.finish()
        }//end open intro activity


        /*/lets now check if we have user data already
        val userInfo = getUserInfo(this)

        //if we have auth data , lets silently login
        if(userInfo is JSONObject){

            //open site lites cos we have
            //we have data now
            startActivity(
                    intentFor<SitesListActivity>()
                            .clearTop()
                            .newTask()
            )

        }else {//else show login activity
    */
        //start auth activity
        startActivity(
                intentFor<AuthActivity>()
                        .clearTop()
                        .newTask()
        )//end start auth activity

        // }//end if userinfo exists or not


        //finish activity
        this.finish()
    }//end onCreate


}//end class
