package com.example.meetingschedule.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
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
                sharedViewModel.readMeetings(
                    dayOfMonth,
                    (month + 1),
                    year,
                    requireContext()
                )
                sharedViewModel.liveMeetingsList.value = sharedViewModel.readMeetingsList()
                sharedViewModel.liveMeetingsList.observe(viewLifecycleOwner) {
                    recyclerAdapter.setMeetingsList(
                        sharedViewModel.readMeetingsList()
                    )
                }
            }

            recyclerViewId.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewId.adapter = recyclerAdapter
        }

        val splitCurrDate = currDate.split(":")
        sharedViewModel.readMeetings(
            splitCurrDate[0].toInt(),
            splitCurrDate[1].toInt(),
            splitCurrDate[2].toInt(),
            requireContext()
        )

        sharedViewModel.liveMeetingsList.value = sharedViewModel.readMeetingsList()
        sharedViewModel.liveMeetingsList.observe(viewLifecycleOwner) {
            recyclerAdapter.setMeetingsList(
                sharedViewModel.readMeetingsList()
            )
        }

        return homeBind.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_button) {
            homeBind.root.findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToAddMeetingFragment(currDate))
        } else if (item.itemId == R.id.delete_all_today_button) {
            val splitCurrDate = currDate.split(":")
            sharedViewModel.deleteAllTodayMeetings(
                splitCurrDate[0].toInt(),
                splitCurrDate[1].toInt(),
                splitCurrDate[2].toInt(),
                requireContext()
            )
            sharedViewModel.liveMeetingsList.value = sharedViewModel.readMeetingsList()
            Toast.makeText(requireContext(), "All Today's Meetings Deleted", Toast.LENGTH_SHORT)
                .show()
        } else if (item.itemId == R.id.push_to_next_day) {
            //TODO("UPDATE THE MEETINGS")
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