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
import com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool.*
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
import kotlinx.android.synthetic.main.fragment_comparison.*
import org.json.JSONObject
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class ComparisonFragment : Fragment() {

    private lateinit var mInterstitialAd: InterstitialAd

    var counter: Int = 0
    var count: Int = 0

    var adView: AdView? = null

    // Recycler view
    var rcView: RecyclerView? = null
    var adapter1: CompareAdapter? = null
    var tv_date: TextView? = null

    private var listViewChange: ListView? = null
    private var listAdapterChange: ArrayAdapter<*>? = null
    private var changenames: List<Analytics> = Vector<Analytics>()

    private var checkBox: CheckedTextView? = null

    //second list for 7 days old
    var rcViewOld: RecyclerView? = null
    var adapterOld: CompareOldAdapter? = null
    var tv_dateOld: TextView? = null

    var results1: SearchAnalyticsQueryResponse? = null
    var results2: SearchAnalyticsQueryResponse? = null

    //change recycler
    var rcViewChange: RecyclerView? = null
    var adapterChange: ChangeAdapter? = null

    private val TAG = "ComparisonFragment"

    lateinit var siteUrl: String

    lateinit var mActivity: Activity

    lateinit var mContext: Context

    //first filter
    val filterDialog: AppCompatDialog by lazy {
        initFilterDialog()
    }

    //second filter
    /*val filterDialogOld: AppCompatDialog by lazy {
        initFilterDialog()
    }*/

    val statsDateDialog: AppCompatDialog by lazy {
        initStatsDateDialog()
    }

    /*val statsDateDialogOld: AppCompatDialog by lazy {
        initStatsDateDialogOld()
    }*/
    //toggle group dialog
    val groupResultsDialog: AppCompatDialog  by lazy {
        proccessGroupResults()
    }//end

    //second group dialog
    /*val groupResultsDialogOld: AppCompatDialog  by lazy {
        proccessGroupResults()
    }*///end

    lateinit var fragView: View

    lateinit var statDateView: Button
    lateinit var statDateViewOld: Button

    var dateInterval = 7

    var startDate: Calendar = Calendar.getInstance()
    var startDateOld: Calendar = Calendar.getInstance()

    var endDate: Calendar = Calendar.getInstance()
    var endDateOld: Calendar = Calendar.getInstance()

    ///analytic filter data
    var queryOptions: JSONObject = JSONObject()
    //comparison filter data
    var queryOptionsOld: JSONObject = JSONObject()

    //query filter  group
    //also add default
    var queryFilter: MutableMap<String, String> = mutableMapOf()

    //analytics grouping
    //add default
    var queryGrouping = mutableListOf("date")
    var queryGroups = mutableListOf("clicks")


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

    //AppCompatCheckBox for elements
    val checkbox_clicks: AppCompatCheckBox by lazy {
        fragView.findViewById<AppCompatCheckBox>(R.id.tv_clicks_compare)
    }


    val checkbox_ctr: AppCompatCheckBox by lazy {
        fragView.findViewById<AppCompatCheckBox>(R.id.tv_ctr_compare)
    }

    val checkbox_impression: AppCompatCheckBox by lazy {
        fragView.findViewById<AppCompatCheckBox>(R.id.tv_impression_compare)
    }

    val checkbox_position: AppCompatCheckBox by lazy {
        fragView.findViewById<AppCompatCheckBox>(R.id.tv_position_compare)
    }

    val elementsId = listOf<Int>(
            R.id.tv_clicks_compare,
            R.id.tv_ctr_compare,
            R.id.tv_impression_compare,
            R.id.tv_position_compare
    )//end group ids


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
        fragView = inflater!!.inflate(R.layout.fragment_comparison, container, false)

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


        //second filter for comparison
        /*fragView
                .findViewById<AppCompatImageButton>(R.id.toggle_filter_old)
                .setOnClickListener {
                    filterDialogOld.show()
                }*///end

        //toggle result grouping groupResultsDialog
        fragView
                .findViewById<AppCompatImageButton>(R.id.toggle_group_results)
                .setOnClickListener {
                    //show dialog on click
                    groupResultsDialog.show()
                }//end

        //group for second group button
        /*fragView
                .findViewById<AppCompatImageButton>(R.id.toggle_group_results_old)
                .setOnClickListener {
                    //show second date picker
                    groupResultsDialogOld.show()
                }*///end

        //get the stat date text view
        statDateView = fragView
                .findViewById(R.id.stat_date_view)

        //get the older date from top date picker
        statDateViewOld = fragView.findViewById(R.id.stat_date_view_old)


        //lets shift the start Date to date intervak beginning
        //that will be last 7days
        //but we want it to end yesterday , so we do it 8
        startDate.add(Calendar.DAY_OF_YEAR, -8)
        startDateOld.add(Calendar.DAY_OF_YEAR, -8)

        //then we make the endData ends at yesterday
        endDate.add(Calendar.DAY_OF_YEAR, -1)
        endDateOld.add(Calendar.DAY_OF_YEAR, -1)

        //fetch the data from last seven days
        proccessStats(dateInterval, true)
        proccessStatsOld(-dateInterval, false)

        //fetch the last previous from top date picker 2
        //proccessStatsOld(-dateInterval)

        //lets get preview date interval
        val datePreviousInterval = fragView
                .findViewById<AppCompatImageButton>(R.id.date_prev_interval)


        //set on click listener
        datePreviousInterval.setOnClickListener {
            //proccess,move date backwards,so negate date Interval
            proccessStats(-dateInterval)
        }//end

        //lets get preview date interval on second datepicker
        val datePreviousIntervalOld = fragView
                .findViewById<AppCompatImageButton>(R.id.date_prev_interval_old)

        //second date picker for previous dates
        datePreviousIntervalOld.setOnClickListener {
            proccessStatsOld(-dateInterval)
        }


        //lets get next date interval
        val dateNextInterval = fragView
                .findViewById<AppCompatImageButton>(R.id.date_next_interval)

        //set on Click Listener
        dateNextInterval.setOnClickListener {
            proccessStats(dateInterval)
        }//end

        //lets get next date for second date interval
        val dateNextIntervalOld = fragView.findViewById<AppCompatImageButton>(R.id.date_next_interval_old)

        dateNextIntervalOld.setOnClickListener {
            proccessStatsOld(dateInterval)
        }

        //when the statDateView text is clicked
        statDateView.setOnClickListener {

            //show stats date dialog
            statsDateDialog.show()
        }//end

        /*statDateViewOld.setOnClickListener {
            statsDateDialogOld.show()
        }*/



        return fragView
    }

    fun showDatePickerDialogOld(inputField: TextInputEditText) {
        //lets create date
        var now = Calendar.getInstance()

        if (inputField.id == R.id.custom_start_date) {
            now = startDateOld ?: now
        } else {
            now = endDateOld ?: now
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
    }


    /**
     * initDatePickerDialog
     */
    fun showDatePickerDialog(inputField: TextInputEditText) {

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

    fun initStatsDateDialogOld(): AppCompatDialog {
        //before anything goes on, lets save the old
        //start date and end date, so that when the action
        //is cancelled , we revert if any changes were made
        val oldStartDate = startDateOld
        val oldEndDate = endDateOld

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
                    showDatePickerDialogOld(customStartDateInput)
                }//end

        //on click
        customEndDateInput
                .setOnClickListener {
                    showDatePickerDialogOld(customEndDateInput)
                }//end

        //cancel action
        view.findViewById<AppCompatButton>(R.id.cancel_date)
                .setOnClickListener {
                    //lets revert all changes
                    startDateOld = oldStartDate
                    endDateOld = oldEndDate

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
                    if (startDateOld!!.after(endDateOld)) {

                        val startDateLayout = view
                                .findViewById<TextInputLayout>(R.id.custom_start_date_layout)

                        //enable error
                        startDateLayout.isErrorEnabled = true

                        //show error
                        startDateLayout.error = mContext.getString(R.string.startDate_exceeds_endDate)

                    } else {

                        //lets call the proccessDate to commit the data
                        proccessStats(dateInterval, true)
                        proccessStatsOld(dateInterval, true)

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
                    startDateOld = initStartDate

                    //same date of today or now
                    endDateOld = initEndDate

                    //set interval to 1 day
                    dateInterval = 1

                }//end when today

                //when yesterday
                R.id.yesterday -> {

                    //subtract 1 day, for yesterday
                    initStartDate.add(Calendar.DAY_OF_YEAR, -1)

                    //start date to start of the day
                    startDateOld = initStartDate

                    //end date to same as yesterday,
                    //because we need stats for yesterday only
                    endDateOld = initStartDate


                    //set interval to 1 day
                    dateInterval = 1

                }//end if yesterday

                //last 7 days
                R.id.last_seven_days -> {

                    //substract 8, which means last 7days ends yesterday
                    initStartDate.add(Calendar.DAY_OF_YEAR, -8)

                    //start from last 7days
                    this.startDateOld = initStartDate

                    //end yesterday
                    initEndDate.add(Calendar.DAY_OF_YEAR, -1)

                    //end date
                    //and end at yesterday
                    this.endDateOld = initEndDate

                    dateInterval = 7

                }//end if since last 3 days

                //this month
                R.id.this_month -> {

                    //set the month of the day to 1st
                    initStartDate.set(Calendar.DAY_OF_MONTH, 1)

                    //startDate
                    //start from last 7days
                    this.startDateOld = initStartDate

                    //end date
                    //and end at today
                    this.endDateOld = initEndDate

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
                    this.startDateOld = initStartDate


                    //send end date to last month too
                    initEndDate.add(Calendar.MONTH, -1)

                    //end date will be end of last month
                    initEndDate.set(Calendar.DAY_OF_MONTH,
                            initEndDate.getActualMaximum(Calendar.DAY_OF_MONTH))

                    //end date
                    //and end at today
                    this.endDateOld = initEndDate

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
                    this.startDateOld = initStartDate

                    //set stat end date date to last month
                    initEndDate.add(Calendar.MONTH, -1)

                    //set date to max day in the month
                    initEndDate.set(Calendar.DAY_OF_MONTH,
                            initEndDate.getActualMaximum(Calendar.DAY_OF_MONTH))
                    //end date
                    //and end at today or now
                    endDateOld = initEndDate

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
    }

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
                        proccessStatsOld(dateInterval, true)

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

    fun proccessStatsOld(interval: Int = dateInterval,
                         ignoreInterval: Boolean = false) {

        //show loader
        contentsView.visibility = View.GONE

        loadingIndicator.visibility = View.VISIBLE
        waitMessage.visibility = View.VISIBLE

        val now: Calendar = Calendar.getInstance()

        //we need the year to help us do good formating
        val thisYear = now.get(Calendar.YEAR).toString()

        //initialise startDate only if null
        this.startDateOld = this.startDateOld ?: Calendar.getInstance()

        //Toast.makeText(mContext, "" + startDateOld,Toast.LENGTH_SHORT).show()

        //save old start date
        var oldStartDate = startDateOld

        //IF we shouldnt ignore the date interval,we wont
        //subtract it
        if (!ignoreInterval) {
            this.startDateOld?.add(Calendar.DAY_OF_YEAR, interval)
        }//end if

        if (this.startDateOld!!.after(now)) {
            this.startDateOld = oldStartDate
        }


        var startDateYear = this.startDateOld?.get(Calendar.YEAR)
                .toString()

        //defualt format
        var startDateFmtPattern = "MMM dd"

        //lets now get the formating
        if (startDateYear != thisYear) {
            startDateFmtPattern = "MMM dd, yy"
        }

        //get the date Text
        var startDateText = SimpleDateFormat(startDateFmtPattern)
                .format(this.startDateOld!!.time)
                .toString()

        //initialize endDate if null
        this.endDateOld = this.endDateOld ?: Calendar.getInstance()

        var oldEndDate = endDateOld

        //IF we shouldnt ignore the date interval,we wont
        //subtract it
        if (!ignoreInterval) {
            this.endDateOld?.add(Calendar.DAY_OF_YEAR, interval)
        }//end if

        //if end date is greater than now, we will set it to now
        //and alert user
        if (this.endDateOld!!.after(now)) {

            //set endDate to current date
            this.endDateOld = oldEndDate
        }//end if end date is greater than now


        //end date year
        var endDateYear = this.endDateOld?.get(Calendar.YEAR).toString()

        //lets get endYear pattern
        var endDateFmtPattern = "MMM dd"


        if (endDateYear != thisYear) {
            endDateFmtPattern = "MMM dd, yy"
        }

        var endDateText = SimpleDateFormat(endDateFmtPattern)
                .format(this.endDateOld?.time)
                .toString()

        //lets now format the text for the stat date
        statDateViewOld.text = "$startDateText - $endDateText"


        val comparisonDateFmt = "yyyy-MM-dd"

        val comparisonStartDate = SimpleDateFormat(comparisonDateFmt)
                .format(startDateOld!!.time)
                .toString()

        val comparisonEndDate = SimpleDateFormat(comparisonDateFmt)
                .format(endDateOld!!.time)
                .toString()

        //add start date
        queryOptionsOld.put("startDate", comparisonStartDate)
        queryOptionsOld.put("endDate", comparisonEndDate)

        //Toast.makeText(mContext, "" + startDateText, Toast.LENGTH_SHORT).show()


        //after Task
        val afterTask = fun(activity: Activity,
                            result: SearchAnalyticsQueryResponse?) {

            //process results
            proccessAnalyticsResultsOld(activity, result)

        }//end after task


        //lets now fetch site Data
        val analyticsData = WMTools().fetchSiteComparisonOld(
                mActivity,
                siteUrl,
                queryOptionsOld,
                queryFilter,
                queryGrouping,
                /*queryGroups,*/
                afterTask
        )

    }//end proccess Date

    @SuppressLint("ClickableViewAccessibility")
    fun proccessAnalyticsResultsOld(activity: Activity, results: SearchAnalyticsQueryResponse?) {
        // hide bar and pie chart by default
        activity.lineChartCard_comparison.visibility = View.GONE
        activity.pieChartCard_comparison.visibility = View.GONE
        activity.barChartCard_comparison.visibility = View.GONE
        activity.keywordbarChartCard_comparison.visibility = View.GONE

        loadingIndicator.visibility = View.GONE
        waitMessage.visibility = View.GONE

        val emptyResponseView = fragView.findViewById<TextView>(R.id.empty_response_msg);


        Log.e(TAG, results.toString())
        var numberofrows = results?.rows

        Log.e(TAG, numberofrows?.size.toString())

        Log.d(TAG, results?.rows.toString())

        if (!results!!.containsKey("rows") || results!!.isEmpty() || results!!.containsKey("")) {

            //hide content view
            contentsView.visibility = View.GONE

            //show empty response View
            emptyResponseView.visibility = View.VISIBLE


        } else {
            results2 = results

            //hide empty response alert
            emptyResponseView.visibility = View.GONE

            //show content view
            contentsView.visibility = View.VISIBLE


            var dateIndex = 0
            var keywordIndex = 0
            var countryIndex = 0
            var deviceIndex = 0

            var clicksIndex = 0
            var ctrIndex = 0
            var posIndex = 0
            var imprIndex = 0



            checkbox_clicks.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked) {

                    checkbox_clicks.setChecked(true)
                    checkbox_position.setChecked(false)
                    checkbox_impression.setChecked(false)
                    checkbox_ctr.setChecked(false)
                    //on view
                    emptyResponseView.visibility = View.GONE
                    contentsView.visibility = View.VISIBLE
                    drawLineGraphclicks(results)

                    Log.d("clicks", results.toString())


                    DM().getApi().getAnalyticsClicks(startDate?.time.toString(),
                            endDate?.time.toString(),
                            siteUrl,
                            startDateOld?.time.toString(),
                            endDateOld?.time.toString(),
                            queryGrouping.toString().replace("[", "").replace("]", ""),
                            checkbox_clicks.tag.toString(),
                            queryFilter.toString(),
                            object : Callback<ChangeResponse> {
                                override fun success(t: ChangeResponse?, response: Response?) {

                                    changenames = t?.currentweek!!


                                    Log.d(TAG, "clicks: " + checkbox_clicks.tag.toString())


                                    Log.d(TAG, "size: " + changenames.size)


                                    var msg: String = ""
                                    for (item: Analytics in changenames.iterator()) {

                                        msg = msg + item.clicks + "\n"

                                    }
                                    Toast.makeText(getActivity(), "List of Category  \n  " + msg, Toast.LENGTH_LONG).show()

                                }

                                override fun failure(error: RetrofitError?) {
                                    Log.d(TAG, "clicks: " + error.toString())
                                }
                            })

                } else {
                    //Toast.makeText(mContext, "Please select any elements to view in chart", Toast.LENGTH_SHORT).show()
                    contentsView.visibility = View.GONE
                    emptyResponseView.visibility = View.VISIBLE
                }
            }

            checkbox_ctr.setOnCheckedChangeListener { compoundButton, isChecked ->

                if (isChecked) {
                    checkbox_clicks.setChecked(false)
                    checkbox_position.setChecked(false)
                    checkbox_impression.setChecked(false)
                    checkbox_ctr.setChecked(true)
                    //on view
                    emptyResponseView.visibility = View.GONE
                    contentsView.visibility = View.VISIBLE
                    drawLineGraphctr(results)

                    Log.e("ctr", results.toString())

                    /*DM().getApi().getAnalyticsClicks(startDate?.time.toString(),
                            endDate?.time.toString(),
                            siteUrl,
                            startDateOld?.time.toString(),
                            endDateOld?.time.toString(),
                            queryGrouping.toString().replace("[","").replace("]",""),
                            checkbox_ctr.tag.toString(),
                            queryFilter.toString(),
                            object : Callback<Response> {
                                override fun success(t: Response?, response: Response?) {
                                    Log.d(TAG,"ctr: " + checkbox_ctr.tag.toString())
                                }

                                override fun failure(error: RetrofitError?) {
                                    Log.d(TAG,"ctr: " + error.toString())
                                }
                            })*/
                } else {
                    //Toast.makeText(mContext, "Please select any elements to view in chart", Toast.LENGTH_SHORT).show()
                    contentsView.visibility = View.GONE
                    emptyResponseView.visibility = View.VISIBLE
                }
            }

            checkbox_position.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked) {
                    checkbox_clicks.setChecked(false)
                    checkbox_position.setChecked(true)
                    checkbox_impression.setChecked(false)
                    checkbox_ctr.setChecked(false)
                    //on view
                    emptyResponseView.visibility = View.GONE
                    contentsView.visibility = View.VISIBLE
                    drawLineGraphposition(results)

                    Log.e("position", results.toString())

                    /*DM().getApi().getAnalyticsClicks(startDate?.time.toString(),
                            endDate?.time.toString(),
                            siteUrl,
                            startDateOld?.time.toString(),
                            endDateOld?.time.toString(),
                            queryGrouping.toString().replace("[","").replace("]",""),
                            checkbox_position.tag.toString(),
                            queryFilter.toString(),
                            object : Callback<Response> {
                                override fun success(t: Response?, response: Response?) {
                                    Log.d(TAG,"position: " + checkbox_position.tag.toString())
                                }

                                override fun failure(error: RetrofitError?) {
                                    Log.d(TAG,"position: " + error.toString())
                                }
                            })*/
                } else {
                    //Toast.makeText(mContext, "Please select any elements to view in chart", Toast.LENGTH_SHORT).show()
                    contentsView.visibility = View.GONE
                    emptyResponseView.visibility = View.VISIBLE
                }
            }

            checkbox_impression.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked) {
                    checkbox_clicks.setChecked(false)
                    checkbox_position.setChecked(false)
                    checkbox_impression.setChecked(true)
                    checkbox_ctr.setChecked(false)
                    //on view
                    emptyResponseView.visibility = View.GONE
                    contentsView.visibility = View.VISIBLE
                    drawLineGraphimpr(results)

                    Log.e("impressions", results.toString())

                    /*DM().getApi().getAnalyticsClicks(startDate?.time.toString(),
                            endDate?.time.toString(),
                            siteUrl,
                            startDateOld?.time.toString(),
                            endDateOld?.time.toString(),
                            queryGrouping.toString().replace("[","").replace("]",""),
                            checkbox_impression.tag.toString(),
                            queryFilter.toString(),
                            object : Callback<Response> {
                                override fun success(t: Response?, response: Response?) {
                                    Log.d(TAG,"impression: " + checkbox_impression.tag.toString())
                                }

                                override fun failure(error: RetrofitError?) {
                                    Log.d(TAG,"impression: " + error.toString())
                                }
                            })*/
                } else {
                    //Toast.makeText(mContext, "Please select any elements to view in chart", Toast.LENGTH_SHORT).show()
                    contentsView.visibility = View.GONE
                    emptyResponseView.visibility = View.VISIBLE
                }
            }


            /*if (checkbox_clicks.isSelected || checkbox_clicks.isChecked) {
                //queryGrouping.add(checkbox_clicks.tag.toString())
                //Toast.makeText(mContext, "Selected " + results?.rows, Toast.LENGTH_SHORT).show()
                loadingIndicator.visibility = View.GONE
                waitMessage.visibility = View.GONE
                emptyResponseView.visibility = View.GONE
                contentsView.visibility = View.VISIBLE
                drawLineGraphclicks(results)
                //end if

                clicksIndex = +1
                ctrIndex = +1
                posIndex = +1
                imprIndex = +1
            }*/

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
            }

        }


        //hide spinner and show contents
        loadingIndicator.visibility = View.GONE
        waitMessage.visibility = View.GONE
        contentsView.visibility = View.VISIBLE

        tv_dateOld = contentsView.findViewById<TextView>(R.id.tv_date_old)
        tv_dateOld!!.setText("" + statDateViewOld!!.text.toString().replace(" ", "").replace(" ", ""))


        //part 2 recyclerview for keyword list
        rcViewOld = contentsView.findViewById<RecyclerView>(R.id.recyclerView_old)
        rcViewOld!!.layoutManager = LinearLayoutManager(this!!.getActivity(), LinearLayout.VERTICAL, false)
        //adapter1 = CustomAdapter(getActivity(), results?.rows)
        adapterOld = CompareOldAdapter(getActivity(), results?.rows)

        rcViewOld?.adapter = adapterOld
        adapterOld?.notifyDataSetChanged()

        /*DM().getApi().getAnalyticsClicks(startDate?.time.toString(),
                endDate.time.toString(),
                siteUrl,
                startDateOld?.time.toString(),
                endDateOld?.time.toString(),
                queryGrouping.toString().replace("[", "").replace("]", ""),
                checkbox_clicks.tag.toString(),
                queryFilter.toString(),
                object : Callback<Response> {
                    override fun success(t: Response?, response: Response?) {
                        Log.d(TAG,"startDate : " + startDate?.time.toString())
                        Log.d(TAG,"endDate : " + endDate.time.toString())
                        Log.d(TAG,"url : " + siteUrl)
                        Log.d(TAG,"startDateOld : " + startDateOld?.time.toString())
                        Log.d(TAG,"startDateOld : " + endDateOld?.time.toString())
                        Log.d(TAG,"groupBy : " + queryGrouping.toString().replace("[", "").replace("]", ""))
                        Log.d(TAG,"elements : " + checkbox_clicks.tag.toString())
                        Log.d(TAG,"filterBy : " + queryFilter.toString())


                    }

                    override fun failure(error: RetrofitError?) {
                        Log.d(TAG,"onFailed: " + error.toString())
                    }

                })*/




        /*listViewChange = contentsView.findViewById<ListView>(R.id.listview_1)
        listAdapterChange = object : ArrayAdapter<AnalyticsModel>(getActivity(), R.layout.compare_one) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var convertView = convertView

                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.compare_one, parent, false)
                }

                val e = changenames.get(position)

                checkBox = convertView!!.findViewById(R.id.tvClicks_change)
                checkBox?.setText(e.clicks)

                return convertView
            }

            override fun getCount(): Int {
                return changenames.size
            }
        }
        listViewChange?.setAdapter(listAdapterChange)*/




        rcViewOld?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        /*counter++

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
                        }*/
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

    }


    /*private fun getdata(): MutableList<AnalyticsModel> {
        val cb = object : Callback<AnalyticsRes> {
            override fun success(ns: AnalyticsRes, response: Response) {
                analytics = ns.data
                arrayAdapter?.notifyDataSetChanged()
                Log.d("get", "on: " + ns.data)
            }

            override fun failure(error: RetrofitError) {
                Log.d("get", "off: " + error)
            }
        }


        return getdata()
    }*/


    @SuppressLint("ResourceType")
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
        //this.startDateOld?.add(Calendar.DAY_OF_YEAR, interval)
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
        //statDateViewOld.text = "$startDateText - $endDateText"


        val analyticDateFmt = "yyyy-MM-dd"

        //first analytics date
        val analyticStartDate = SimpleDateFormat(analyticDateFmt)
                .format(startDate!!.time)
                .toString()


        val analyticEndDate = SimpleDateFormat(analyticDateFmt)
                .format(endDate!!.time)
                .toString()

        //comparisonStartDate
        val comparisonStartDate = SimpleDateFormat(analyticDateFmt)
                .format(startDateOld!!.time)
                .toString()

        val comparisonEndDate = SimpleDateFormat(analyticDateFmt)
                .format(endDateOld!!.time)
                .toString()

        //add start date
        queryOptions.put("startDate", analyticStartDate)
        queryOptions.put("endDate", analyticEndDate)
        /*queryOptions.put("startDate", comparisonStartDate)
        queryOptions.put("endDate", comparisonEndDate)*/
        //queryOptions.put("startDate", analyticStartDate)
        //queryOptions.put("endDate", analyticEndDate)

        //Toast.makeText(mContext, "" + clicksData, Toast.LENGTH_SHORT).show()

        //after Task
        val afterTask = fun(activity: Activity,
                            result: SearchAnalyticsQueryResponse?) {

            //process results
            proccessAnalyticsResults(activity, result)
            //proccessAnalyticsResultsOld(activity, result)

        }//end after task


        //lets now fetch site Data
        val analyticsData = WMTools().fetchSiteComparison(
                mActivity,
                siteUrl,
                queryOptions,
                queryFilter,
                queryGrouping,
                /*queryGroups,*/
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
        activity.lineChartCard_comparison.visibility = View.GONE
        activity.pieChartCard_comparison.visibility = View.GONE
        activity.barChartCard_comparison.visibility = View.GONE
        activity.keywordbarChartCard_comparison.visibility = View.GONE

        loadingIndicator.visibility = View.GONE
        waitMessage.visibility = View.GONE

        val emptyResponseView = fragView.findViewById<TextView>(R.id.empty_response_msg);


        Log.e(TAG, results.toString())
        var numberofrows = results?.rows

        Log.e(TAG, numberofrows?.size.toString())

        Log.d(TAG, results?.rows.toString())

        if (!results!!.containsKey("rows") || results!!.isEmpty() || results!!.containsKey("")) {

            //hide content view
            contentsView.visibility = View.GONE

            //show empty response View
            emptyResponseView.visibility = View.VISIBLE


        } else {
            results1 = results
            //hide empty response alert
            emptyResponseView.visibility = View.GONE


            //show content view
            contentsView.visibility = View.VISIBLE


            var dateIndex = 0
            var keywordIndex = 0
            var countryIndex = 0
            var deviceIndex = 0

            var clicksIndex = 0
            var ctrIndex = 0
            var posIndex = 0
            var imprIndex = 0

            //drawLineGraphclicks(results)

            checkbox_clicks.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked) {

                    checkbox_clicks.setChecked(true)
                    checkbox_position.setChecked(false)
                    checkbox_impression.setChecked(false)
                    checkbox_ctr.setChecked(false)
                    //on view
                    emptyResponseView.visibility = View.GONE
                    contentsView.visibility = View.VISIBLE
                    drawLineGraphclicks(results)

                    Log.d("clicks", results.toString())

                    DM().getApi().getAnalyticsClicks(startDate?.time.toString(),
                            endDate?.time.toString(),
                            siteUrl,
                            startDateOld?.time.toString(),
                            endDateOld?.time.toString(),
                            queryGrouping.toString().replace("[", "").replace("]", ""),
                            checkbox_clicks.tag.toString(),
                            queryFilter.toString(),
                            object : Callback<ChangeResponse> {
                                override fun success(t: ChangeResponse?, response: Response?) {

                                    Log.d(TAG, "clicks: " + checkbox_clicks.tag.toString())


                                    Log.d("MainActivity", "" + changenames.size)


                                    var msg: String = ""
                                    for (item: Analytics in changenames.iterator()) {

                                        msg = msg + item.clicks + "\n"

                                    }
                                    Toast.makeText(getActivity(), "List of Category  \n  " + msg, Toast.LENGTH_LONG).show()

                                }

                                override fun failure(error: RetrofitError?) {
                                    Log.d(TAG, "clicks: " + error.toString())
                                }
                            })


                } else {
                    //Toast.makeText(mContext, "Please select any elements to view in chart", Toast.LENGTH_SHORT).show()

                    contentsView.visibility = View.GONE
                    emptyResponseView.visibility = View.VISIBLE
                }
            }

            checkbox_ctr.setOnCheckedChangeListener { compoundButton, isChecked ->

                if (isChecked) {
                    checkbox_clicks.setChecked(false)
                    checkbox_position.setChecked(false)
                    checkbox_impression.setChecked(false)
                    checkbox_ctr.setChecked(true)
                    //on view
                    emptyResponseView.visibility = View.GONE
                    contentsView.visibility = View.VISIBLE
                    drawLineGraphctr(results)

                    Log.d("ctr", results.toString())

                    /*DM().getApi().getAnalyticsClicks(startDate?.time.toString(),
                            endDate?.time.toString(),
                            siteUrl,
                            startDateOld?.time.toString(),
                            endDateOld?.time.toString(),
                            queryGrouping.toString().replace("[", "").replace("]", ""),
                            checkbox_ctr.tag.toString(),
                            queryFilter.toString(),
                            object : Callback<Response> {
                                override fun success(t: Response?, response: Response?) {
                                    Log.d(TAG, "ctr: " + checkbox_ctr.tag.toString())
                                }

                                override fun failure(error: RetrofitError?) {
                                    Log.d(TAG, "ctr: " + error.toString())
                                }
                            })*/
                } else {
                    //Toast.makeText(mContext, "Please select any elements to view in chart", Toast.LENGTH_SHORT).show()
                    contentsView.visibility = View.GONE
                    emptyResponseView.visibility = View.VISIBLE
                }
            }

            checkbox_position.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked) {
                    checkbox_clicks.setChecked(false)
                    checkbox_position.setChecked(true)
                    checkbox_impression.setChecked(false)
                    checkbox_ctr.setChecked(false)
                    //on view
                    emptyResponseView.visibility = View.GONE
                    contentsView.visibility = View.VISIBLE
                    drawLineGraphposition(results)

                    Log.d("position", results.toString())

                    /*DM().getApi().getAnalyticsClicks(startDate?.time.toString(),
                            endDate?.time.toString(),
                            siteUrl,
                            startDateOld?.time.toString(),
                            endDateOld?.time.toString(),
                            queryGrouping.toString().replace("[","").replace("]",""),
                            checkbox_position.tag.toString(),
                            queryFilter.toString(),
                            object : Callback<Response> {
                                override fun success(t: Response?, response: Response?) {
                                    Log.d(TAG,"position: " + checkbox_position.tag.toString())
                                }

                                override fun failure(error: RetrofitError?) {
                                    Log.d(TAG,"position: " + error.toString())
                                }
                            })*/
                } else {
                    //Toast.makeText(mContext, "Please select any elements to view in chart", Toast.LENGTH_SHORT).show()
                    contentsView.visibility = View.GONE
                    emptyResponseView.visibility = View.VISIBLE
                }
            }

            checkbox_impression.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked) {
                    checkbox_clicks.setChecked(false)
                    checkbox_position.setChecked(false)
                    checkbox_ctr.setChecked(false)
                    checkbox_impression.setChecked(true)
                    //on view
                    emptyResponseView.visibility = View.GONE
                    contentsView.visibility = View.VISIBLE
                    drawLineGraphimpr(results)

                    Log.d("impressions", results.toString())


                    /* DM().getApi().getAnalyticsClicks(startDate?.time.toString(),
                             endDate?.time.toString(),
                             siteUrl,
                             startDateOld?.time.toString(),
                             endDateOld?.time.toString(),
                             queryGrouping.toString().replace("[","").replace("]",""),
                             checkbox_impression.tag.toString(),
                             queryFilter.toString(),
                             object : Callback<Response> {
                                 override fun success(t: Response?, response: Response?) {
                                     Log.d(TAG,"impressions: " + checkbox_impression.tag.toString())
                                 }

                                 override fun failure(error: RetrofitError?) {
                                     Log.d(TAG,"impressions: " + error.toString())
                                 }
                             })*/
                } else {
                    //Toast.makeText(mContext, "Please select any elements to view in chart", Toast.LENGTH_SHORT).show()
                    contentsView.visibility = View.GONE
                    emptyResponseView.visibility = View.VISIBLE
                }
            }


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
            //checkbox for click elements selections

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

        tv_date = contentsView.findViewById<TextView>(R.id.tv_date)
        tv_date!!.setText("" + statDateView!!.text.toString().replace(" ", "").replace(" ", ""))

        //part 2 recyclerview for keyword list
        rcView = contentsView.findViewById<RecyclerView>(R.id.recyclerView)
        rcView!!.layoutManager = LinearLayoutManager(this!!.getActivity(), LinearLayout.VERTICAL, false)
        //adapter1 = CustomAdapter(getActivity(), results?.rows)
        adapter1 = CompareAdapter(getActivity(), results!!.rows)

        rcView?.adapter = adapter1
        adapter1?.notifyDataSetChanged()


        rcView?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        //drawLineGraphclicks(results)
                        /*counter++

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
                        }*/
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

    }//end fun

    /*fun change() {
        rcViewChange = fragView.findViewById(R.id.recyclerView_change)
        rcViewChange!!.layoutManager = LinearLayoutManager(this!!.getActivity(), LinearLayout.VERTICAL, false)
        //adapter1 = CustomAdapter(getActivity(), results?.rows)
        adapterChange = ChangeAdapter(getActivity(), results1?.rows!!)

        rcViewChange?.adapter = adapterChange
        adapterChange?.notifyDataSetChanged()
    }*/


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

        pieChartCard_comparison.visibility = View.VISIBLE

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

        keywordbarChartCard_comparison.visibility = View.VISIBLE

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

        barChartCard_comparison.visibility = View.VISIBLE

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
        Log.e("Labels", varDump(xAxisDateLabels))

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
        lineChartCard_comparison.visibility = View.VISIBLE
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

    //element selection section

    fun drawLineGraphclicks(lineChartDataSet: SearchAnalyticsQueryResponse?,
                            enableGrid: Boolean = false) {
        //Toast.makeText(mContext, "click linechart data append  here" + lineChartDataSet.rows.get(0).clicks.toFloat(), Toast.LENGTH_SHORT).show()

        var lineChartView = fragView
                .findViewById<LineChart>(R.id.stats_line_chart)

        //lets listen to show grid toggle
        lineChartToggleGrid.setOnCheckedChangeListener { _, isChecked ->
            //redraw graph
            drawLineGraphclicks(lineChartDataSet, isChecked)
        }//end toggle btn

        //disable descriptions
        lineChartView.description.isEnabled = false
        lineChartView.axisRight.isEnabled = false
        lineChartView.setTouchEnabled(true)
        lineChartView.setDragEnabled(true)
        lineChartView.setScaleEnabled(true)
        lineChartView.setDrawGridBackground(false)
        lineChartView.isFocusable == true

        var xAxisDateLabels = ArrayList<String>()

        //entries
        var clicksGraphEntries: ArrayList<Entry> = ArrayList()
        var clickOldGraphEntries: ArrayList<Entry> = ArrayList()
        var positionGraphEntries: ArrayList<Entry> = ArrayList()

        //if chart exists, clear old data
        if (lineChartExists) {
            lineChartView.clear()
            xAxisDateLabels.clear()
            clicksGraphEntries.clear()
            clickOldGraphEntries.clear()
            clicksGraphEntries.clear()
        }//eend if


        //lets position the x axis at the bottom
        var xAxis = lineChartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis.setDrawGridLines(enableGrid)


        //set gride is needed or not
        lineChartView.axisLeft.setDrawAxisLine(enableGrid)


        val resultsDataRows = lineChartDataSet!!.rows

        var countingClicks: Int = 0
        if (results1 != null && results2 != null) {

            //loop and display chart
            for ((index, rowData) in results1!!.rows.withIndex()) {

                var keys = rowData.getKeys()

                var statDate = keys.elementAt(0)

                //lets add the date
                //xAxisDateLabels.add(index, statDate)
                countingClicks++
                xAxisDateLabels.add(index, countingClicks.toString())

                var clicks = rowData.clicks.toFloat()

                clicksGraphEntries.add(Entry(index.toFloat(), clicks))
                // clickOldGraphEntries.add(Entry(index.toFloat(), clicksOld))
                //positionGraphEntries.add(Entry(index.toFloat(), position))
            }//endloop

            Log.e("Labels", varDump(xAxisDateLabels))

            for ((index, rowData) in results2!!.rows.withIndex()) {

                var keys = rowData.getKeys()

                var statDate = keys.elementAt(0)

                //lets add the date
                //xAxisDateLabels.add(index, statDate)
                /*counting++
                xAxisDateLabels.add(index, counting.toString())*/

                var clicks = rowData.clicks.toFloat()

                clickOldGraphEntries.add(Entry(index.toFloat(), clicks))
            }


            //marker for click linechart
            lineChartView.marker = GraphMarkerViewClicks(
                    ctx = mContext,
                    xAxisDates = xAxisDateLabels,
                    dataSet = results1!!.rows!!,
                    dataSet1 = results2!!.rows!!)

            //clicks data
            var clicksDataSet = LineDataSet(clicksGraphEntries, getString(R.string.clicks))
            clicksDataSet.color = Color.rgb(123, 31, 162)

            var clicksOldDataSet = LineDataSet(clickOldGraphEntries, getString(R.string.clicks))
            clicksOldDataSet.color = Color.rgb(255, 109, 0)

            var lineData = LineData(clicksDataSet, clicksOldDataSet)

            //Log.e("DataSet", varDump(lineData.getDataSetByIndex(0)))
            lineData.notifyDataChanged()

            //SET DATA to graph
            lineChartView.data = lineData

            xAxis.setLabelCount(countingClicks, true)
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
            lineChartCard_comparison.visibility = View.VISIBLE
            lineChartView.animateX(1000)

        }

    }


    fun drawLineGraphctr(lineChartDataSet: SearchAnalyticsQueryResponse?,
                         enableGrid: Boolean = false) {

        var lineChartView = fragView
                .findViewById<LineChart>(R.id.stats_line_chart)

        //lets listen to show grid toggle
        lineChartToggleGrid.setOnCheckedChangeListener { _, isChecked ->
            //redraw graph
            drawLineGraphctr(lineChartDataSet, isChecked)
        }//end toggle btn

        //disable descriptions
        lineChartView.description.isEnabled = false
        lineChartView.axisRight.isEnabled = false
        lineChartView.setTouchEnabled(true)
        lineChartView.setDragEnabled(true)
        lineChartView.setScaleEnabled(true)
        lineChartView.setDrawGridBackground(false)
        lineChartView.isFocusable == true

        var xAxisDateLabels = ArrayList<String>()

        //entries
        var ctrGraphEntries: ArrayList<Entry> = ArrayList()
        var ctrOldGraphEntries: ArrayList<Entry> = ArrayList()

        //if chart exists, clear old data
        if (lineChartExists) {
            lineChartView.clear()
            xAxisDateLabels.clear()
            ctrGraphEntries.clear()
            ctrOldGraphEntries.clear()
            ctrGraphEntries.clear()
        }//eend if


        //lets position the x axis at the bottom
        var xAxis = lineChartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis.setDrawGridLines(enableGrid)


        //set gride is needed or not
        lineChartView.axisLeft.setDrawAxisLine(enableGrid)


        val resultsDataRows = lineChartDataSet!!.rows
        var countingCtr: Int = 0
        if (results1 != null && results2 != null) {

            //loop and display chart
            for ((index, rowData) in results1!!.rows.withIndex()) {

                var keys = rowData.getKeys()

                var statDate = keys.elementAt(0)

                //lets add the date
                //xAxisDateLabels.add(index, statDate)
                countingCtr++
                xAxisDateLabels.add(index, countingCtr.toString())

                var ctr = rowData.ctr.toFloat()

                //var clicksOld = 0.0f

                ctrGraphEntries.add(Entry(index.toFloat(), ctr))
                // clickOldGraphEntries.add(Entry(index.toFloat(), clicksOld))
                //positionGraphEntries.add(Entry(index.toFloat(), position))
            }//endloop

            Log.e("Labels", varDump(xAxisDateLabels))

            for ((index, rowData) in results2!!.rows.withIndex()) {

                var keys = rowData.getKeys()

                var statDate = keys.elementAt(0)

                //lets add the date
                //xAxisDateLabels.add(index, statDate)
                /*counter++
                xAxisDateLabels.add(index, counter.toString())*/

                var ctrOld = rowData.ctr.toFloat()

                ctrOldGraphEntries.add(Entry(index.toFloat(), ctrOld))
                // clickOldGraphEntries.add(Entry(index.toFloat(), clicksOld))
                //positionGraphEntries.add(Entry(index.toFloat(), position))
            }

            //marker for click linechart
            lineChartView.marker = GraphMarkerViewCtr(
                    ctx = mContext,
                    xAxisDates = xAxisDateLabels,
                    dataSet = results1!!.rows!!,
                    dataSet1 = results2!!.rows!!)

            //clicks data
            var ctrDataSet = LineDataSet(ctrGraphEntries, getString(R.string.click_through_rate))
            ctrDataSet.color = Color.rgb(123, 31, 162)

            var clicksOldDataSet = LineDataSet(ctrOldGraphEntries, getString(R.string.click_through_rate))
            clicksOldDataSet.color = Color.rgb(255, 109, 0)

            var lineData = LineData(ctrDataSet, clicksOldDataSet)

            //Log.e("DataSet", varDump(lineData.getDataSetByIndex(0)))
            lineData.notifyDataChanged()

            //SET DATA to graph
            lineChartView.data = lineData

            xAxis.setLabelCount(countingCtr, true)
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
            lineChartCard_comparison.visibility = View.VISIBLE
            lineChartView.animateX(1000)

        }
    }

    fun drawLineGraphposition(lineChartDataSet: SearchAnalyticsQueryResponse?,
                              enableGrid: Boolean = false) {

        var lineChartView = fragView
                .findViewById<LineChart>(R.id.stats_line_chart)

        //lets listen to show grid toggle
        lineChartToggleGrid.setOnCheckedChangeListener { _, isChecked ->
            //redraw graph
            drawLineGraphposition(lineChartDataSet, isChecked)
        }//end toggle btn

        //disable descriptions
        lineChartView.description.isEnabled = false
        lineChartView.axisRight.isEnabled = false
        lineChartView.setTouchEnabled(true)
        lineChartView.setDragEnabled(true)
        lineChartView.setScaleEnabled(true)
        lineChartView.setDrawGridBackground(false)
        lineChartView.isFocusable == true

        var xAxisDateLabels = ArrayList<String>()

        //entries
        var positionGraphEntries: ArrayList<Entry> = ArrayList()
        var positionOldGraphEntries: ArrayList<Entry> = ArrayList()
        //var ctrOldGraphEntries: ArrayList<Entry> = ArrayList()

        //if chart exists, clear old data
        if (lineChartExists) {
            lineChartView.clear()
            xAxisDateLabels.clear()
            positionGraphEntries.clear()
            positionOldGraphEntries.clear()
            positionGraphEntries.clear()
        }//eend if


        //lets position the x axis at the bottom
        var xAxis = lineChartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis.setDrawGridLines(enableGrid)


        //set gride is needed or not
        lineChartView.axisLeft.setDrawAxisLine(enableGrid)


        val resultsDataRows = lineChartDataSet!!.rows
        var countingPos: Int = 0
        if (results1 != null && results2 != null) {

            //loop and display chart
            for ((index, rowData) in results1!!.rows.withIndex()) {

                var keys = rowData.getKeys()

                var statDate = keys.elementAt(0)

                //lets add the date
                //xAxisDateLabels.add(index, statDate)
                countingPos++
                xAxisDateLabels.add(index, countingPos.toString())

                var position = rowData.position.toFloat()

                //var clicksOld = 0.0f

                positionGraphEntries.add(Entry(index.toFloat(), position))
                // clickOldGraphEntries.add(Entry(index.toFloat(), clicksOld))
                //positionGraphEntries.add(Entry(index.toFloat(), position))
            }//endloop

            Log.e("Labels", varDump(xAxisDateLabels))

            for ((index, rowData) in results2!!.rows.withIndex()) {

                var keys = rowData.getKeys()

                var statDate = keys.elementAt(0)

                //lets add the date
                //xAxisDateLabels.add(index, statDate)
                /*counter++
                xAxisDateLabels.add(index, counter.toString())*/

                var posOld = rowData.position.toFloat()

                positionOldGraphEntries.add(Entry(index.toFloat(), posOld))
                // clickOldGraphEntries.add(Entry(index.toFloat(), clicksOld))
                //positionGraphEntries.add(Entry(index.toFloat(), position))
            }

            //marker for click linechart
            lineChartView.marker = GraphMarkerViewPosition(
                    ctx = mContext,
                    xAxisDates = xAxisDateLabels,
                    dataSet = results1!!.rows!!,
                    dataSet1 = results2!!.rows!!)

            //clicks data
            var positionDataSet = LineDataSet(positionGraphEntries, getString(R.string.position))
            positionDataSet.color = Color.rgb(123, 31, 162)

            var positionOldDataSet = LineDataSet(positionOldGraphEntries, getString(R.string.position))
            positionOldDataSet.color = Color.rgb(255, 109, 0)

            var lineData = LineData(positionDataSet, positionOldDataSet)

            //Log.e("DataSet", varDump(lineData.getDataSetByIndex(0)))
            lineData.notifyDataChanged()

            //SET DATA to graph
            lineChartView.data = lineData

            xAxis.setLabelCount(countingPos, true)
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
            lineChartCard_comparison.visibility = View.VISIBLE
            lineChartView.animateX(1000)

        }
    }

    fun drawLineGraphimpr(lineChartDataSet: SearchAnalyticsQueryResponse?,
                          enableGrid: Boolean = false) {

        var lineChartView = fragView
                .findViewById<LineChart>(R.id.stats_line_chart)

        //lets listen to show grid toggle
        lineChartToggleGrid.setOnCheckedChangeListener { _, isChecked ->
            //redraw graph
            drawLineGraphimpr(lineChartDataSet, isChecked)
        }//end toggle btn

        //disable descriptions
        lineChartView.description.isEnabled = false
        lineChartView.axisRight.isEnabled = false
        lineChartView.setTouchEnabled(true)
        lineChartView.setDragEnabled(true)
        lineChartView.setScaleEnabled(true)
        lineChartView.setDrawGridBackground(false)
        lineChartView.isFocusable == true

        var xAxisDateLabels = ArrayList<String>()

        //entries
        var impressionGraphEntries: ArrayList<Entry> = ArrayList()
        var impressionOldGraphEntries: ArrayList<Entry> = ArrayList()
        //var ctrOldGraphEntries: ArrayList<Entry> = ArrayList()

        //if chart exists, clear old data
        if (lineChartExists) {
            lineChartView.clear()
            xAxisDateLabels.clear()
            impressionGraphEntries.clear()
            impressionOldGraphEntries.clear()
            impressionGraphEntries.clear()
        }//eend if


        //lets position the x axis at the bottom
        var xAxis = lineChartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis.setDrawGridLines(enableGrid)


        //set gride is needed or not
        lineChartView.axisLeft.setDrawAxisLine(enableGrid)


        val resultsDataRows = lineChartDataSet!!.rows
        var countingImpression: Int = 0
        if (results1 != null && results2 != null) {

            //loop and display chart
            for ((index, rowData) in results1!!.rows.withIndex()) {

                var keys = rowData.getKeys()

                var statDate = keys.elementAt(0)

                //lets add the date
                //xAxisDateLabels.add(index, statDate)
                countingImpression++
                xAxisDateLabels.add(index, countingImpression.toString())

                var impression = rowData.impressions.toFloat()

                //var clicksOld = 0.0f

                impressionGraphEntries.add(Entry(index.toFloat(), impression))
                // clickOldGraphEntries.add(Entry(index.toFloat(), clicksOld))
                //positionGraphEntries.add(Entry(index.toFloat(), position))
            }//endloop

            Log.e("Labels", varDump(xAxisDateLabels))

            for ((index, rowData) in results2!!.rows.withIndex()) {

                var keys = rowData.getKeys()

                var statDate = keys.elementAt(0)

                //lets add the date
                //xAxisDateLabels.add(index, statDate)
                /*counter++
                xAxisDateLabels.add(index, counter.toString())*/

                var impressionOld = rowData.impressions.toFloat()

                impressionOldGraphEntries.add(Entry(index.toFloat(), impressionOld))
                // clickOldGraphEntries.add(Entry(index.toFloat(), clicksOld))
                //positionGraphEntries.add(Entry(index.toFloat(), position))
            }

            //marker for click linechart
            lineChartView.marker = GraphMarkerViewImpression(
                    ctx = mContext,
                    xAxisDates = xAxisDateLabels,
                    dataSet = results1!!.rows!!,
                    dataSet1 = results2!!.rows!!)

            //clicks data
            var impressionDataSet = LineDataSet(impressionGraphEntries, getString(R.string.impressions))
            impressionDataSet.color = Color.rgb(123, 31, 162)

            var impressionOldDataSet = LineDataSet(impressionOldGraphEntries, getString(R.string.impressions))
            impressionOldDataSet.color = Color.rgb(255, 109, 0)

            var lineData = LineData(impressionDataSet, impressionOldDataSet)

            //Log.e("DataSet", varDump(lineData.getDataSetByIndex(0)))
            lineData.notifyDataChanged()

            //SET DATA to graph
            lineChartView.data = lineData

            xAxis.setLabelCount(countingImpression, true)
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
            lineChartCard_comparison.visibility = View.VISIBLE
            lineChartView.animateX(1000)

        }
    }

    /**
     * filterDialog
     */
    fun initFilterDialog(): AppCompatDialog {

        //val activity = mContext as Activity

        //init dialog
        val dialog = AppCompatDialog(mContext)

        //set the view
        dialog.setContentView(R.layout.filter_compare_dialog)

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
                    proccessStatsOld(0, true)
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
        dialog.setContentView(R.layout.group_compare_dialog)

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
                                .findViewById<AppCompatRadioButton>(groupId)

                        //if checked add it
                        if (checkbox!!.isChecked) {
                            queryGrouping.add(checkbox.tag.toString())
                            //end if
                        }


                    }
                    //end for loop

                    //dismiss
                    dialog.dismiss()

                    //let proccess starts
                    proccessStats(0, true)
                    proccessStatsOld(0, true)
                } //end save grouping onClick

        return dialog
    }//end fun


}//end class
