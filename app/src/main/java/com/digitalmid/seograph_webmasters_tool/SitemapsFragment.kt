package com.digitalmid.seograph_webmasters_tool


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.api.services.webmasters.model.SitemapsListResponse
import kotlinx.android.synthetic.main.fragment_sitemaps.*


/**
 * A simple [Fragment] subclass.
 */
class SitemapsFragment : Fragment() {

    private lateinit var mInterstitialAd: InterstitialAd

    var adView: AdView? = null


    val barChartToggleGrid: SwitchCompat by lazy {
        fragView.findViewById<SwitchCompat>(R.id.barchart_toggle_grid_sitemap)
    }

    lateinit var fragView: View

    lateinit var siteUrl: String

    val TAG: String = "SitemapsFragment"

    //this boolean will show if linechrt has already been
    //initiated
    var lineChartExists = false

    lateinit var mActivity: Activity

    lateinit var mContext: Context


    var rcViewForSitemap: RecyclerView? = null
    var adapterSitemap: SitemapAdapter? = null


    //loading indicator
    val loadingIndicator: ProgressBar by lazy {
        fragView.findViewById<ProgressBar>(R.id.loading_indicator)
    }


    //contents View
    val contentsView: LinearLayout by lazy {
        fragView.findViewById<LinearLayout>(R.id.content_sitemap)
    }//end content View

    //onAttach
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        mActivity = context as AppCompatActivity

    }//end

    val recyclerView: RecyclerView? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragView = inflater!!.inflate(R.layout.fragment_sitemaps, container, false)


        fragView.findViewById<RecyclerView>(R.id.sites_list_recycler_view)

        //siteUrl
        siteUrl = (context as ViewSiteActivity).fetchSiteUrl()


        //dummy()
        proccessStats(true)

        return fragView
    }

    /*private fun dummy() {
        val barChart = fragView.findViewById<BarChart>(R.id.barchart)

        val entries = java.util.ArrayList<BarEntry>()
        entries.add(BarEntry(8f, 0f))
        entries.add(BarEntry(2f, 1f))
        entries.add(BarEntry(5f, 2f))
        entries.add(BarEntry(20f, 3f))
        entries.add(BarEntry(15f, 4f))
        entries.add(BarEntry(19f, 5f))

        val bardataset = BarDataSet(entries, "Cells")

        val labels = java.util.ArrayList<String>()
        labels.add("2016")
        labels.add("2015")
        labels.add("2014")
        labels.add("2013")
        labels.add("2012")
        labels.add("2011")


        val data = BarData(bardataset)
        barChart.setData(data) // set the data and list of lables into chart

        //barChart.setDescription("Set Bar Chart Description");  // set the description

        bardataset.setColors(*ColorTemplate.COLORFUL_COLORS)

        barChart.animateY(5000)
    }*/

    private var initialLoaded = false

    fun loadIfUnloaded() {
        if (initialLoaded == false) loadData()
    }

    private fun loadData() {
        initialLoaded = true

        onResume()
    }

    private fun proccessStats(Boolean: Boolean = false) {

        //show loader
        contentsView.visibility = View.GONE

        loadingIndicator.visibility = View.VISIBLE


        val afterTask = fun(activity: Activity,
                            results: SitemapsListResponse?) {

            //process results
            proccessSiteResults(activity, results)

            Log.d(TAG, results.toString())
            Log.e(TAG, results.toString())
        }//end after task

        val errorData = WMTools().fetchSitemap(mActivity,
                siteUrl,
                afterTask)

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun proccessSiteResults(activity: Activity, results: SitemapsListResponse?) {

        // Log.e("Results",varDump(results))

        // hide bar and pie chart by default
        activity.barChartCard.visibility = View.GONE

        loadingIndicator.visibility = View.GONE

        val emptyResponseView = fragView.findViewById<TextView>(R.id.empty_response_msg_sitemap);

        Log.e(TAG, results.toString())
        var numberofrows = results?.sitemap

        Log.e(TAG, numberofrows?.size.toString())

        Log.d(TAG, results?.sitemap.toString())

        if (!results!!.containsKey("sitemap")) {

            //hide content view
            contentsView.visibility = View.GONE

            //show empty response View
            emptyResponseView.visibility = View.VISIBLE

        } else {

            //hide empty response alert
            emptyResponseView.visibility = View.GONE

            //show content view
            contentsView.visibility = View.VISIBLE
            Log.d(TAG, results.toString())

            //drawbarGraph(results)

        }//end if not null


        //hide spinner and show contents
        loadingIndicator.visibility = View.GONE
        contentsView.visibility = View.VISIBLE

        //drawbarGraphData(results)
        drawbarGraph(results)

        //part 2 recyclerview for keyword list
        rcViewForSitemap = contentsView.findViewById<RecyclerView>(R.id.recyclerView_sitemap)
        rcViewForSitemap!!.layoutManager = LinearLayoutManager(this!!.getActivity(), LinearLayout.VERTICAL, false)
        adapterSitemap = SitemapAdapter(getActivity(), results?.sitemap)

        rcViewForSitemap?.adapter = adapterSitemap
        adapterSitemap?.notifyDataSetChanged()

        /*rcViewForSitemap?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {

                        counter++

                        if (counter == 7) {
                            //Toast.makeText(mContext, "Touch" + counter, Toast.LENGTH_SHORT).show()
                            mInterstitialAd = InterstitialAd(mContext)

                            // set the ad unit ID
                            mInterstitialAd.adUnitId = getString(R.string.interstitial_full_screen)

                            val adRequest = AdRequest.Builder()
                                    .build()

                            // Load ads into Interstitial Ads
                            mInterstitialAd.loadAd(adRequest)

                            mInterstitialAd.adListener = object : AdListener() {
                                override fun onAdLoaded() {
                                    //showInterstitial()
                                }
                            }
                        }
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })*/
    }

    private fun drawbarGraph(results: SitemapsListResponse) {
        val barChart_error = fragView.findViewById<BarChart>(R.id.barchart)


        var barChartIndexDataSet = java.util.ArrayList<BarEntry>()
        var barChartSubmitDataSet = java.util.ArrayList<BarEntry>()
        var barChartIndexValueSet = java.util.ArrayList<BarEntry>()
        var barChartSubmitValueSet = java.util.ArrayList<BarEntry>()

        var xAxisLabels = java.util.ArrayList<String>()

        val results = results.sitemap

        //barChartIndexDataSet = java.util.ArrayList<BarEntry>()

        try {
            try {
                if (results.size.toString().isNotEmpty() || results.size.toString().isNullOrBlank()) {

                    barChartIndexDataSet.add(BarEntry(results.get(0).contents.get(0).indexed.toFloat(), results.get(0).contents[0].submitted.toFloat()))
                    barChartSubmitDataSet.add(BarEntry(results.get(0).contents.get(1).indexed.toFloat(), results.get(0).contents.get(1).submitted.toFloat()))
                    barChartIndexValueSet.add(BarEntry(results.get(1).contents.get(0).indexed.toFloat(), results.get(1).contents.get(0).submitted.toFloat()))
                    barChartSubmitValueSet.add(BarEntry(results.get(1).contents.get(1).indexed.toFloat(), results.get(1).contents.get(1).submitted.toFloat()))
                }
            } catch (e: java.lang.IndexOutOfBoundsException) {
                e.printStackTrace()
            }

            barChartIndexDataSet.add(BarEntry(results.get(0).contents.get(0).indexed.toFloat(), results.get(0).contents[0].submitted.toFloat()))
            barChartSubmitDataSet.add(BarEntry(results.get(0).contents.get(1).indexed.toFloat(), results.get(0).contents.get(1).submitted.toFloat()))
            barChartIndexValueSet.add(BarEntry(results.get(1).contents.get(0).indexed.toFloat(), results.get(1).contents.get(0).submitted.toFloat()))
            barChartSubmitValueSet.add(BarEntry(results.get(1).contents.get(1).indexed.toFloat(), results.get(1).contents.get(1).submitted.toFloat()))
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

        val bardataset = BarDataSet(barChartIndexDataSet, "Indexed")
        val barsubmitset = BarDataSet(barChartSubmitDataSet, "Submitted")
        val barindexvalueset = BarDataSet(barChartIndexValueSet, "Indexed1")
        val barsubmitValueset = BarDataSet(barChartSubmitValueSet, "Submitted1")

        val data = BarData(bardataset, barsubmitset, barindexvalueset, barsubmitValueset)
        barChart_error.setData(data) // set the data and list of lables into chart

        //barChart.setDescription("Set Bar Chart Description");  // set the description

        bardataset.setColors(*ColorTemplate.COLORFUL_COLORS)

        barChart_error.marker = GraphMarkerSitemap(
                ctx = mContext,
                xAxisDates = xAxisLabels,
                dataSet = results)

        Log.e("SitemapLabels", varDump(xAxisLabels))


        barChart_error.animateY(1000)

        barChartCard.visibility = View.VISIBLE
    }

    private fun showInterstitial() {

        if (mInterstitialAd.isLoaded) {
            adView?.setVisibility(View.VISIBLE)
            mInterstitialAd.show()
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    private fun drawbarGraphData(results: SitemapsListResponse) {
        val barChart = fragView.findViewById<BarChart>(R.id.barchart)


        var barChartIndexDataSet = ArrayList<BarEntry>()
        var barChartSubmitSet = ArrayList<BarEntry>()

        var barChartIndexValuesSet = ArrayList<BarEntry>()
        var barChartSubmitValueSet = ArrayList<BarEntry>()

        val xAxisLabels = ArrayList<String>()

        val results = results.sitemap

        for ((index, rowData) in results.withIndex()) {

            /*if (!results.isEmpty()) {

                var keys = rowData.keys

                var data = keys.elementAt(0)

                xAxisLabels.add(index, data)

                var indexedData = rowData.contents.get(0).indexed.toFloat()
                var submittedData = rowData.contents.get(0).submitted.toFloat()

                barChartIndexDataSet.add(BarEntry(index.toFloat(), indexedData))
                barChartSubmitSet.add(BarEntry(index.toFloat(), submittedData))
            }

            if (!results.isEmpty()) {
                var keys = rowData.keys

                var data = keys.elementAt(0)

                xAxisLabels.add(index, data)

                var indexedValue = rowData.contents.get(1).indexed.toFloat()
                var submittedValue = rowData.contents.get(1).submitted.toFloat()


                barChartIndexDataSet.add(BarEntry(index.toFloat(), indexedValue))
                barChartSubmitSet.add(BarEntry(index.toFloat(), submittedValue))
            }*/

            var keys = rowData.path.isNotEmpty()

            var data = keys.toString().elementAt(0)

            xAxisLabels.add(index, data.toString())


            var indexedData = rowData.contents.get(0).indexed.toFloat()
            var submittedData = rowData.contents.get(0).submitted.toFloat()

            barChartIndexDataSet.add(BarEntry(index.toFloat(), indexedData))
            barChartSubmitSet.add(BarEntry(index.toFloat(), submittedData))
        }


        Log.e("SitemapLabels", varDump(xAxisLabels))

        val barIndexDataset = BarDataSet(barChartIndexDataSet, "Indexed")
        barIndexDataset.setColors(*ColorTemplate.COLORFUL_COLORS)

        val barSubmittedset = BarDataSet(barChartSubmitSet, "Submitted")
        barSubmittedset.setColors(*ColorTemplate.COLORFUL_COLORS)

        val barIndexValueset = BarDataSet(barChartIndexValuesSet, "Indexed1")
        barIndexValueset.setColors(*ColorTemplate.COLORFUL_COLORS)

        val barSubmittedValueset = BarDataSet(barChartSubmitValueSet, "Submitted1")
        barSubmittedValueset.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = BarData(barIndexDataset/*, barSubmittedset, barIndexValueset, barSubmittedValueset*/)
        barChart.data = data // set the data and list of lables into chart

        //barChart.setDescription("Set Bar Chart Description");  // set the description


        barChart.marker = GraphMarkerSitemap(
                ctx = mContext,
                xAxisDates = xAxisLabels,
                dataSet = results)

        barChart.animateY(1000)

        barChartCard.visibility = View.VISIBLE

        /*barChart.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {

                        counter++

                        if (counter == 7) {
                            //Toast.makeText(mContext, "Touch" + counter, Toast.LENGTH_SHORT).show()
                            mInterstitialAd = InterstitialAd(mContext)

                            // set the ad unit ID
                            mInterstitialAd.adUnitId = getString(R.string.interstitial_full_screen)

                            val adRequest = AdRequest.Builder()
                                    .build()

                            // Load ads into Interstitial Ads
                            mInterstitialAd.loadAd(adRequest)

                            mInterstitialAd.adListener = object : AdListener() {
                                override fun onAdLoaded() {
                                    showInterstitial()
                                }
                            }
                        }
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })*/
    }


}
