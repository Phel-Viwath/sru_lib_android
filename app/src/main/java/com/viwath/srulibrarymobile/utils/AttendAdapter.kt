package com.viwath.srulibrarymobile.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.domain.model.AttendDetail

class AttendAdapter(
    context: Context,
    private val resource: Int,
    private val items: List<com.viwath.srulibrarymobile.domain.model.AttendDetail>
) : ArrayAdapter<com.viwath.srulibrarymobile.domain.model.AttendDetail>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val item = items[position]

        view.findViewById<TextView>(R.id.tvNo).text = (position + 1).toString()
        view.findViewById<TextView>(R.id.tvStudentId).text = item.studentId.toString()
        view.findViewById<TextView>(R.id.tvStudentName).text = item.studentName
        view.findViewById<TextView>(R.id.tvGender).text = item.gender
        view.findViewById<TextView>(R.id.tvMajor).text = item.major
        view.findViewById<TextView>(R.id.tvEntryTimes).text = item.entryTimes
        view.findViewById<TextView>(R.id.tvExitingTimes).text = item.exitingTimes
        view.findViewById<TextView>(R.id.tvPurpose).text = item.purpose
        view.findViewById<TextView>(R.id.tvStatus).text = item.status

        return view
    }
}