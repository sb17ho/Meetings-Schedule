package com.example.meetingschedule.fragments

import android.R
import android.app.Activity
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.meetingschedule.databinding.FragmentUpdateBinding
import com.example.meetingschedule.model.MeetingModelClass
import com.example.meetingschedule.viewModel.SharedViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class UpdateFragment : Fragment() {
    private lateinit var updateFragBind: FragmentUpdateBinding
    private val args: UpdateFragmentArgs by navArgs()
    private val sharedViewModel by lazy {
        ViewModelProvider(this)[SharedViewModel::class.java]
    }

    private lateinit var hr12CurrTime: String
    private var hr24currTime: Date? = null
    private var contactName: String? = null
    private var contactNumber: String? = null
    private val result =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
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
                contactName =
                    cursor?.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                contactNumber =
                    cursor?.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val contactInfo = "${contactName} (${contactNumber})"

                updateFragBind.contactInfoView.text =
                    contactInfo
            }
        }
    private lateinit var date: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        updateFragBind = FragmentUpdateBinding.inflate(layoutInflater, container, false)

        updateFragBind.apply {
            meetingTitleId.setText(args.meetingModel.name)
            startTimeId.setText(args.meetingModel.startTime)
            endTimeId.setText(args.meetingModel.endTime)
            date = "${args.meetingModel.dd}:${args.meetingModel.mm}:${args.meetingModel.yy}"
            monthDateStartId.setText(date)
            monthDateEndId.setText(date)
            contactName = args.meetingModel.contactName
            contactNumber = args.meetingModel.contactNumber

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

            startTimeId.setOnClickListener {
                val timePick = TimePickerDialog(
                    requireContext(),
                    R.style.Theme_Holo_Light_Dialog_MinWidth,
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
                endTimeId.text = null
            }

            endTimeId.setOnClickListener {
                val timePick = TimePickerDialog(
                    requireContext(),
                    R.style.Theme_Holo_Light_Dialog_MinWidth,
                    { view, hourOfDay, minute ->
                        val timeOfDay = "$hourOfDay:$minute"
                        val time24Format = SimpleDateFormat("HH:mm", Locale.CANADA)
                        try {
                            val date: Date? = time24Format.parse(timeOfDay)
                            when {
                                date!!.after(
                                    SimpleDateFormat("HH:mm", Locale.getDefault()).parse(
                                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                                            SimpleDateFormat("hh:mm aa", Locale.getDefault()).parse(
                                                startTimeId.text.toString()
                                            )
                                        )
                                    )
                                ) -> {
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

            val contactInfo =
                "${args.meetingModel.contactName} (${args.meetingModel.contactNumber})"

            if (contactInfo.contains("null")) {
                updateFragBind.contactInfoView.text = ""
                deleteButton.isEnabled = false
            } else {
                contactInfoView.setText(contactInfo)
                deleteButton.isEnabled = true
            }

            addContactButton.setOnClickListener {
                pickContact()
            }

            deleteButton.setOnClickListener {
                contactInfoView.text = ""
                contactName = null
                contactNumber = null
            }

            contactInfoView.doOnTextChanged { text, _, _, _ ->
                deleteButton.isEnabled = text!!.isNotEmpty()
            }

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

            updateMeetingButton.setOnClickListener {
                if (meetingTitleId.text.isNullOrBlank() && endTimeId.text.isNullOrBlank()) {
                    meetingTitleId.error = "Field Required"
                    endTimeId.error = "Field Required"
                } else if (endTimeId.text.isNullOrBlank()) {
                    endTimeId.error = "Field Required"
                } else if (meetingTitleId.text.isNullOrBlank()) {
                    meetingTitleId.error = "Field Required"
                } else {
                    updateCurrentMeeting()
                    findNavController().popBackStack()
                }
            }
        }

        return updateFragBind.root
    }

    private fun pickContact() {
        result.launch(
            Intent(Intent.ACTION_PICK, Uri.parse("content://contacts")).setType(
                ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            )
        )
    }

    private fun updateCurrentMeeting() {
        val dateArr = date.split(":")
        val modelClass = MeetingModelClass(
            _id = args.meetingModel._id, //TODO: Did Change
            name = updateFragBind.meetingTitleId.text.toString(),
            dd = dateArr[0].toInt(),
            mm = dateArr[1].toInt(),
            yy = dateArr[2].toInt(),
            startTime = updateFragBind.startTimeId.text.toString(),
            endTime = updateFragBind.endTimeId.text.toString(),
            contactName = contactName.toString(),
            contactNumber = contactNumber.toString()
        )

        sharedViewModel.updateSelectedMeeting(
            args.meetingModel,
            modelClass
        )

        sharedViewModel.liveMeetingsList.value = sharedViewModel.readMeetingsList()
    }

}