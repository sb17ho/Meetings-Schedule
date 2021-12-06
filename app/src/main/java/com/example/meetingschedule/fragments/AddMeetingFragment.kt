package com.example.meetingschedule.fragments

import android.app.Activity.RESULT_OK
import android.app.TimePickerDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.meetingschedule.MainActivity
import com.example.meetingschedule.databinding.FragmentAddMeetingBinding
import com.example.meetingschedule.model.MeetingModelClass
import com.example.meetingschedule.viewModel.SharedViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddMeetingFragment : Fragment() {
    private lateinit var addFragBind: FragmentAddMeetingBinding
    private val sharedViewModel by lazy {
        ViewModelProvider(this)[SharedViewModel::class.java]
    }
    private val args: AddMeetingFragmentArgs by navArgs()
    private val dateArr by lazy { args.date.split(":") }
    private lateinit var hr12CurrTime: String
    private var hr24currTime: Date? = null

    private val result =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val uri = it.data?.data
                val contactInfoNeeded = arrayOf(
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                )

                val cursor: Cursor? =
                    requireContext().contentResolver.query(
                        uri!!,
                        contactInfoNeeded,
                        null,
                        null,
                        null
                    )
                cursor?.moveToFirst()

                addFragBind.contactAddId.text =
                    cursor?.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        addFragBind = FragmentAddMeetingBinding.inflate(layoutInflater, container, false)

        val getCurrTime: String = Calendar.getInstance().time.toString().split(" ")[3]
        val ignoreSeconds = getCurrTime.split(":")
        val hhmm = "${ignoreSeconds[0]}:${ignoreSeconds[1]}"

        try {
            hr24currTime = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(hhmm)
            hr12CurrTime = SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(
                hr24currTime
            )
        } catch (e: ParseException) {
            Log.d("Exception", e.stackTraceToString())
        }

        addFragBind.apply {
            startTimeId.text = hr12CurrTime

            startTimeId.setOnClickListener {
                val timePick = TimePickerDialog(
                    requireContext(),
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    { view, hourOfDay, minute ->
                        val timeOfDay = "$hourOfDay:$minute"
                        val time24Format = SimpleDateFormat("HH:mm", Locale.getDefault())
                        try {
                            val date: Date? = time24Format.parse(timeOfDay)
                            val change24To12Hr = SimpleDateFormat("hh:mm aa", Locale.getDefault())
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
                val timePick = TimePickerDialog(
                    requireContext(),
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    { view, hourOfDay, minute ->
                        val timeOfDay = "$hourOfDay:$minute"
                        val time24Format = SimpleDateFormat("HH:mm", Locale.CANADA)
                        try {
                            val date: Date? = time24Format.parse(timeOfDay)
                            when {
                                date!!.after(hr24currTime) -> {
                                    val change24To12Hr = SimpleDateFormat("hh:mm aa", Locale.CANADA)
                                    val hr12time = change24To12Hr.format(date)
                                    endTimeId.text = hr12time
                                }
                                date.after(
                                    SimpleDateFormat(
                                        "HH:mm",
                                        Locale.getDefault()
                                    ).parse("00:00")
                                ) -> {
                                    Toast.makeText(
                                        context,
                                        "Time selected is past Start Time of Meeting",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: ParseException) {
                            Log.d("Exception", e.stackTraceToString())
                        }

                    }, 12, 0, false
                )
                timePick.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                timePick.show()
            }
            monthDateStartId.text = args.date
            monthDateEndId.text = args.date

            meetingTitleId.doOnTextChanged { text, _, _, _ ->
                if (!text.isNullOrBlank()) {
                    meetingTitleId.error = null
                }
            }

            endTimeId.doOnTextChanged { text, _, _, _ ->
                if (!text.isNullOrBlank()) {
                    endTimeId.error = null
                }
            }

            addMeetingAddScreen.setOnClickListener {
                if (meetingTitleId.text.isNullOrBlank() && endTimeId.text.isNullOrBlank()) {
                    meetingTitleId.error = "Field Required"
                    endTimeId.error = "Field Required"
                } else if (endTimeId.text.isNullOrBlank()) {
                    endTimeId.error = "Field Required"
                } else if (meetingTitleId.text.isNullOrBlank()) {
                    meetingTitleId.error = "Field Required"
                } else {
                    addMeetingToCurrDate()
                }
            }
        }

        addFragBind.addContactButton.setOnClickListener {
            pickContact()
        }

        addFragBind.deleteButton.setOnClickListener {
            addFragBind.contactAddId.text = null
        }

        addFragBind.contactAddId.doOnTextChanged { text, _, _, _ ->
            addFragBind.deleteButton.isEnabled = text!!.isNotEmpty()
        }

        return addFragBind.root
    }

    private fun pickContact() {
        result.launch(
            Intent(Intent.ACTION_PICK, Uri.parse("content://contacts")).setType(
                ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            )
        )
    }

    private fun addMeetingToCurrDate() {
        val modelClass = MeetingModelClass(
            name = addFragBind.meetingTitleId.text.toString(),
            dd = dateArr[0].toInt(),
            mm = dateArr[1].toInt(),
            yy = dateArr[2].toInt(),
            startTime = addFragBind.startTimeId.text.toString(),
            endTime = addFragBind.endTimeId.text.toString(),
            contactID = "null",
            contactName = "null",
            contactNumber = "null"
        )
        sharedViewModel.addMeetingsToDatabase(
            modelClass,
            requireContext()
        )
    }

    //To override the action bar title
    override fun onResume() {
        super.onResume()
        (activity as MainActivity)
            .setActionBarTitle("Add Meeting")
    }
}