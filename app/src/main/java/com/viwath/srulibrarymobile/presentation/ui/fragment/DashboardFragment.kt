/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

@file:Suppress("DEPRECATION")

package com.viwath.srulibrarymobile.presentation.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.databinding.FragmentDashboardBinding
import com.viwath.srulibrarymobile.domain.model.dashboard.Day
import com.viwath.srulibrarymobile.domain.model.dashboard.TotalMajorVisitor
import com.viwath.srulibrarymobile.domain.model.entry.AttendDetail
import com.viwath.srulibrarymobile.presentation.event.DashboardEntryEvent
import com.viwath.srulibrarymobile.presentation.state.StudentState
import com.viwath.srulibrarymobile.presentation.ui.adapter.AttendRecyclerViewAdapter
import com.viwath.srulibrarymobile.presentation.ui.dialog.DialogEntry
import com.viwath.srulibrarymobile.presentation.viewmodel.ConnectivityViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.DashboardViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.CLASSIC
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.MODERN
import com.viwath.srulibrarymobile.utils.view_component.applyBlur
import com.viwath.srulibrarymobile.utils.view_component.showSnackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The DashboardFragment class is responsible for displaying the main dashboard screen of the application.
 * It fetches data from the */
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var isCardEnlarged = false
    private var currentExpandCard: CardView? = null
    private var isDarkMode: Boolean = false
    private var isClassicMode: Boolean = true

    // Define View Model
    private val viewModel: DashboardViewModel by activityViewModels()
    private val connectivityViewModel: ConnectivityViewModel by activityViewModels()
    private val settingViewModel: SettingViewModel by activityViewModels()

    private lateinit var loading: Loading

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loading = Loading(requireActivity())
        isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        lifecycleScope.launch {
            settingViewModel.viewMode.observe(requireActivity()) { viewMode ->
                isClassicMode = when(viewMode){
                    CLASSIC -> true
                    MODERN -> false
                    else -> true
                }
                if (_binding != null){
                    setUpTheme(isDarkMode, isClassicMode)
                }
            }
        }

        setUpUi(isDarkMode)
        observeViewModel(isDarkMode)

        // observe network status
        connectivityViewModel.networkStatus.observe(viewLifecycleOwner){ isConnected ->
            when(isConnected){
                false -> showSnackbar(binding.root, "No Internet Connection")
                true -> observeViewModel(isDarkMode)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //viewModel.state.removeObservers(this)
        _binding = null
    }


    @SuppressLint("SetTextI18n")
    private fun setUpUi(isDarkMode: Boolean){
        binding.swipeRefresh.setColorSchemeResources(R.color.red, R.color.green, R.color.orange)
        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.swipeRefresh.setColorSchemeResources(android.R.color.transparent)
        binding.swipeRefresh.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.getDashboard()
            }, 1000)
        }

        // button entry
        binding.btEntry.setOnClickListener {
            showDialogEntry(requireContext(), isDarkMode)
        }

        // set card animation
        val cardList = listOf(binding.card1, binding.card2, binding.card3, binding.card4)
        cardList.forEach { cardView ->
            setupCardAnimation(cardView)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel(isDarkMode: Boolean){
        viewModel.state.removeObservers(viewLifecycleOwner)
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when{
                state.isLoading -> loading.startLoading()
                state.dashboard != null -> {
                    state.dashboard.let { dashboard ->
                        loading.stopLoading()
                        // entry card
                        val cardData = dashboard.cardData
                        binding.tvCard1.text = cardData[0].cardType
                        binding.tvEntryCount.text = "${cardData[0].amount}"
                        binding.tvEntryPercentage.text = buildString {
                            append(dashboard.cardData[0].analytic.toString())
                            append(" % from yesterday")
                        }
                        // borrow card
                        binding.tvCard2.text = cardData[1].cardType
                        binding.tvBookBorrowCount.text = "${cardData[1].amount}"
                        binding.tvBookBorrowPercentage.text = buildString {
                            append(cardData[1].analytic.toString())
                            append(" % from yesterday")
                        }
                        //
                        binding.tvCard3.text = cardData[2].cardType
                        binding.tvBookSponsorCount.text = "${cardData[2].amount}"
                        binding.tvBookBorrowPercentage.text = buildString {
                            append(cardData[2].analytic.toString())
                            append(" % from last month")
                        }
                        binding.tvCard4.text = cardData[3].cardType
                        binding.tvTotalEntryCount.text = "${cardData[3].amount}"
                        binding.tvTotalEntryPercentage.text = buildString {
                            append(cardData[3].analytic.toString())
                            append(" % from last month")
                        }
                        // weekly visitor
                        binding.tvTotalMajor.text = buildString {
                            append("Total")
                            append(dashboard.totalMajorVisitor.size.toString())
                        }
                        // bar chart
                        barChart(dashboard.weeklyVisitor.days, isDarkMode)
                        // pie chart
                        pieChart(dashboard.totalMajorVisitor, isDarkMode)
                        // progress ring

                        val totalEngBook = dashboard.bookAvailable[0].totalBook
                        val engBookAvailable = dashboard.bookAvailable[0].available
                        val eng = dashboard.bookAvailable[0].language
                        with(binding.progressEnglish){
                            progress = totalEngBook.toFloat()
                            text = engBookAvailable.toString()
                            textColor = if (isDarkMode) Color.WHITE else Color.BLACK
                            finishedStrokeColor = if (isDarkMode) resources.getColor(R.color.purple) else "#8A2BE2".toColorInt()
                        }
                        binding.english.text = eng

                        val totalKhBook = dashboard.bookAvailable[1].totalBook
                        val khBookAvailable = dashboard.bookAvailable[1].available
                        val kh = dashboard.bookAvailable[1].language
                        with(binding.progressKhmer){
                            progress = khBookAvailable.toFloat()
                            text = totalKhBook.toString()
                            textColor = if (isDarkMode) Color.WHITE else Color.BLACK
                            finishedStrokeColor = if (isDarkMode) resources.getColor(R.color.purple) else "#8A2BE2".toColorInt()
                        }
                        binding.khmer.text = kh


                        /// add recycler view
                        val list: List<AttendDetail> = dashboard.customEntry
                        binding.recyclerViewEntry.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        val adapter = AttendRecyclerViewAdapter(requireActivity(), list, isDarkMode, isClassicMode)
                        binding.recyclerViewEntry.adapter = adapter

                    }
                    binding.swipeRefresh.isRefreshing = false
                }
                state.error.isNotEmpty() -> {
                    loading.stopLoading()
                    Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }

        viewModel.username.observe(viewLifecycleOwner) {
            binding.greetingText.text = "Hello, $it"
        }
    }

    // set up theme
    private fun setUpTheme(isDarkMode: Boolean, isClassicMode: Boolean){
        val activity = requireActivity()
        val nightBoxBackground = R.drawable.night_box_bg
        val lightBoxBackground = R.drawable.light_box_bg
        val bottomLightBackground = R.drawable.bottom_light_box_bg
        val bottomNightBackground = R.drawable.bottom_night_box_bg
        val topLightBackground = R.drawable.top_light_box_bg
        val topNightBackground = R.drawable.top_night_box_bg

        val translucentColor = if (isDarkMode) {
            ContextCompat.getColor(requireContext(), R.color.translucent_black_20)
        }
        else {
            ContextCompat.getColor(requireContext(), R.color.translucent_white_20)
        }

        val innerCardTranslucentColor = if (!isDarkMode) {
           ContextCompat.getColor(requireContext(), R.color.translucent_white_35)
        } else {
            ContextCompat.getColor(requireContext(), R.color.translucent_black_35)
        }

        binding.btEntry.setIconTintResource(if (isDarkMode) R.color.white else R.color.black)

        when(isClassicMode){
            true -> {
                binding.cardViewCardSection.setCardBackgroundColor(requireContext().resources.getColor(android.R.color.transparent))
                binding.relativeCard.setBackgroundResource(if (isDarkMode) nightBoxBackground else lightBoxBackground)
                binding.relativeBarChart.setBackgroundResource(if (isDarkMode) nightBoxBackground else lightBoxBackground)
                binding.pieChartBlur.setBackgroundResource(if (isDarkMode) nightBoxBackground else lightBoxBackground)
                binding.linearProgress.setBackgroundResource(if (isDarkMode) nightBoxBackground else lightBoxBackground)
                binding.constraintRecycle.setBackgroundResource(if (isDarkMode) topNightBackground else topLightBackground)
                binding.linearAttendRecyclerView.setBackgroundResource(if (isDarkMode) bottomNightBackground else bottomLightBackground)
            }
            false -> {
                //
                binding.cardViewCardSection.apply {
                    setCardBackgroundColor(requireContext().getColor(android.R.color.transparent))
                }
                binding.card1.setCardBackgroundColor(requireContext().getColor(android.R.color.transparent))

                binding.blurViewCardSection.applyBlur(activity, 5f, translucentColor)
                binding.cardEntryBlur.applyBlur(activity, 50f, innerCardTranslucentColor)
                binding.cardBorrowBlur.applyBlur(activity, 50f, innerCardTranslucentColor)
                binding.cardDonationBlur.applyBlur(activity, 50f, innerCardTranslucentColor)
                binding.cardMonthlyEntryBlur.applyBlur(activity, 50f, innerCardTranslucentColor)

                binding.blurViewBarChartSection.applyBlur(activity, 5f, translucentColor)
                binding.pieChartBlur.applyBlur(activity, 5f, translucentColor)
                binding.progressRingBlur.applyBlur(activity, 5f, translucentColor)
                binding.blurViewEntryList.applyBlur(activity, 5f, translucentColor)
                binding.blurViewBtEntry.applyBlur(activity, 5f, translucentColor)
            }
        }
    }

    ///////////////////////* Card View *///////////////////////
    //                                                       //
    /* set up Card View Animation */
    private fun setupCardAnimation(cardView: CardView) {
        cardView.setOnClickListener {
            if (currentExpandCard != null && currentExpandCard != cardView){
                animateCard(currentExpandCard!!, 1.05f, 1.0f)
            }
            if (currentExpandCard == cardView){
                animateCard(cardView, 1.05f, 1.0f)
                currentExpandCard = null
            }else{
                animateCard(cardView, 1.0f, 1.05f)
                currentExpandCard = cardView
            }
        }
    }
    // Card View Animation
    private fun animateCard(cardView: CardView, fromScale: Float, toScale: Float) {
        val scaleAnimation = ScaleAnimation(
            fromScale, toScale, // Start and end values for the X axis scaling
            fromScale, toScale, // Start and end values for the Y axis scaling
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f // Pivot point of Y scaling
        ).apply {
            duration = 300 // Animation duration in milliseconds
            fillAfter = true // If true, the animation transformation is applied after it ends
        }

        scaleAnimation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                val layoutParam = cardView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParam.height = if (isCardEnlarged) ViewGroup.LayoutParams.WRAP_CONTENT else ViewGroup.LayoutParams.WRAP_CONTENT
                cardView.layoutParams = layoutParam
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })

        cardView.startAnimation(scaleAnimation)
    }

    ///////////////////////* Bar Chart *///////////////////////
    //                                                       //
    private fun barChart(day: List<Day>, isDarkMode: Boolean){
        val entries: MutableList<BarEntry> = ArrayList()
        for (i in 0..6){
            entries.add(BarEntry(i.toFloat(), day[i].count.toFloat()))
        }
        val dataSet = BarDataSet(entries, "Weekly Visitors")
        dataSet.setColors(
            "#800080".toColorInt(),  // Mon
            "#FF6347".toColorInt(),  // Tue
            "#FFD700".toColorInt(),  // Wen
            "#00FA9A".toColorInt(),  // Thu
            "#1E90FF".toColorInt(),  // Fri
            "#21cc4e".toColorInt(),  // Sat
            "#e314ba".toColorInt()   // Sun
        )

        dataSet.valueTextColor = if (isDarkMode) Color.WHITE else Color.BLACK
        dataSet.valueTextSize = 12f
        val barData = BarData(dataSet)
        binding.barChart.data = barData
        binding.barChart.invalidate() // Refresh the chart

        with(binding.barChart){
            description.isEnabled = false
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(arrayOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"))
                textColor = if (isDarkMode) Color.WHITE else Color.BLACK
            }
            axisLeft.apply {
                textColor = if (isDarkMode) Color.WHITE else Color.BLACK
                textSize = 12f
            }
            axisRight.apply {
                textColor = if (isDarkMode) Color.WHITE else Color.BLACK
                textSize = 12f
            }
            legend.apply {
                textColor = if (isDarkMode) Color.WHITE else Color.BLACK
                textSize = 12f
            }
        }

    }

    ///////////////////////* Pie Chart *///////////////////////
    //                                                       //
    private fun pieChart(majorVisitor: List<TotalMajorVisitor>, isDarkMode: Boolean){
        val entries: MutableList<PieEntry> = ArrayList()
        val color: MutableList<Int> = ArrayList()
        majorVisitor.forEach { major ->
            entries.add(PieEntry(major.totalAmount.toFloat(), cutMajorName(major.majorName)))
            color.add(generateColor(major.majorName))
        }
        val pieDataSet = PieDataSet(entries, "").apply {
            colors = color
            valueTextColor = if (isDarkMode) Color.WHITE else Color.BLACK
            valueTextSize = 14f
        }


        val data = PieData(pieDataSet)
        with(binding.pieChart){
            this.data = data
            this.description.isEnabled = false
            this.legend.apply {
                textColor = if (isDarkMode) Color.WHITE else Color.BLACK
                textSize = 14f
                formSize = 18f
            }
            description.isEnabled = false
            holeRadius = 60f
            setDrawEntryLabels(false)
            setEntryLabelColor(Color.WHITE)
            setUsePercentValues(false)
            setDrawCenterText(true)
            holeRadius = 60f
            transparentCircleRadius = 61f
            setCenterTextColor("#99CCFF".toColorInt())
            invalidate()
        }


    }
    /// generate color for pia chart
    private fun generateColor(str: String): Int{
        val hashCode = str.hashCode()
        val r = (hashCode and 0xFF0000 shr 16)
        val g = (hashCode and 0x00FF00 shr 8)
        val b = (hashCode and 0x0000FF)
        return Color.rgb(r, g, b)
    }
    // Cut major name
    private fun cutMajorName(name: String): String{
        val str = name.split(" ")
        return if (str.size > 1)
            name.split(" ").joinToString("") { it.first().uppercase() }
        else
            name[2].uppercase()
    }

    //////////
    @SuppressLint("SetTextI18n")
    private fun showDialogEntry(context: Context, isDarkMode: Boolean){

        val dialogView = LayoutInflater.from(context).inflate( R.layout.dialog_entry, null)
        // change view background
        dialogView.setBackgroundColor(if (isDarkMode) Color.BLACK else Color.WHITE) // change background
        // create dialog
        val dialog = MaterialAlertDialogBuilder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val input = DialogEntry(dialogView)

        //button
        input.btnFind.setImageResource(if (isDarkMode) R.drawable.ic_light_search_24 else R.drawable.ic_night_search_24)
        input.btnFind.setOnClickListener {
            val studentId = input.getStudentId()
            if (studentId != null){
                // search
                viewModel.onEntryEvent(DashboardEntryEvent.GetStudent(studentId.toString()))
            }
        }
        input.btEntry.setOnClickListener {
            val studentId = input.entryPurpose().first
            val purpose = input.entryPurpose().second ?: ""
            if (purpose.isEmpty())
                Toast.makeText(context, "Purpose is empty", Toast.LENGTH_LONG).show()
            else {
                viewModel.onEntryEvent(DashboardEntryEvent.SaveAttend(studentId.toString(), purpose))
            }
        }

        // collect channel result
        lifecycleScope.launch {
            viewModel.eventFlow.collect { result ->
                when(result){
                    is StudentState.GetStudentLoading -> loading.startLoading()
                    is StudentState.GetStudentSuccess -> {
                        loading.stopLoading()
                        with(result.students){
                            input.setSearchResult(studentName, majorName, generation)
                        }
                        withContext(Dispatchers.Main){
                            input.btEntry.isEnabled = true
                        }
                    }
                    is StudentState.GetStudentError -> {
                        loading.stopLoading()
                        Snackbar.make(dialogView, result.errorMsg, Snackbar.LENGTH_LONG).show()
                    }
                    is StudentState.SaveAttendLoading -> loading.startLoading()
                    is StudentState.SaveAttendSuccess -> {
                        loading.stopLoading()
                        Toast.makeText(context, "Entry Saved Successfully!", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                    is StudentState.SaveAttendError -> {
                        loading.stopLoading()
                        Snackbar.make(dialogView, result.errorMsg, Snackbar.LENGTH_LONG).show()
                    }

                }
            }
        }
        dialog.show()
    }

}