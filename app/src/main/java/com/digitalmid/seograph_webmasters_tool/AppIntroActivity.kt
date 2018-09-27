package com.digitalmid.seograph_webmasters_tool

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.Window
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask


class AppIntroActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {

        //hide title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        //call
        super.onCreate(savedInstanceState)

        //lets hide status bar
        hideStatusBar(this)

        //hide action bar
        supportActionBar?.hide();

        setSlideOverAnimation();

        val slideOneTitle = getString(R.string.intro_multi_site_title) as String
        val slideOneDes = getString(R.string.intro_multi_site_desc) as String
        val slideOneImage: Int =  R.drawable.ic_multi_sites as Int
        val slideOneBg = ContextCompat.getColor(this, R.color.intro_multi_site_bg)

        //add slide
        addSlide(AppIntroFragment.newInstance(slideOneTitle , slideOneDes,slideOneImage ,slideOneBg));


        val slideTwoTitle = getString(R.string.intro_sitemap_title) as String
         val slideTwoDes = getString(R.string.intro_sitemap_desc) as String
         val slidTwoImage: Int =  R.drawable.ic_sitemap as Int
         val slideTwoBg = ContextCompat.getColor(this, R.color.intro_sitemap_bg)

        //add slide
        addSlide(AppIntroFragment.newInstance(slideTwoTitle , slideTwoDes,slidTwoImage ,slideTwoBg));

        val slideThreeTitle = getString(R.string.intro_analytics_title) as String
        val slideThreeDes = getString(R.string.intro_analytics_desc) as String
        val slideThreeImage: Int =  R.drawable.ic_analytics as Int
        val slideThreeBg = ContextCompat.getColor(this, R.color.intro_analytics_bg)

        //add slide
        addSlide(AppIntroFragment.newInstance(slideThreeTitle , slideThreeDes,slideThreeImage ,slideThreeBg));


        setVibrate(true);
        setVibrateIntensity(30);
    }//end

    override fun onResume() {
        val introCompleted :Any? = getSharedPref(this, "intro_completed")

        if(introCompleted == true){
            closeIntro(false)
        }
        super.onResume()
    }

    /**
     * onBackPressed
     */
    override fun onBackPressed(){
        //minimize the app
        minimizeApp(this)
    }//end on back pressed


    /**
     * onSkipPressed
     * @currentFragment
     */
     override  fun  onSkipPressed(currentFargment: Fragment){
        super.onSkipPressed(currentFargment)

        //close the intro
        closeIntro()
    }//end onskipPressed


    /**
     * onDonePressed
     * @param currentFragment
     */
    override fun onDonePressed(currentFragment: Fragment){
        super.onDonePressed(currentFragment)

        //close the intro
        closeIntro()
    }//end fun

    /**
     * openLoginActivity
     */
    fun closeIntro(updateDB: Boolean = true){

        if(updateDB == true) {
            //lets update status that user has finished intro
            saveSharedPref(this, "intro_completed", true)
        }

        //open activity
        startActivity(
                intentFor<AuthActivity>()
                        .clearTop()
                        .newTask()
        )

        //finish the activity
        this.finish()
    }//end open login activity

}//end class
