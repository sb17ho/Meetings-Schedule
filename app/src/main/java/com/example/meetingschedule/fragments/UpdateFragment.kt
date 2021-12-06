package com.example.meetingschedule.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.meetingschedule.databinding.FragmentUpdateBinding
import com.example.meetingschedule.model.MeetingModelClass
import com.example.meetingschedule.viewModel.SharedViewModel

class UpdateFragment : Fragment() {
    private lateinit var updateFragBind: FragmentUpdateBinding
    private val args: UpdateFragmentArgs by navArgs()
    private val sharedViewModel by lazy {
        ViewModelProvider(this)[SharedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        updateFragBind = FragmentUpdateBinding.inflate(layoutInflater, container, false)

        updateFragBind.apply {
            meetingTitleId.setText(args.meetingModel.name)
            startTimeId.setText(args.meetingModel.startTime)
            endTimeId.setText(args.meetingModel.endTime)
            val date = "${args.meetingModel.dd}:${args.meetingModel.mm}:${args.meetingModel.yy}"
            monthDateStartId.setText(date)
            monthDateEndId.setText(date)
            //TODO SET CONTACTS INFO TOO

            updateMeetingButton.setOnClickListener {
                val dateArr = date.split(":")
                val modelClass = MeetingModelClass(
                    _id = args.meetingModel._id, //TODO: Did Change
                    name = meetingTitleId.text.toString(),
                    dd = dateArr[0].toInt(),
                    mm = dateArr[1].toInt(),
                    yy = dateArr[2].toInt(),
                    startTime = startTimeId.text.toString(),
                    endTime = endTimeId.text.toString(),
                    contactID = "null",
                    contactName = "null",
                    contactNumber = "null"
                )

                sharedViewModel.updateSelectedMeeting(
                    args.meetingModel,
                    modelClass
                )
            }
        }

        return updateFragBind.root
    }

}