package com.example.meetingschedule.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meetingschedule.MainActivity
import com.example.meetingschedule.R
import com.example.meetingschedule.adapters.HomeRecyclerAdapter
import com.example.meetingschedule.databinding.FragmentHomeBinding
import com.example.meetingschedule.viewModel.SharedViewModel
import java.util.*


class HomeFragment : Fragment() {
    private lateinit var homeBind: FragmentHomeBinding
    private val recyclerAdapter: HomeRecyclerAdapter by lazy {
        HomeRecyclerAdapter()
    }
    private val sharedViewModel by lazy {
        ViewModelProvider(this)[SharedViewModel::class.java]
    }
    private lateinit var currDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBind = FragmentHomeBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)

        val getCurrDate: List<String> =
            Calendar.getInstance().time.toString()
                .split(" ")

        currDate =
            "${getCurrDate[2]}:${sharedViewModel.parseMonthStringToInt(getCurrDate[1])}:${getCurrDate[5]}"

        homeBind.apply {
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                currDate = "$dayOfMonth:${month + 1}:$year"
                recyclerAdapter.setMeetingsList(
                    sharedViewModel.readMeetings(
                        dayOfMonth.toString(),
                        (month + 1).toString(),
                        year.toString(),
                        requireContext()
                    )
                )
            }

            recyclerViewId.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewId.adapter = recyclerAdapter
        }

        val splitCurrDate = currDate.split(":")
        recyclerAdapter.setMeetingsList(
            sharedViewModel.readMeetings(
                splitCurrDate[0],
                splitCurrDate[1],
                splitCurrDate[2],
                requireContext()
            )
        )

        return homeBind.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_button) {
            homeBind.root.findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToAddMeetingFragment(currDate))
        }
        return super.onOptionsItemSelected(item)
    }

    //To override the action bar title
    override fun onResume() {
        super.onResume()
        (activity as MainActivity)
            .setActionBarTitle("Calendar")
    }
}