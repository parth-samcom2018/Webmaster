package com.digitalmid.seograph_webmasters_tool


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDialog
import android.support.v7.widget.*
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.api.services.webmasters.model.UrlCrawlErrorsCountsQueryResponse
import kotlinx.android.synthetic.main.fragment_site_errors.*
import org.json.JSONObject
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class SiteErrorsFragment : Fragment() {

    private lateinit var mInterstitialAd: InterstitialAd

    var adView: AdView? = null

    var counter: Int = 0

    private var mChart: LineChart? = null
    //toggle grid
    val lineChartToggleGrid: SwitchCompat by lazy {
        fragView.findViewById<SwitchCompat>(R.id.linechart_toggle_grid_error)
    }

    ///siteerror filter data
    var queryOptions: JSONObject = JSONObject()

    lateinit var fragView: View

    lateinit var siteUrl: String

    val TAG: String = "SiteErrorsFragment"


    lateinit var mActivity: Activity

    lateinit var mContext: Context

    //toggle group dialog
    val groupErrorsDialog: AppCompatDialog  by lazy {
        proccessGroupErrors()
    }//end

    var rcViewForErrors: RecyclerView? = null
    var adapterErrors: ErrorsAdapter? = null

    //analytics grouping
    //add default
    //var queryFilter = mutableListOf("notFound")
    var queryFilter = mutableListOf("notFound")

    //loading indicator
    val loadingIndicator: ProgressBar by lazy {
        fragView.findViewById<ProgressBar>(R.id.loading_indicator)
    }

    //contents View
    val contentsView: LinearLayout by lazy {
        fragView.findViewById<LinearLayout>(R.id.content_errors)
    }//end content View

    //this boolean will show if linechrt has already been
    //initiated
    var lineChartExists = false

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
        fragView = inflater!!.inflate(R.layout.fragment_site_errors, container, false)
        //val textView = TextView(activity)

        fragView.findViewById<RecyclerView>(R.id.sites_list_recycler_view)

        //siteUrl
        siteUrl = (context as ViewSiteActivity).fetchSiteUrl()

        //toggle result grouping groupResultsDialog
        fragView
                .findViewById<AppCompatImageButton>(R.id.toggle_group_results_errors)
                .setOnClickListener {
                    //show dialog on click
                    groupErrorsDialog.show()
                }//end

        proccessStats(true)

        return fragView
    }

    private fun showInterstitial() {

        if (mInterstitialAd.isLoaded) {
            adView?.setVisibility(View.VISIBLE)
            mInterstitialAd.show()
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    private fun proccessErrorResults(activity: Activity, results: UrlCrawlErrorsCountsQueryResponse?) {

        // Log.e("Results",varDump(results))

        // hide bar and pie chart by default
        activity.lineChartCard_Error.visibility = View.GONE
        activity.barChartCard_siteerror.visibility = View.GONE

        loadingIndicator.visibility = View.GONE

        val emptyResponseView = fragView.findViewById<TextView>(R.id.empty_response_msg_errors);

        Log.e(TAG, results.toString())
        var numberofrows = results?.countPerTypes

        Log.e(TAG, numberofrows?.size.toString())

        Log.d(TAG, results?.countPerTypes.toString())

        if (!results!!.containsKey("countPerTypes")) {

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


            //if group by notfound
            //if group by date
            if (queryFilter.contains("notFound")) {
                //display the line graph
                //drawLineGraph(results)
                drawBarGraph(results)

            }//end if date

            if (queryFilter.contains("serverError")) {
                drawBarGraph(results)
            }

            //lets check if country or deveices grouping was enabled
            if (queryFilter.contains("roboted")) {

                drawBarGraph(results)
            }//end if


            //we have group by device
            if (queryFilter.contains("soft404")) {

                drawBarGraph(results)
            }//end if group by device

            if (queryFilter.contains("manyToOneRedirect")) {
                drawBarGraph(results)
            }

            if (queryFilter.contains("notFollowed")) {
                drawBarGraph(results)
            }

            if (queryFilter.contains("other")) {
                drawBarGraph(results)
            }
        }//end if not null

        //hide spinner and show contents
        loadingIndicator.visibility = View.GONE
        contentsView.visibility = View.VISIBLE

        /*mChart = contentsView.findViewById<LineChart>(R.id.linechartdata)
        setData(40, 60)
        mChart!!.animateX(1000)*/

        //drawLineGraph(results)

        //part 2 recyclerview for keyword list
        rcViewForErrors = contentsView.findViewById<RecyclerView>(R.id.recyclerView_errors)
        rcViewForErrors!!.layoutManager = LinearLayoutManager(this!!.getActivity(), LinearLayout.VERTICAL, false)
        //adapterErrors = ErrorsAdapter(getActivity(), results?.countPerTypes[0].getEntries())
        adapterErrors = ErrorsAdapter(getActivity(), results?.countPerTypes)

        rcViewForErrors?.adapter = adapterErrors
        adapterErrors?.notifyDataSetChanged()

        rcViewForErrors?.setOnTouchListener(object : android.view.View.OnTouchListener {
            override fun onTouch(v: android.view.View?, event: android.view.MotionEvent?): kotlin.Boolean {
                when (event?.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {

                        counter++

                        if (counter == 7) {
                            //Toast.makeText(mContext, "Touch" + counter, Toast.LENGTH_SHORT).show()
                            mInterstitialAd = com.google.android.gms.ads.InterstitialAd(mContext)

                            // set the ad unit ID
                            mInterstitialAd.adUnitId = getString(com.digitalmid.seograph_webmasters_tool.R.string.interstitial_full_screen)

                            val adRequest = com.google.android.gms.ads.AdRequest.Builder()
                                    .build()

                            // Load ads into Interstitial Ads
                            mInterstitialAd.loadAd(adRequest)

                            mInterstitialAd.adListener = object : com.google.android.gms.ads.AdListener() {
                                override fun onAdLoaded() {
                                    //showInterstitial()
                                }
                            }
                        }
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })
    }

    private fun drawLineGraph(lineChartDataSet: UrlCrawlErrorsCountsQueryResponse,
                              enableGrid: Boolean = false) {
        var lineChartView = fragView
                .findViewById<LineChart>(R.id.stats_line_chart_error)


        //lets listen to show grid toggle
        lineChartToggleGrid.setOnCheckedChangeListener { _, isChecked ->
            //redraw graph
            drawLineGraph(lineChartDataSet, isChecked)
        }//end toggle btn

        //get line chart

        //disable descriptions
        lineChartView.description.isEnabled = false
        lineChartView.axisRight.isEnabled = false
        lineChartView.setTouchEnabled(true)
        lineChartView.setDragEnabled(true)
        lineChartView.setScaleEnabled(true)
        lineChartView.setDrawGridBackground(false)


        var xAxisDateLabels = ArrayList<String>()

        //entries
        var countsGraphEntries: ArrayList<Entry> = ArrayList()
        var categoryGraphEntries: ArrayList<Entry> = ArrayList()
        var timestampGraphEntries: ArrayList<Entry> = ArrayList()

        //if chart exists, clear old data
        if (lineChartExists) {
            lineChartView.clear()
            xAxisDateLabels.clear()
            countsGraphEntries.clear()
            categoryGraphEntries.clear()
            countsGraphEntries.clear()
        }//eend if

        //lets position the x axis at the bottom
        var xAxis = lineChartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis.setDrawGridLines(enableGrid)

        //set gride is needed or not
        lineChartView.axisLeft.setDrawAxisLine(enableGrid)

        val resultsDataRows = lineChartDataSet.countPerTypes


        for ((index, rowData) in resultsDataRows.withIndex()) {
            try {
                var keys = rowData.keys

                var data = keys.elementAt(0)

                xAxisDateLabels.add(index, data)

                var countData = rowData.getEntries()[0].count.toFloat()
                var categoryData = rowData.category[0].category.value.toFloat()


                var timestampData = rowData.getEntries()[0].timestamp.toString().getDateTime()!!.toFloat()

                countsGraphEntries.add(Entry(index.toFloat(), countData))
                categoryGraphEntries.add(Entry(index.toFloat(), categoryData))
                timestampGraphEntries.add(Entry(index.toFloat(), timestampData))

                //timestampGraphEntries.add(BarEntry(index.toFloat(), timestampData))
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }

        }

        Log.e("SiteLabels", varDump(categoryGraphEntries))


        lineChartView.marker = GraphMarkerViewSite(
                ctx = mContext,
                xAxisDates = xAxisDateLabels,
                dataSet = resultsDataRows)

        //clicks data
        var countDataSet = LineDataSet(countsGraphEntries, getString(R.string.count))
        countDataSet.color = Color.rgb(123, 31, 162)

        var categoryDataSet = LineDataSet(categoryGraphEntries, getString(R.string.category))
        categoryDataSet.color = Color.rgb(21, 101, 192)

        var timeDataSet = LineDataSet(timestampGraphEntries, getString(R.string.timestamp))
        timeDataSet.color = Color.rgb(255, 109, 0)

        var lineData = LineData(countDataSet, categoryDataSet, timeDataSet)


        //Log.e("DataSet", varDump(lineData.getDataSetByIndex(0)))
        lineData.notifyDataChanged()

        //SET DATA to graph
        lineChartView.data = lineData

        xAxis.setLabelCount(5, true)
        lineChartView.notifyDataSetChanged()


        //lets set the x axis date
        xAxis.setValueFormatter({ value, axis ->

            //send the xvalue as date string
            var xIndex = value.toInt()

            var totalDates = xAxisDateLabels.size

            //since we want to show only 6 xlabels so the indexes must be limited to 6
            //for some strange reasons the indexes is based on the value
            //used in setLabelCount which is also the max in variable value
            if (xIndex >= totalDates) {
                xIndex = totalDates - 1
            }

            xAxisDateLabels[xIndex]
        })//end

        //lineChartView.isHorizontalScrollBarEnabled = true
        //lineChartView.isVerticalScrollBarEnabled = true

        //now inavlidate view
        lineChartView.invalidate()

        //update lineChartExists
        lineChartExists = true

        //show linehart view card
        lineChartCard_Error.visibility = View.VISIBLE
        lineChartView.animateX(1000)

    }

    private fun drawBarGraph(results: UrlCrawlErrorsCountsQueryResponse) {
        val barChartSiteError = fragView.findViewById<BarChart>(R.id.barchart_siteerror)

        var barChartCountDataSet = ArrayList<BarEntry>()
        var barChartCategoryDataSet = ArrayList<BarEntry>()
        var barChartTimestampsDataSet = ArrayList<BarEntry>()

        val xAxisLabels = ArrayList<String>()

        val results = results.countPerTypes


        for ((index, rowData) in results.withIndex()) {

            var keys = rowData.keys

            var data = keys.elementAt(0)

            xAxisLabels.add(index, data.toString().getDateTime()!!)

            var countData = rowData.getEntries()[0].count.toFloat()
            var category = rowData.getEntries()[0].count.toFloat()
            var timestamp = rowData.getEntries()[0].count.toFloat()

            barChartCountDataSet.add(BarEntry(index.toFloat(), countData))
            barChartCategoryDataSet.add(BarEntry(index.toFloat(), category))
            barChartTimestampsDataSet.add(BarEntry(index.toFloat(), timestamp))
        }


        Log.e("SiteErrorLabels", varDump(barChartCountDataSet))

        barChartSiteError.marker = GraphMarkerViewSite(
                ctx = mContext,
                xAxisDates = xAxisLabels,
                dataSet = results)

        //onclick data
        val bardataset = BarDataSet(barChartCountDataSet, "count")
        bardataset.color = Color.rgb(123, 31, 162)

        val barcategoryset = BarDataSet(barChartCategoryDataSet, "category")
        barcategoryset.color = Color.rgb(21, 101, 192)

        val bartimestampset = BarDataSet(barChartTimestampsDataSet, "timestamp")
        bartimestampset.color = Color.rgb(255, 109, 0)

        val data = BarData(bardataset, barcategoryset, bartimestampset)
        barChartSiteError.data = data // set the data and list of lables into chart


        var xAxis = barChartSiteError.xAxis

        var totalDates = xAxisLabels.size

        xAxis.setValueFormatter({ value, axis ->

            // Log.e("XNo",value.toString())

            //send the xvalue as date string
            var xIndex = value.toInt()

            //since we want to show only 6 xlabels so the indexes must be limited to 6
            //for some strange reasons the indexes is based on the value
            //used in setLabelCount which is also the max in variable value
            if (xIndex >= totalDates) {
                xIndex = totalDates - 1
            }

            xAxisLabels[xIndex]
        })//end


        barChartSiteError.animateY(1000)


        barChartCard_siteerror.visibility = View.VISIBLE

        barChartSiteError.setOnTouchListener(object : View.OnTouchListener {
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
        })
    }


    //proccess group stats
    fun proccessGroupErrors(): AppCompatDialog {


        //init dialog
        val dialog = AppCompatDialog(mContext)

        //set the view
        dialog.setContentView(R.layout.group_error_dialog)

        //set window width to fit
        dialog!!.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT)


        //close dialog
        dialog
                .findViewById<AppCompatButton>(R.id.close_dialog)
                ?.setOnClickListener {
                    //dismiss
                    dialog.dismiss()
                }//end close dialog

        //groups ids
        val groupsErrorId = listOf<Int>(
                R.id.tv_notFound,
                R.id.tv_error,
                R.id.tv_roboted,
                R.id.tv_soft404,
                R.id.tv_manyToOneRedirect,
                R.id.tv_notFollowed,
                R.id.tv_other
        )//end group ids

        //save grouping
        dialog
                .findViewById<AppCompatButton>(R.id.save_grouping)
                ?.setOnClickListener {

                    //clear current groupings
                    queryFilter.clear()

                    //lets get grouping
                    for (groupId in groupsErrorId) {

                        val checkbox = dialog
                                .findViewById<AppCompatCheckBox>(groupId)


                        //if checked add it
                        if (checkbox!!.isChecked) {
                            queryFilter.add(checkbox.tag.toString())
                        } //end if


                    } //end for loop

                    //dismiss
                    dialog.dismiss()

                    proccessStats(true)
                } //end save grouping onClick

        return dialog
    }//end fun


    fun proccessStats(Boolean: Boolean = false) {
        //Toast.makeText(mContext, "Data show over here", Toast.LENGTH_SHORT).show()

        //show loader
        contentsView.visibility = View.GONE

        loadingIndicator.visibility = View.VISIBLE


        val afterTask = fun(activity: Activity,
                            result: UrlCrawlErrorsCountsQueryResponse?) {

            //process results
            proccessErrorResults(activity, result)

            Log.d(TAG, result.toString())
            Log.e(TAG, result.toString())
        }//end after task


        val errorData = WMTools().fetchSiteError(mActivity,
                siteUrl,
                queryOptions,
                queryFilter,
                afterTask)

        Log.d("queryFilter", queryFilter.toString())
    }

}// Required empty public constructor

