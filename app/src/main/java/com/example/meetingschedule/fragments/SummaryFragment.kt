package com.example.meetingschedule.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meetingschedule.adapters.SummaryRecyclerAdapter
import com.example.meetingschedule.databinding.FragmentSummaryBinding
import com.example.meetingschedule.viewModel.SharedViewModel

class SummaryFragment : Fragment() {
    private lateinit var summaryBinding: FragmentSummaryBinding
    private val recyclerAdapter: SummaryRecyclerAdapter by lazy {
        SummaryRecyclerAdapter()
    }
    private val sharedViewModel by lazy {
        ViewModelProvider(this)[SharedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        summaryBinding = FragmentSummaryBinding.inflate(layoutInflater, container, false)

        summaryBinding.apply {
            summaryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            summaryRecyclerView.adapter = recyclerAdapter
        }

        sharedViewModel.liveAllMeetingsList.value =
            sharedViewModel.readAllMeetings(requireContext())

        sharedViewModel.liveAllMeetingsList.observe(viewLifecycleOwner) {
            recyclerAdapter.setAllMeetingsList(
                sharedViewModel.readAllMeetingsList()
            )
        }

        return summaryBinding.root
    }

}

//        Log.w("ALL MEETINGS", sharedViewModel.readAllMeetings(requireContext()).toString())
//        Log.w("ALL MEETINGS BY DATE", sharedViewModel.readMeetings(
//            splitCurrDate[0],
//            splitCurrDate[1],
//            splitCurrDate[2],
//            requireContext()
//        ).toString())