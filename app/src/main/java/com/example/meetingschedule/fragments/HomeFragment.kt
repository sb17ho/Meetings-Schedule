package com.example.meetingschedule.fragments

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var todaysDate: String

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
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBind = FragmentHomeBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)

        val getCurrDate: List<String> =
            Calendar.getInstance().time.toString()
                .split(" ")

        todaysDate = Calendar.getInstance().time.toString().split(" ")[2]

        //TODO DID THIS
        val listOfAllMeetingsIDs = sharedViewModel.getLastInsertRowId(requireContext())
        if (!listOfAllMeetingsIDs.isEmpty()) {
            MainActivity.column_id_counter =
                listOfAllMeetingsIDs[listOfAllMeetingsIDs.size - 1]._id + 1
        }

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
            override fun onItemDeleteListener(meetings: MeetingModelClass) {
                sharedViewModel.deleteSelectedMeeting(meetings)
                sharedViewModel.readMeetings(
                    splitCurrDate[0].toInt(),
                    splitCurrDate[1].toInt(),
                    splitCurrDate[2].toInt(),
                    requireContext()
                )
                sharedViewModel.liveMeetingsList.value = sharedViewModel.readMeetingsList()
            }

            override fun onDeleteContactListener(meetings: MeetingModelClass) {
                val modelClass = MeetingModelClass(
                    _id = meetings._id, //TODO: Did Change
                    name = meetings.name,
                    dd = meetings.dd,
                    mm = meetings.mm,
                    yy = meetings.yy,
                    startTime = meetings.startTime,
                    endTime = meetings.endTime,
                    contactName = "null",
                    contactNumber = "null"
                )

                sharedViewModel.updateSelectedMeeting(
                    meetings,
                    modelClass
                )

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

    private fun pickContact() {
        result.launch(
            Intent(Intent.ACTION_PICK, Uri.parse("content://contacts")).setType(
                ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            )
        )
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
            if (currDate.split(":")[0].toInt() == todaysDate.toInt()) {
                val splitCurrDate = currDate.split(":")

                val gregCalendar: Calendar = GregorianCalendar()
                val time = gregCalendar.time.toString().split(" ")

                if (time[0].lowercase() == "sat") {
                    gregCalendar.add(Calendar.DATE, 7) //SATURDAY
                } else if (time[0].lowercase() == "sun") {
                    gregCalendar.add(Calendar.DATE, 6) //SUNDAY
                } else if (time[0].lowercase() == "mon" ||
                    time[0].lowercase() == "tue" ||
                    time[0].lowercase() == "wed" ||
                    time[0].lowercase() == "thu"
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
            } else {
                Toast.makeText(
                    requireContext(),
                    "Only Today's Meetings can be pushed",
                    Toast.LENGTH_SHORT
                ).show()
            }
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