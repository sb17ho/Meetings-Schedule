package com.example.meetingschedule.fragments

import android.os.Bundle
import android.util.Log
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
import com.example.meetingschedule.model.MeetingModelClass
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

        recyclerAdapter.setOnItemClickListener(object : HomeRecyclerAdapter.OnItemClickListener {
            override fun onItemClickListener(meetings: MeetingModelClass) {
                sharedViewModel.deleteSelectedMeeting(meetings)
                sharedViewModel.readMeetings(
                    splitCurrDate[0].toInt(),
                    splitCurrDate[1].toInt(),
                    splitCurrDate[2].toInt(),
                    requireContext()
                )
                sharedViewModel.liveMeetingsList.value = sharedViewModel.readMeetingsList()
            }
        })

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
            val splitCurrDate = currDate.split(":")

            Log.w("DAY OF WEEK", Calendar.DAY_OF_WEEK.toString())
            Log.w("DATE", Calendar.DATE.toString())
            Log.w("SATURDAY", Calendar.MONDAY.toString())

            val gregCalendar: Calendar = GregorianCalendar()
            if (Calendar.DAY_OF_WEEK == 6) {
                gregCalendar.add(Calendar.DATE, 7) //SATURDAY
            } else if (Calendar.DAY_OF_WEEK == 7) {
                gregCalendar.add(Calendar.DATE, 6) //SUNDAY
            } else if (Calendar.DAY_OF_WEEK == 1 ||
                Calendar.DAY_OF_WEEK == 2 ||
                Calendar.DAY_OF_WEEK == 3 ||
                Calendar.DAY_OF_WEEK == 4
            ) {
                gregCalendar.add(Calendar.DATE, 1)
            } else {
                gregCalendar.add(Calendar.DATE, 3)
            }
            val date = gregCalendar.time.toString().split(" ")
            sharedViewModel.updateDate(
                splitCurrDate[0].toInt(),
                splitCurrDate[1].toInt(),
                splitCurrDate[2].toInt(),
                date[2].toInt(),
                sharedViewModel.parseMonthStringToInt(date[1]),
                date[5].toInt()
            )
            sharedViewModel.readMeetings(
                splitCurrDate[0].toInt(),
                splitCurrDate[1].toInt(),
                splitCurrDate[2].toInt(),
                requireContext()
            )
            sharedViewModel.liveMeetingsList.value = sharedViewModel.readMeetingsList()
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