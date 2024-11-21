package com.viwath.srulibrarymobile.presentation.ui.fragment

import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.FragmentDashboardBinding
import com.viwath.srulibrarymobile.domain.model.AttendDetail
import com.viwath.srulibrarymobile.domain.model.dashboard.Day
import com.viwath.srulibrarymobile.domain.model.dashboard.TotalMajorVisitor
import com.viwath.srulibrarymobile.presentation.ui.adapter.EntryRecycleViewAdapter
import com.viwath.srulibrarymobile.presentation.viewmodel.DashboardViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private var isCardEnlarged = false
    private var currentExpandCard: CardView? = null

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

        binding.greetingText.text = "Hello, ${viewModel.username}"
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when{
                state.isLoading -> binding.swipeRefresh.isRefreshing = true
                state.dashboard != null -> {
                    state.dashboard.let { dashboard ->
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
                        barChart(dashboard.weeklyVisitor.days)
                        // pie chart
                        pieChart(dashboard.totalMajorVisitor)
                        // progress ring

                        val totalKhBook = dashboard.bookAvailable[0].totalBook
                        val khBookAvailable = dashboard.bookAvailable[0].available
                        val kh = dashboard.bookAvailable[0].language
                        binding.progressKhmer.progress = khBookAvailable.toFloat()
                        binding.progressKhmer.text = totalKhBook.toString()
                        binding.khmer.text = kh

                        val totalEngBook = dashboard.bookAvailable[1].totalBook
                        val engBookAvailable = dashboard.bookAvailable[1].available
                        val eng = dashboard.bookAvailable[1].language
                        binding.progressEnglish.progress = totalEngBook.toFloat()
                        binding.progressEnglish.text = engBookAvailable.toString()
                        binding.english.text = eng


                        /// add recycler view
                        val list: List<AttendDetail> = dashboard.customEntry
                        binding.recyclerViewEntry.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        val adapter = EntryRecycleViewAdapter(list)
                        binding.recyclerViewEntry.adapter = adapter

                    }
                    binding.swipeRefresh.isRefreshing = false
                }
                state.error.isNotEmpty() -> {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                    binding.swipeRefresh.isRefreshing = false
                }
            }
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

    }

    ///////////////////////* Card View *///////////////////////
    //                                                       //
    /* set up Card View Animation */
    private fun setupCardAnimation(cardView: CardView) {
        cardView.setOnClickListener {
            if (currentExpandCard != null && currentExpandCard != cardView){
                animateCard(currentExpandCard!!, 1.1f, 1.0f)
            }
            if (currentExpandCard == cardView){
                animateCard(cardView, 1.1f, 1.0f)
                currentExpandCard = null
            }else{
                animateCard(cardView, 1.0f, 1.1f)
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
    private fun barChart(day: List<Day>){
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

        val barData = BarData(dataSet)
        binding.barChart.data = barData
        binding.barChart.invalidate() // Refresh the chart

        binding.barChart.description.isEnabled = false
        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"))

    }

    ///////////////////////* Pie Chart *///////////////////////
    //                                                       //
    private fun pieChart(majorVisitor: List<TotalMajorVisitor>){
        val entries: MutableList<PieEntry> = ArrayList()
        val color: MutableList<Int> = ArrayList()
        majorVisitor.forEach { major ->
            entries.add(PieEntry(major.totalAmount.toFloat(), cutMajorName(major.majorName)))
            color.add(generateColor(major.majorName))
        }
        val pieDataSet = PieDataSet(entries, "")
        pieDataSet.colors = color
        pieDataSet.valueTextColor = Color.WHITE
        val data = PieData(pieDataSet)
        binding.pieChart.data = data
        binding.pieChart.description.isEnabled = false
        binding.pieChart.legend.formSize = 18f
        binding.pieChart.holeRadius = 60f
        binding.pieChart.setDrawEntryLabels(false)
        binding.pieChart.setEntryLabelColor(Color.WHITE)
        binding.pieChart.setUsePercentValues(false)
        binding.pieChart.setDrawCenterText(true)
        binding.pieChart.setCenterTextColor(Color.parseColor("#99CCFF"))
        binding.pieChart.invalidate()

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

}