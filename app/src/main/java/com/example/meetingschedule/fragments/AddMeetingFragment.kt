package com.example.meetingschedule.fragments

import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.meetingschedule.databinding.FragmentAddMeetingBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddMeetingFragment : Fragment() {
    private lateinit var addFragBind: FragmentAddMeetingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        addFragBind = FragmentAddMeetingBinding.inflate(layoutInflater, container, false)

        addFragBind.apply {
            startTimeId.setOnClickListener {
                val timePick: TimePickerDialog = TimePickerDialog(
                    requireContext(),
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    { view, hourOfDay, minute ->
                        val timeOfDay: String = "$hourOfDay:$minute"
                        val time24Format = SimpleDateFormat("HH:mm", Locale.CANADA)
                        try {
                            val date: Date? = time24Format.parse(timeOfDay)
                            val change24To12Hr = SimpleDateFormat("hh:mm aa", Locale.CANADA)
                            startTimeId.text = change24To12Hr.format(date)
                        } catch (e: ParseException) {
                            Log.d("Exception", e.stackTraceToString())
                        }

                    }, 12, 0, false
                )
                timePick.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                timePick.show()
            }

            endTimeId.setOnClickListener {
                val timePick: TimePickerDialog = TimePickerDialog(
                    requireContext(),
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    { view, hourOfDay, minute ->
                        val timeOfDay: String = "$hourOfDay:$minute"
                        val time24Format = SimpleDateFormat("HH:mm", Locale.CANADA)
                        try {
                            val date: Date? = time24Format.parse(timeOfDay)
                            val change24To12Hr = SimpleDateFormat("hh:mm aa", Locale.CANADA)
                            endTimeId.text = change24To12Hr.format(date)
                        } catch (e: ParseException) {
                            Log.d("Exception", e.stackTraceToString())
                        }

                    }, 12, 0, false
                )
                timePick.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                timePick.show()
            }
        }

        return addFragBind.root
    }
}