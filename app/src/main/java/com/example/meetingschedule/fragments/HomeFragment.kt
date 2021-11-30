package com.example.meetingschedule.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.meetingschedule.MainActivity
import com.example.meetingschedule.R
import com.example.meetingschedule.adapters.HomeRecyclerAdapter
import com.example.meetingschedule.databinding.FragmentHomeBinding
import java.util.*


class HomeFragment : Fragment() {
    private lateinit var homeBind: FragmentHomeBinding
    private val recyclerAdapter: HomeRecyclerAdapter by lazy {
        HomeRecyclerAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBind = FragmentHomeBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)

        val currDate: Date =
            Calendar.getInstance().time //TODO: Use this to see which curr date it is and show meetings (Only when app is loaded)

        homeBind.apply {

            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                Log.w(
                    "DATE CHANGE",
                    dayOfMonth.toString()
                )
            }
        }

        return homeBind.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_button) {
            homeBind.root.findNavController()
                .navigate(R.id.addMeetingFragment)
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