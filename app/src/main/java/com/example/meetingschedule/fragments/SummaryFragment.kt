package com.example.meetingschedule.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meetingschedule.MainActivity
import com.example.meetingschedule.R
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
        setHasOptionsMenu(true)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_all, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_all_button) {
            sharedViewModel.deleteAllMeetings()
            sharedViewModel.liveAllMeetingsList.value = sharedViewModel.readAllMeetingsList()
        }
        return super.onOptionsItemSelected(item)
    }

    //To override the action bar title
    override fun onResume() {
        super.onResume()
        (activity as MainActivity)
            .setActionBarTitle("Summary")
    }

}