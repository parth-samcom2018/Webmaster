package com.digitalmid.seograph_webmasters_tool


import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDialog
import android.support.v7.widget.*
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.*
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.api.services.webmasters.model.ApiDataRow
import com.google.api.services.webmasters.model.SearchAnalyticsQueryResponse
import kotlinx.android.synthetic.main.fragment_analytics.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class AnalyticsFragment : Fragment() {

    private lateinit var mInterstitialAd: InterstitialAd

    var counter: Int = 0

    var adView: AdView? = null

    // Recycler view
    var rcView: RecyclerView? = null
    var adapter1: CustomAdapter? = null

    private val TAG = "AnalyticsFragment"

    lateinit var siteUrl: String

    lateinit var mActivity: Activity

    lateinit var mContext: Context

    val filterDialog: AppCompatDialog by lazy {
        initFilterDialog()
    }

    val statsDateDialog: AppCompatDialog by lazy {
        initStatsDateDialog()
    }

    //toggle group dialog
    val groupResultsDialog: AppCompatDialog  by lazy {
        proccessGroupResults()
    }//end

    lateinit var fragView: View

    lateinit var statDateView: Button

    var dateInterval = 7

    var startDate: Calendar = Calendar.getInstance()

    var endDate: Calendar = Calendar.getInstance()

    ///analytic filter data
    var queryOptions: JSONObject = JSONObject()

    //query filter  group
    //also add default
    var queryFilter: MutableMap<String, String> = mutableMapOf()

    //analytics grouping
    //add default
    var queryGrouping = mutableListOf("date")


    //loading indicator
    val loadingIndicator: ProgressBar by lazy {
        fragView.findViewById<ProgressBar>(R.id.loading_indicator)
    }

    val waitMessage: TextView by lazy {
        fragView.findViewById<TextView>(R.id.wait_msg)
    }


    //contents View
    val contentsView: LinearLayout by lazy {
        fragView.findViewById<LinearLayout>(R.id.content)
    }//end content View


    //toggle grid
    val lineChartToggleGrid: SwitchCompat by lazy {
        fragView.findViewById<SwitchCompat>(R.id.linechart_toggle_grid)
    }

    val barChartToggleGrid: SwitchCompat by lazy {
        fragView.findViewById<SwitchCompat>(R.id.barchart_toggle_grid)
    }

    val pieChartToggleGrid: SwitchCompat by lazy {
        fragView.findViewById<SwitchCompat>(R.id.piechart_toggle_grid)
    }

    //global sitesListData data holder for filtering sake
    var sitesListData: ArrayList<MutableMap<String, String>> = ArrayList()

    //get Recycletview


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
        fragView = inflater!!.inflate(R.layout.fragment_analytics, container, false)

        fragView.findViewById<RecyclerView>(R.id.sites_list_recycler_view)
        //siteUrl
        siteUrl = (context as ViewSiteActivity).fetchSiteUrl()

        //lets get views items
        fragView
                .findViewById<AppCompatImageButton>(R.id.toggle_filter)
                .setOnClickListener {
                    //show dialog on click
                    filterDialog.show()
                }

        //toggle result grouping groupResultsDialog
        fragView
                .findViewById<AppCompatImageButton>(R.id.toggle_group_results)
                .setOnClickListener {
                    //show dialog on click
                    groupResultsDialog.show()
                }//end


        //get the stat date text view
        statDateView = fragView
                .findViewById(R.id.stat_date_view)


        //lets shift the start Date to date intervak beginning
        //that will be last 7days
        //but we want it to end yesterday , so we do it 8
        startDate.add(Calendar.DAY_OF_YEAR, -8)

        //then we make the endData ends at yesterday
        endDate.add(Calendar.DAY_OF_YEAR, -1)

        //fetch the data from last seven days
        proccessStats(dateInterval, true)

        //lets get preview date interval
        val datePreviousInterval = fragView
                .findViewById<AppCompatImageButton>(R.id.date_prev_interval)

        //set on click listener
        datePreviousInterval.setOnClickListener {
            //proccess,move date backwards,so negate date Interval
            proccessStats(-dateInterval)
        }//end

        //lets get preview date interval
        val dateNextInterval = fragView
                .findViewById<AppCompatImageButton>(R.id.date_next_interval)
        //set on Click Listener
        dateNextInterval.setOnClickListener {
            proccessStats(dateInterval)
        }//end

        //when the statDateView text is clicked
        statDateView.setOnClickListener {

            //show stats date dialog
            statsDateDialog.show()
        }//end


        return fragView
    }

    override fun onResume() {
        super.onResume()


    }


    //onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }//end on create

    /**
     * initDatePickerDialog
     */
    fun  showDatePickerDialog(inputField: TextInputEditText) {

        //lets create date
        var now = Calendar.getInstance()

        if (inputField.id == R.id.custom_start_date) {
            now = startDate ?: now
        } else {
            now = endDate ?: now
        }

        //on date set event
        val onDateSet = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            proccessOnDateSet(inputField, datePicker, year, month, day)
        }

        //Date picker
        val picker: DatePickerDialog = DatePickerDialog(
                mContext,
                R.style.ThemeOverlay_AppCompat_Dialog,
                onDateSet,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        )//end date picker
        //lets set max date to now
        picker.datePicker.maxDate = now.timeInMillis

        //show date picker
        picker.show()
    }//end fun

    /**
     * proccess date set
     */
    fun proccessOnDateSet(inputField: TextInputEditText,
                          datePicker: DatePicker,
                          year: Int,
                          month: Int,
                          dayOfMonth: Int) {


        val date = Calendar.getInstance()

        date.set(Calendar.YEAR, year)
        date.set(Calendar.MONTH, month)
        date.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        //lets format date
        val dateFmt = SimpleDateFormat("yyyy, MMM. dd")
                .format(date.time)
                .toString()

        //set text field
        inputField.setText(dateFmt)

        //if start date we will set up start date
        if (inputField.id == R.id.custom_start_date) {

            //set date to start date
            startDate = date

        } else {//if not custom startDate then its custom EndDate

            //set end date
            endDate = date

        }//end //end if


    }//end fun

    /**
     *statDateDialog
     **/
    fun initStatsDateDialog(): AppCompatDialog {

        //before anything goes on, lets save the old
        //start date and end date, so that when the action
        //is cancelled , we revert if any changes were made
        val oldStartDate = startDate
        val oldEndDate = endDate

        val activity = mContext as Activity

        val dialog = AppCompatDialog(mContext)

        val view = activity
                .layoutInflater
                .inflate(R.layout.analytics_stats_date_dialog, null)

        //set window view
        dialog.setContentView(view)

        //set window size
        //set window width to fit
        dialog.window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT)

        //lets get views ,first lets hide keyboard when
        //textInputEditText is focused
        val customStartDateInput = view
                .findViewById<TextInputEditText>(R.id.custom_start_date)

        val customEndDateInput = view
                .findViewById<TextInputEditText>(R.id.custom_end_date)

        //set input type to help diable keyboard
        customStartDateInput.inputType = InputType.TYPE_NULL
        customEndDateInput.inputType = InputType.TYPE_NULL


        //customStartDateInput parent View on click lets show date picker
        customStartDateInput
                .setOnClickListener {
                    showDatePickerDialog(customStartDateInput)
                }//end

        //on click
        customEndDateInput
                .setOnClickListener {
                    showDatePickerDialog(customEndDateInput)
                }//end

        //cancel action
        view.findViewById<AppCompatButton>(R.id.cancel_date)
                .setOnClickListener {
                    //lets revert all changes
                    startDate = oldStartDate
                    endDate = oldEndDate

                    //close dialog
                    dialog.dismiss()
                }//end on clickr

        //save date or action
        view.findViewById<AppCompatButton>(R.id.save_date)
                .setOnClickListener {

                    //before we commit, lets check if the start
                    //date is greater than the end date , if it is
                    //then we alert user to make changes ,
                    //this is most useful for custom dates
                    if (startDate!!.after(endDate)) {

                        val startDateLayout = view
                                .findViewById<TextInputLayout>(R.id.custom_start_date_layout)

                        //enable error
                        startDateLayout.isErrorEnabled = true

                        //show error
                        startDateLayout.error = mContext.getString(R.string.startDate_exceeds_endDate)

                    } else {

                        //lets call the proccessDate to commit the data
                        proccessStats(dateInterval, true)

                        //dismiss dialog
                        dialog.dismiss()

                    }//end else

                }//end save date or action


        //dates options radio group
        val radioGroup = view
                .findViewById<RadioGroup>(R.id.dates_option_radio_group)


        //lets listen to radio click events
        radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->

            //get todays date
            val initStartDate = Calendar.getInstance()
            val initEndDate = Calendar.getInstance()

            //lets get the custom date field parent
            val customDateParent = view
                    .findViewById<LinearLayout>(R.id.custom_date_parent)

            //if not custom_dates, then hide custom date field
            if (checkedId != R.id.custom_dates) {
                customDateParent.visibility = View.GONE
            }//end hide field

            //conditionally provide each radio id with it function
            when (checkedId) {

                //when today
                R.id.today -> {


                    //today's date or now
                    startDate = initStartDate

                    //same date of today or now
                    endDate = initEndDate

                    //set interval to 1 day
                    dateInterval = 1

                }//end when today

                //when yesterday
                R.id.yesterday -> {

                    //subtract 1 day, for yesterday
                    initStartDate.add(Calendar.DAY_OF_YEAR, -1)

                    //start date to start of the day
                    startDate = initStartDate

                    //end date to same as yesterday,
                    //because we need stats for yesterday only
                    endDate = initStartDate


                    //set interval to 1 day
                    dateInterval = 1

                }//end if yesterday

                //last 7 days
                R.id.last_seven_days -> {

                    //substract 8, which means last 7days ends yesterday
                    initStartDate.add(Calendar.DAY_OF_YEAR, -8)

                    //start from last 7days
                    this.startDate = initStartDate

                    //end yesterday
                    initEndDate.add(Calendar.DAY_OF_YEAR, -1)

                    //end date
                    //and end at yesterday
                    this.endDate = initEndDate

                    dateInterval = 7

                }//end if since last 3 days

                //this month
                R.id.this_month -> {

                    //set the month of the day to 1st
                    initStartDate.set(Calendar.DAY_OF_MONTH, 1)

                    //startDate
                    //start from last 7days
                    this.startDate = initStartDate

                    //end date
                    //and end at today
                    this.endDate = initEndDate

                }//end if this month

                //this month
                R.id.last_month -> {

                    //get last month
                    initStartDate.add(Calendar.MONTH, -1)

                    //preparing it to start of month
                    //every month starts with 1 unless you are from mars
                    initStartDate.set(Calendar.DAY_OF_MONTH, 1)

                    //startDate
                    //start from last 7days
                    this.startDate = initStartDate


                    //send end date to last month too
                    initEndDate.add(Calendar.MONTH, -1)

                    //end date will be end of last month
                    initEndDate.set(Calendar.DAY_OF_MONTH,
                            initEndDate.getActualMaximum(Calendar.DAY_OF_MONTH))

                    //end date
                    //and end at today
                    this.endDate = initEndDate

                    dateInterval = 30

                }//end if this month


                //last 3 months
                R.id.last_three_months -> {

                    //set startDate for last 3months
                    //we used -4 means the end date will be
                    //minus 1 so stat will end last month 31
                    initStartDate.add(Calendar.MONTH, -4)

                    //lets set the month day to first 1st of the
                    //month
                    initStartDate.set(Calendar.DAY_OF_MONTH, 1)

                    //startDate
                    this.startDate = initStartDate

                    //set stat end date date to last month
                    initEndDate.add(Calendar.MONTH, -1)

                    //set date to max day in the month
                    initEndDate.set(Calendar.DAY_OF_MONTH,
                            initEndDate.getActualMaximum(Calendar.DAY_OF_MONTH))
                    //end date
                    //and end at today or now
                    endDate = initEndDate

                    dateInterval = 30

                }//end last 3 months stats

                //if show custom dialog fields
                R.id.custom_dates -> {

                    //set it to visible
                    customDateParent.visibility = View.VISIBLE
                }//end if custom dialog fields

            }//end when or switch

        }//end event Listener

        return dialog
    }//end fun


    /**
     * proccess Date
     */
    fun proccessStats(interval: Int = dateInterval,
                      ignoreInterval: Boolean = false) {

        //show loader
        contentsView.visibility = View.GONE

        loadingIndicator.visibility = View.VISIBLE
        waitMessage.visibility = View.VISIBLE

        val now: Calendar = Calendar.getInstance()

        //we need the year to help us do good formating
        val thisYear = now.get(Calendar.YEAR).toString()

        //initialise startDate only if null
        this.startDate = this.startDate ?: Calendar.getInstance()

        //save old start date
        var oldStartDate = startDate

        //IF we shouldnt ignore the date interval,we wont
        //subtract it
        if (!ignoreInterval) {
            this.startDate?.add(Calendar.DAY_OF_YEAR, interval)
        }//end if

        if (this.startDate!!.after(now)) {
            this.startDate = oldStartDate
        }


        var startDateYear = this.startDate?.get(Calendar.YEAR)
                .toString()

        //defualt format
        var startDateFmtPattern = "MMM dd"

        //lets now get the formating
        if (startDateYear != thisYear) {
            startDateFmtPattern = "MMM dd, yy"
        }

        //get the date Text
        var startDateText = SimpleDateFormat(startDateFmtPattern)
                .format(this.startDate!!.time)
                .toString()

        //initialize endDate if null
        this.endDate = this.endDate ?: Calendar.getInstance()

        var oldEndDate = endDate

        //IF we shouldnt ignore the date interval,we wont
        //subtract it
        if (!ignoreInterval) {
            this.endDate?.add(Calendar.DAY_OF_YEAR, interval)
        }//end if

        //if end date is greater than now, we will set it to now
        //and alert user
        if (this.endDate!!.after(now)) {

            //set endDate to current date
            this.endDate = oldEndDate
        }//end if end date is greater than now


        //end date year
        var endDateYear = this.endDate?.get(Calendar.YEAR).toString()

        //lets get endYear pattern
        var endDateFmtPattern = "MMM dd"


        if (endDateYear != thisYear) {
            endDateFmtPattern = "MMM dd, yy"
        }

        var endDateText = SimpleDateFormat(endDateFmtPattern)
                .format(this.endDate?.time)
                .toString()

        //lets now format the text for the stat date
        statDateView.text = "$startDateText - $endDateText"


        val analyticDateFmt = "yyyy-MM-dd"

        val analyticStartDate = SimpleDateFormat(analyticDateFmt)
                .format(startDate!!.time)
                .toString()

        val analyticEndDate = SimpleDateFormat(analyticDateFmt)
                .format(endDate!!.time)
                .toString()

        //add start date
        queryOptions.put("startDate", analyticStartDate)
        queryOptions.put("endDate", analyticEndDate)


        //after Task
        val afterTask = fun(activity: Activity,
                            result: SearchAnalyticsQueryResponse?) {

            //process results
            proccessAnalyticsResults(activity, result)

        }//end after task


        //lets now fetch site Data
        val analyticsData = WMTools().fetchSiteAnalytics(
                mActivity,
                siteUrl,
                queryOptions,
                queryFilter,
                queryGrouping,
                afterTask
        )

    }//end proccess Date


    @SuppressLint("ClickableViewAccessibility")
            /**
             * proccess Search Results
             */
    fun proccessAnalyticsResults(activity: Activity,
                                 results: SearchAnalyticsQueryResponse?) {

        // Log.e("Results",varDump(results))

        // hide bar and pie chart by default
        activity.lineChartCard.visibility = View.GONE
        activity.pieChartCard.visibility = View.GONE
        activity.barChartCard.visibility = View.GONE
        activity.keywordbarChartCard.visibility = View.GONE

        loadingIndicator.visibility = View.GONE
        waitMessage.visibility = View.GONE

        val emptyResponseView = fragView.findViewById<TextView>(R.id.empty_response_msg);

        Log.e(TAG, results.toString())
        var numberofrows = results?.rows

        Log.e(TAG, numberofrows?.size.toString())

        Log.d(TAG, results?.rows.toString())

        if (!results!!.containsKey("rows")) {

            //hide content view
            contentsView.visibility = View.GONE

            //show empty response View
            emptyResponseView.visibility = View.VISIBLE
        } else {

            //hide empty response alert
            emptyResponseView.visibility = View.GONE
            waitMessage.visibility = View.GONE

            //show content view
            contentsView.visibility = View.VISIBLE


            var dateIndex = 0
            var keywordIndex = 0
            var countryIndex = 0
            var deviceIndex = 0


            //if group by date
            if (queryGrouping.contains("date")) {
                //Toast.makeText(getActivity(), "date" + countryIndex, Toast.LENGTH_SHORT ).show()
                //display the line graph
                drawLineGraph(results)

                //if there is date, then the indexes changes for the rest
                keywordIndex = +1
                //just do increment, dont hard code data
                countryIndex = +1

                //we are not sure country index exits so we also increment device
                deviceIndex = +1
            }//end if date

            if (queryGrouping.contains("query")) {
                //Toast.makeText(getActivity(), "query" + keywordIndex, Toast.LENGTH_SHORT ).show()
                proccessGroupDataChart(results.rows, keywordIndex, "query")
                countryIndex = +1

                deviceIndex = +1

            }

            //lets check if country or deveices grouping was enabled
            if (queryGrouping.contains("country")) {
                //Toast.makeText(getActivity(), "country" + countryIndex, Toast.LENGTH_SHORT ).show()
                //lets proccess pie chart,COUNTRY FIRST
                proccessGroupDataChart(results.rows, countryIndex, "country")

                //now country index is vailable, means we increment,
                //dont set a hardcoded value
                deviceIndex = +1
            }//end if


            //we have group by device
            if (queryGrouping.contains("device")) {
                //Toast.makeText(getActivity(), "device" + countryIndex, Toast.LENGTH_SHORT ).show()
                //if we are here then means country is empty, so we can use 1
                //as device index,
                proccessGroupDataChart(results.rows, deviceIndex, "device")
                //dummyPieChart(results)

            }//end if group by device

        }//end if not null

        //hide spinner and show contents
        loadingIndicator.visibility = View.GONE
        waitMessage.visibility = View.GONE
        contentsView.visibility = View.VISIBLE

        //part 2 recyclerview for keyword list
        rcView = contentsView.findViewById<RecyclerView>(R.id.recyclerView)
        rcView!!.layoutManager = LinearLayoutManager(this!!.getActivity(), LinearLayout.VERTICAL, false)
        //adapter1 = CustomAdapter(getActivity(), results?.rows)
        adapter1 = CustomAdapter(getActivity(), results?.rows)

        rcView?.adapter = adapter1
        adapter1?.notifyDataSetChanged()


        rcView?.setOnTouchListener(object : View.OnTouchListener {
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

    }//end fun


    private fun showInterstitial() {

        if (mInterstitialAd.isLoaded) {
            adView?.setVisibility(View.VISIBLE)
            mInterstitialAd.show()
        }


    }

    /**
     * proccess piechat
     */
    fun proccessGroupDataChart(dataSet: MutableList<ApiDataRow>,
                               groupedIndex: Int,
                               groupType: String) {

        var pieChartView = fragView
                .findViewById<PieChart>(R.id.devices_piechart)

        //using loop
        var processedData = mutableMapOf<String, Int>()

        //looping
        for ((rowIndex, rowData) in dataSet.withIndex()) {

            //lets get the keys first
            val keysData = rowData.getKeys()

            //lets get the value form the provided index
            val groupedByKey = keysData[groupedIndex].toLowerCase()

            val rowClicks = rowData.clicks.toInt()
            // val impressions = rowData.impressions

            //we dont need 0s
            if (rowClicks == 0) {
                continue
            }

            //if data exists already
            if (processedData.containsKey(groupedByKey)) {

                var oldClicks = processedData[groupedByKey]

                //disable counter and impressions
                //enable them when needed
                val newClicks = oldClicks!!.plus(rowClicks)

                //increment data
                processedData[groupedByKey] = newClicks

            } else {
                //if it doesnt exist

                //insert  data
                processedData.put(groupedByKey, rowClicks)
            }//end if it doesnt exist

        }//end for loop


        var splitLimit = 5

        if (processedData.size < splitLimit) {
            splitLimit = processedData.size
        }

        //sort and get the first 5 countries
        val sortedProccessedData = processedData.toList()
                .sortedBy { (_, value) -> value }
                .reversed()
                .subList(0, splitLimit)
                .toMap()

        //Log.e("Sorted",varDump(sortedProccessedData))

        var pieChartDataSet = ArrayList<PieEntry>()
        var barChartDataSet = ArrayList<BarEntry>()

        val xAxisLabels = ArrayList<String>()

        //entries
        /*var clicksGraphEntries: ArrayList<Entry> = ArrayList()
        var ctrGraphEntries: ArrayList<Entry> = ArrayList()
        var impGraphEntries: ArrayList<Entry> = ArrayList()
        var positionGraphEntries: ArrayList<Entry> = ArrayList()*/

        var counter: Int = 0 //


        //loopin the data lets get the data into the data set
        for ((groupName, clicks) in sortedProccessedData) {

            //lets get the cicks
            // val clicks = dataMap["clicks"]

            //skip 0 data
            if (clicks!!.toInt() <= 0) {
                continue
            }//end


            if (groupType == "device") {
                //add clicks data to graph
                pieChartDataSet.add(PieEntry(clicks.toFloat(), groupName.toUpperCase()))


                //clicksGraphEntries.add(PieEntry(clicks.toFloat(), ctr))

            } else {

                //add country data
                barChartDataSet.add(BarEntry(counter.toFloat(), clicks.toFloat()))

                //add xaxis data
                xAxisLabels.add(counter, groupName.toUpperCase())
            }//end add chartData set


            //increment counter
            counter += 1
        }//end loop


        //draw barchat
        if (groupType == "query") {
            //Toast.makeText(mContext, "Query append here", Toast.LENGTH_SHORT).show()

            val chartLabel = mContext.getString(R.string.clicks_by_keywords)

            //draw Bar Chart
            drawBarChartKeyword(barChartDataSet,
                    xAxisLabels,
                    chartLabel,
                    dataSet)


        } else if (groupType == "country") {

            val chartLabel = mContext.getString(R.string.clicks_by_countries)

            //draw Bar Chart
            drawBarChart(barChartDataSet,
                    xAxisLabels,
                    chartLabel,
                    dataSet)


        } else if (groupType == "device") {

            val chartLabel = mContext.getString(R.string.clicks_by_devices)

            //draw Pie Chart
            drawPieChart(pieChartDataSet,
                    xAxisLabels,
                    chartLabel,
                    dataSet)

        }//end if


    }//end proccess data


    @SuppressLint("ClickableViewAccessibility")
            /**
             * drawPieChart doesn't work on click data
             */
    fun drawPieChart(chartData: ArrayList<PieEntry>,
                     xAxisLabels: ArrayList<String>,
                     chartLabel: String,
                     resultsDataRows: MutableList<ApiDataRow>) {

        // Log.e("Size",xAxisLabels.size.toString())

        var pieChartView = fragView
                .findViewById<PieChart>(R.id.devices_piechart)


        //pie chart data set
        var pieDataSet = PieDataSet(chartData, chartLabel)

        //set colors on data set
        pieDataSet.colors = (ColorTemplate.createColors(ColorTemplate.MATERIAL_COLORS))


        //set the data
        var pieData = PieData(pieDataSet)


        pieChartView.data = pieData

        pieChartView.setDrawEntryLabels(false)


        //marker
        pieChartView.marker = GraphMarkerViewPie(
                ctx = mContext,
                xAxisDates = xAxisLabels,
                dataSet = resultsDataRows)

        Log.e("ChartData", varDump(chartData))

        Log.e(TAG, chartData.toString())


        //set data change notification
        //set data change notification
        pieChartView.notifyDataSetChanged()



        pieChartView.invalidate()

        pieChartCard.visibility = View.VISIBLE

        pieChartView.setOnTouchListener(object : View.OnTouchListener {
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
    }//end draw PieChart

    @SuppressLint("ClickableViewAccessibility")
    fun drawBarChartKeyword(chartData: ArrayList<BarEntry>,
                            xAxisLabels: ArrayList<String>,
                            chartLabel: String,
                            resultsDataRows: MutableList<ApiDataRow>
    ) {

        var barChartView = fragView
                .findViewById<HorizontalBarChart>(R.id.keywords_barchart)

        //Log.e("ChartData", varDump(chartData))

        //lets listen to show grid toggle
        barChartToggleGrid.setOnCheckedChangeListener { _, isChecked ->

            //redraw graph
            drawBarChartKeyword(
                    chartData,
                    xAxisLabels,
                    chartLabel,
                    resultsDataRows)

        }//end toggle btn

        //bar data set
        var barDataSet = BarDataSet(chartData, chartLabel)

        //create colors and set it
        barDataSet.colors = (ColorTemplate.createColors(ColorTemplate.MATERIAL_COLORS))

        var barData = BarData(barDataSet)

        barChartView.data = barData

        var xAxis = barChartView.xAxis

        //set grid
        //xAxis.setDrawGridLines(enableGrid)

        // xAxis.setCenterAxisLabels(true)

        //count starts from 0, so 5 is 4,
        // xAxis.setLabelCount(5,true)

        xAxis.granularity = 1f

        //lets get yaxis
        var yAxis = barChartView.axisLeft
        yAxis.isGranularityEnabled = true
        yAxis.granularity = 1f

        var totalDates = xAxisLabels.size

        xAxis.setValueFormatter({ value, axis ->

            // Log.e("XNo",value.toString())

            //send the xvalue as date string
            var xIndex = value.toInt()

            //since we want to show only 6 xlabels so the indexes must be limited to 6
            //for some strange reasons the indexes is based on the value
            //used in setLabelCount which is also the max in variable value
            if (xIndex >= totalDates) {
                xIndex = totalDates
            }

            xAxisLabels[xIndex]
        })//end

        //marker
        //barChartView.setOnClickListener(View.OnClickListener { Toast.makeText(getActivity(),"This is bar data" + barDataSet , Toast.LENGTH_LONG).show() })

        //marker
        barChartView.marker = GraphMarkerViewBar(
                ctx = mContext,
                xAxisDates = xAxisLabels,
                dataSet = resultsDataRows)


        //set data change notification
        //set data change notification
        barChartView.notifyDataSetChanged()

        barChartView.invalidate()

        keywordbarChartCard.visibility = View.VISIBLE

        barChartView.setOnTouchListener(object : View.OnTouchListener {
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
    }//end draw bar graph

    @SuppressLint("ClickableViewAccessibility")
            /**
             * drawBarChart
             */
    fun drawBarChart(chartData: ArrayList<BarEntry>,
                     xAxisLabels: ArrayList<String>,
                     chartLabel: String,
                     resultsDataRows: MutableList<ApiDataRow>
    ) {

        var barChartView = fragView
                .findViewById<HorizontalBarChart>(R.id.countries_barchart)

        //Log.e("ChartData", varDump(chartData))

        //lets listen to show grid toggle
        barChartToggleGrid.setOnCheckedChangeListener { _, isChecked ->

            //redraw graph
            drawBarChart(
                    chartData,
                    xAxisLabels,
                    chartLabel,
                    resultsDataRows)

        }//end toggle btn

        //bar data set
        var barDataSet = BarDataSet(chartData, chartLabel)

        //create colors and set it
        barDataSet.colors = (ColorTemplate.createColors(ColorTemplate.MATERIAL_COLORS))

        var barData = BarData(barDataSet)

        barChartView.data = barData

        var xAxis = barChartView.xAxis

        //set grid
        //xAxis.setDrawGridLines(enableGrid)

        // xAxis.setCenterAxisLabels(true)

        //count starts from 0, so 5 is 4,
        // xAxis.setLabelCount(5,true)

        xAxis.granularity = 1f

        //lets get yaxis
        var yAxis = barChartView.axisLeft
        yAxis.isGranularityEnabled = true
        yAxis.granularity = 1f

        var totalDates = xAxisLabels.size

        xAxis.setValueFormatter({ value, axis ->

            // Log.e("XNo",value.toString())

            //send the xvalue as date string
            var xIndex = value.toInt()

            //since we want to show only 6 xlabels so the indexes must be limited to 6
            //for some strange reasons the indexes is based on the value
            //used in setLabelCount which is also the max in variable value
            if (xIndex >= totalDates) {
                xIndex = totalDates
            }

            xAxisLabels[xIndex]
        })//end

        //marker
        //barChartView.setOnClickListener(View.OnClickListener { Toast.makeText(getActivity(),"This is bar data" + barDataSet , Toast.LENGTH_LONG).show() })

        //marker
        barChartView.marker = GraphMarkerViewBar(
                ctx = mContext,
                xAxisDates = xAxisLabels,
                dataSet = resultsDataRows)


        //set data change notification
        //set data change notification
        barChartView.notifyDataSetChanged()

        barChartView.invalidate()

        barChartCard.visibility = View.VISIBLE

        barChartView.setOnTouchListener(object : View.OnTouchListener {
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
    }//end draw bar graph


    @SuppressLint("ClickableViewAccessibility")
            /**
             * draw lineGRaph
             */
    fun drawLineGraph(
            lineChartDataSet: SearchAnalyticsQueryResponse,
            enableGrid: Boolean = false) {

        var lineChartView = fragView
                .findViewById<LineChart>(R.id.stats_line_chart)


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
        var clicksGraphEntries: ArrayList<Entry> = ArrayList()
        var ctrGraphEntries: ArrayList<Entry> = ArrayList()
        var impGraphEntries: ArrayList<Entry> = ArrayList()
        var positionGraphEntries: ArrayList<Entry> = ArrayList()

        //if chart exists, clear old data
        if (lineChartExists) {
            lineChartView.clear()
            xAxisDateLabels.clear()
            clicksGraphEntries.clear()
            ctrGraphEntries.clear()
            clicksGraphEntries.clear()
            positionGraphEntries.clear()
        }//eend if

        //lets position the x axis at the bottom
        var xAxis = lineChartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis.setDrawGridLines(enableGrid)

        //set gride is needed or not
        lineChartView.axisLeft.setDrawAxisLine(enableGrid)


        val resultsDataRows = lineChartDataSet.rows

        //loop and display chart
        for ((index, rowData) in resultsDataRows.withIndex()) {

            var keys = rowData.getKeys()

            var statDate = keys.elementAt(0)

            //lets add the date
            xAxisDateLabels.add(index, statDate)

            var clicks = rowData.clicks.toFloat()
            var ctr = rowData.ctr.toFloat()
            var impressions = rowData.impressions.toFloat()
            var position = rowData.position.toFloat()

            clicksGraphEntries.add(Entry(index.toFloat(), clicks))
            ctrGraphEntries.add(Entry(index.toFloat(), ctr))
            impGraphEntries.add(Entry(index.toFloat(), impressions))
            positionGraphEntries.add(Entry(index.toFloat(), position))


        }//endloop
        Log.e("AnalyticsLabels", varDump(clicksGraphEntries))

        //marker
        lineChartView.marker = GraphMarkerView(
                ctx = mContext,
                xAxisDates = xAxisDateLabels,
                dataSet = resultsDataRows)

        //clicks data
        var clicksDataSet = LineDataSet(clicksGraphEntries, getString(R.string.clicks))
        clicksDataSet.color = Color.rgb(123, 31, 162)

        var ctrDataSet = LineDataSet(ctrGraphEntries, getString(R.string.click_through_rate))
        ctrDataSet.color = Color.rgb(21, 101, 192)

        var impDataSet = LineDataSet(impGraphEntries, getString(R.string.impressions))
        impDataSet.color = Color.rgb(255, 109, 0)


        var positionDataSet = LineDataSet(positionGraphEntries, getString(R.string.position))
        positionDataSet.color = Color.rgb(0, 151, 167)


        var lineData = LineData(clicksDataSet, ctrDataSet, impDataSet, positionDataSet)

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
        lineChartCard.visibility = View.VISIBLE
        lineChartView.animateX(1000)

        lineChartView.setOnTouchListener(object : View.OnTouchListener {
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

    }//end fun


    /**
     * filterDialog
     */
    fun initFilterDialog(): AppCompatDialog {

        //val activity = mContext as Activity

        //init dialog
        val dialog = AppCompatDialog(mContext)

        //set the view
        dialog.setContentView(R.layout.filter_analytics_dialog)

        //set window width to fit
        dialog!!.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT)

        //lets get the country
        val countryInput = dialog!!.findViewById<TextInputEditText>(R.id.by_country)

        //disable it
        countryInput!!.inputType = InputType.TYPE_NULL


        //clear country input
        val clearCountryInputBtn = dialog
                .findViewById<AppCompatImageButton>(R.id.clear_country_input)

        //when clicked , clear the the country input field
        clearCountryInputBtn?.setOnClickListener {

            //clear country input field
            countryInput.text.clear()

            //remaove tag
            countryInput.tag = ""

            //remove flag
            countryInput.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            //hide clearCountryInputBtn
            clearCountryInputBtn.visibility = View.INVISIBLE
        }//end Onclick


        //show datepicker on click
        countryInput.setOnClickListener {
            Geo(mContext).countryPicker({//onCountry Selected
                country ->

                //set text
                countryInput.setText(country.name)

                //lets set tag to the iso code
                countryInput.tag = country.isoAlpha3

                //flag
                val flag = ContextCompat.getDrawable(mContext, country.flag)

                //set flag
                countryInput.setCompoundDrawablesWithIntrinsicBounds(flag, null, null, null)

                //set padding
                countryInput.compoundDrawablePadding = 10

                //show the btn
                clearCountryInputBtn?.visibility = View.VISIBLE
            })//end on country selected


        }//end on click


        //close dialog when ok is clicked
        dialog!!.findViewById<AppCompatButton>(R.id.close_filter_dialog)
                ?.setOnClickListener {

                    //close dialog
                    dialog.dismiss()
                }//end close dialog

        //save filter data
        dialog
                .findViewById<AppCompatButton>(R.id.save_filter_config)
                ?.setOnClickListener {

                    //clear current filters
                    queryFilter.clear()

                    //country data
                    if (!countryInput.text.toString().isNullOrEmpty()) {
                        //set country data
                        queryFilter["country"] = countryInput.tag.toString()
                    }//end if


                    //lets now get the devices
                    val devicesView = dialog.findViewById<RadioGroup>(R.id.by_device)

                    val checkedDeviceId = devicesView!!.checkedRadioButtonId

                    //if all device
                    if (checkedDeviceId != R.id.all_devices) {

                        //get selected device and add it
                        val selecteDevice = dialog?.findViewById<AppCompatRadioButton>(checkedDeviceId)?.tag

                        //add device type to filter group
                        queryFilter["device"] = selecteDevice.toString().toUpperCase()
                    }//end if all devices


                    //lets check if keyword isnt empty, then we will add that
                    val keyword = dialog
                            .findViewById<TextInputEditText>(R.id.by_keyword)
                            ?.text.toString()


                    //if we have keyword in it
                    if (!keyword.isNullOrEmpty()) {
                        //add the keyword to query options
                        //queryFilter["keyword"] = keyword
                        queryFilter["query"] = keyword
                    }//end if


                    //search type
                    val searchTypeRadioGroup = dialog
                            .findViewById<RadioGroup>(R.id.search_type)

                    val selectedSearchTypeId = searchTypeRadioGroup!!.checkedRadioButtonId

                    //selected let add to query options
                    if (selectedSearchTypeId != -1) {

                        //get sele
                        val searchType = dialog
                                .findViewById<AppCompatRadioButton>(selectedSearchTypeId)
                                ?.tag

                        //add searchType
                        //search type isnt a filter,we wont use
                        //filter group, we will use query options
                        queryOptions.put("searchType", searchType)
                    }//end if


                    //lets check if pageUri isnt empty, then we will add that
                    val pageUri = dialog
                            .findViewById<TextInputEditText>(R.id.by_page)
                            ?.text.toString()

                    //if we have keyword in it
                    if (!pageUri.isNullOrEmpty()) {
                        //add the page to query options
                        //add to filter group
                        queryFilter["page"] = pageUri
                    }//end if


                    //close dialog
                    dialog.dismiss()

                    //lets now proccess stats
                    proccessStats(0, true)
                    // Log.e("queryOptions", varDump(queryOptions))
                }//end save filter data

        //return dialog
        return dialog
    }//emd


    //proccess group stats
    fun proccessGroupResults(): AppCompatDialog {


        //init dialog
        val dialog = AppCompatDialog(mContext)

        //set the view
        dialog.setContentView(R.layout.group_analytics_dialog)

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
        val groupsId = listOf<Int>(
                R.id.date,
                R.id.keywords,
                R.id.countries,
                R.id.devices,
                R.id.pages
        )//end group ids

        //save grouping
        dialog
                .findViewById<AppCompatButton>(R.id.save_grouping)
                ?.setOnClickListener {

                    //clear current groupings
                    queryGrouping.clear()

                    //always add date
                    //queryGrouping.add("date")

                    //lets get grouping
                    for (groupId in groupsId) {

                        val checkbox = dialog
                                .findViewById<AppCompatCheckBox>(groupId)


                        //if checked add it
                        if (checkbox!!.isChecked) {
                            queryGrouping.add(checkbox.tag.toString())
                            //end if
                        }


                    } //end for loop

                    //dismiss
                    dialog.dismiss()

                    //let proccess starts
                    proccessStats(0, true)
                } //end save grouping onClick

        return dialog
    }//end fun


}//end class
