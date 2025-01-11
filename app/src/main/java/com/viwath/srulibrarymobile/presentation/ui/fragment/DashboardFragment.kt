/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
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
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.databinding.FragmentDashboardBinding
import com.viwath.srulibrarymobile.domain.model.AttendDetail
import com.viwath.srulibrarymobile.domain.model.dashboard.Day
import com.viwath.srulibrarymobile.domain.model.dashboard.TotalMajorVisitor
import com.viwath.srulibrarymobile.presentation.event.DashboardEntryEvent
import com.viwath.srulibrarymobile.presentation.state.StudentState
import com.viwath.srulibrarymobile.presentation.ui.adapter.EntryRecycleViewAdapter
import com.viwath.srulibrarymobile.presentation.viewmodel.DashboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private var isCardEnlarged = false
    private var currentExpandCard: CardView? = null
    private lateinit var loading: Loading

    // Define View Model
    private val viewModel: DashboardViewModel by activityViewModels()

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
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        setUpTheme(isDarkMode)

        /// swipe refresh
        binding.swipeRefresh.setColorSchemeResources(R.color.red, R.color.green, R.color.orange)
        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.swipeRefresh.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.getDashboard()
            }, 3000)
        }

        ////////////////

        //// load data
        binding.greetingText.text = "Hello, ${viewModel.username}"
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when{
                state.isLoading -> startLoading()
                state.dashboard != null -> {
                    state.dashboard.let { dashboard ->
                        stopLoading()
                        // entry card
                        val cardData = dashboard.cardData
                        Log.d("Viwath", "onViewCreated: ${cardData[1]}")
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
                            finishedStrokeColor = if (isDarkMode) resources.getColor(R.color.purple) else Color.parseColor("#8A2BE2")
                        }
                        binding.english.text = eng

                        val totalKhBook = dashboard.bookAvailable[1].totalBook
                        val khBookAvailable = dashboard.bookAvailable[1].available
                        val kh = dashboard.bookAvailable[1].language
                        with(binding.progressKhmer){
                            progress = khBookAvailable.toFloat()
                            text = totalKhBook.toString()
                            textColor = if (isDarkMode) Color.WHITE else Color.BLACK
                            finishedStrokeColor = if (isDarkMode) resources.getColor(R.color.purple) else Color.parseColor("#8A2BE2")
                        }
                        binding.khmer.text = kh


                        /// add recycler view
                        val list: List<AttendDetail> = dashboard.customEntry
                        binding.recyclerViewEntry.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        val adapter = EntryRecycleViewAdapter(list)
                        binding.recyclerViewEntry.adapter = adapter

                    }
                    binding.swipeRefresh.isRefreshing = false
                }
                state.error.isNotEmpty() -> {
                    stopLoading()
                    Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }

        // button entry
        binding.btEntry.setOnClickListener {
            showModal(requireContext(), isDarkMode)
        }

        // set card animation
        val cardList = listOf(binding.card1, binding.card2, binding.card3, binding.card4)
        cardList.forEach { cardView ->
            setupCardAnimation(cardView)
        }

    }

    // set up theme
    private fun setUpTheme(isDarkMode: Boolean){
        val nightBoxBackground = R.drawable.night_box_bg
        val lightBoxBackground = R.drawable.light_box_bg
        val bottomLightBackground = R.drawable.bottom_light_box_bg
        val bottomNightBackground = R.drawable.bottom_night_box_bg
        val topLightBackground = R.drawable.top_light_box_bg
        val topNightBackground = R.drawable.top_night_box_bg

        binding.relativeCard.setBackgroundResource(if (isDarkMode) nightBoxBackground else lightBoxBackground)
        binding.relativeBarChart.setBackgroundResource(if (isDarkMode) nightBoxBackground else lightBoxBackground)
        binding.relativePieChart.setBackgroundResource(if (isDarkMode) nightBoxBackground else lightBoxBackground)
        binding.linearProgress.setBackgroundResource(if (isDarkMode) nightBoxBackground else lightBoxBackground)
        binding.constraintRecycle.setBackgroundResource(if (isDarkMode) topNightBackground else topLightBackground)
        binding.horizontalScrollView.setBackgroundResource(if (isDarkMode) bottomNightBackground else bottomLightBackground)

        binding.btEntry.setIconTintResource(if (isDarkMode) R.color.white else R.color.black)
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
            Color.parseColor("#800080"),  // Mon
            Color.parseColor("#FF6347"),  // Tue
            Color.parseColor("#FFD700"),  // Wen
            Color.parseColor("#00FA9A"),  // Thu
            Color.parseColor("#1E90FF"),  // Fri
            Color.parseColor("#21cc4e"),  // Sat
            Color.parseColor("#e314ba")   // Sun
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
            transparentCircleRadius = 65f
            setCenterTextColor(Color.parseColor("#99CCFF"))
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
    private fun showModal(context: Context, isDarkMode: Boolean){
        // purpose option
        val purposes = mutableListOf<String>()
        val usePC = "Use PC"
        val reading = "Reading"
        val assignment = "Assignment"
        val other = "Other"

        val view = LayoutInflater.from(context).inflate( R.layout.modal_entry, null)
        // change view background
        view.setBackgroundColor(if (isDarkMode) Color.BLACK else Color.WHITE)
        // create dialog
        val dialog = MaterialAlertDialogBuilder(context)
            .setView(view)
            .setCancelable(true)
            .create()

        //button
        val btnFind = view.findViewById<ImageButton>(R.id.btFind)
        val btEntry = view.findViewById<Button>(R.id.btn_submit)
        btnFind.setImageResource(if (isDarkMode) R.drawable.ic_light_search_24 else R.drawable.ic_night_search_24)
        // TextView and EditText
        val edtStudentId = view.findViewById<EditText>(R.id.et_student_id)
        val tvStudentName = view.findViewById<TextView>(R.id.tv_student_name)
        val tvMajorName = view.findViewById<TextView>(R.id.tv_major_name)
        val tvGeneration = view.findViewById<TextView>(R.id.tv_generation)
        // radio button
        val cbReading = view.findViewById<MaterialCheckBox>(R.id.rb_read_book)
        val cbAssignment = view.findViewById<MaterialCheckBox>(R.id.rb_assignment)
        val cbUsePc= view.findViewById<MaterialCheckBox>(R.id.rb_use_pc)
        val cbOther = view.findViewById<MaterialCheckBox>(R.id.rb_other)


        btnFind.setOnClickListener {
            val studentId = edtStudentId.text.toString().toLongOrNull()
            if (studentId != null){
                // search
                viewModel.onEntryEvent(DashboardEntryEvent.GetStudent(studentId.toString()))
            }
        }

        btEntry.setOnClickListener {
            val studentId = edtStudentId.text.toString()
            if (cbReading.isChecked) purposes.add(reading)
            if (cbAssignment.isChecked) purposes.add(assignment)
            if (cbUsePc.isChecked) purposes.add(usePC)
            if (cbOther.isChecked) purposes.add(other)
            val purpose = purposes.joinToString(", ")

            if (purpose.isEmpty())
                Toast.makeText(context, "Purpose is empty", Toast.LENGTH_LONG).show()
            else {
                viewModel.onEntryEvent(DashboardEntryEvent.SaveAttend(studentId, purpose))
            }
        }

        // collect channel result
        lifecycleScope.launch {
            viewModel.eventFlow.collect { result ->
                when(result){
                    is StudentState.GetStudentLoading -> startLoading()
                    is StudentState.GetStudentSuccess -> {
                        stopLoading()
                        with(result.students){
                            tvStudentName.text = "Name: $studentName"
                            tvGeneration.text = "Generation: $generation"
                            tvMajorName.text = "Major: $majorName"
                        }
                        withContext(Dispatchers.Main){
                            btEntry.isEnabled = true
                        }
                    }
                    is StudentState.GetStudentError -> {
                        stopLoading()
                        Snackbar.make(view, result.errorMsg, Snackbar.LENGTH_LONG).show()
                    }
                    is StudentState.SaveAttendLoading -> startLoading()
                    is StudentState.SaveAttendSuccess -> {
                        stopLoading()
                        Toast.makeText(context, "Entry Saved Successfully!", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                    is StudentState.SaveAttendError -> {
                        stopLoading()
                        Snackbar.make(view, result.errorMsg, Snackbar.LENGTH_LONG).show()
                    }

                }
            }
        }

        dialog.show()
    }

    private fun startLoading() = CoroutineScope(Dispatchers.Main).launch{
        loading.loadingStart()
    }

    private fun stopLoading() = CoroutineScope(Dispatchers.Main).launch {
        loading.loadingDismiss()
    }

}