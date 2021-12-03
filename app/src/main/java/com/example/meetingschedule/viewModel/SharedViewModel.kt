package com.example.meetingschedule.viewModel

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.meetingschedule.model.MeetingModelClass
import com.example.meetingschedule.sqlite.MyDatabaseHelper

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val READ_CONTACTS_PERMISSION = 777 // It's an arbitrary value (It can be anything)
    private var READ_PERMISSION_GRANTED = false
    private val meetingsList: ArrayList<MeetingModelClass> = ArrayList()
    val liveMeetingsList: MutableLiveData<ArrayList<MeetingModelClass>> = MutableLiveData()
    private val allMeetings: ArrayList<MeetingModelClass> = ArrayList()

    private val databaseHelper: MyDatabaseHelper by lazy {
        MyDatabaseHelper(application.applicationContext)
    }

    fun readContacts() {

    }

    fun setReadContactsPermission(readPermission: Boolean) {
        this.READ_PERMISSION_GRANTED = readPermission
    }

    fun getPermissionCode() = READ_CONTACTS_PERMISSION

    fun addMeetingsToDatabase(meetings: MeetingModelClass, context: Context) {
        databaseHelper.insertMeeting(meetings, context)
    }

    private fun storeDatabaseReadToListForCurrentDate(
        date: Int,
        month: Int,
        year: Int,
        context: Context
    ) {
        val cursor: Cursor = databaseHelper.readCurrentDateMeetings(date, month, year)
        meetingsList.clear()
        if (cursor.count == 0) {
            Toast.makeText(context, "No Meetings", Toast.LENGTH_SHORT)
                .show()
        } else {
            while (cursor.moveToNext()) {
                meetingsList.add(
                    MeetingModelClass(
                        name = cursor.getString(1),
                        dd = cursor.getInt(2),
                        mm = cursor.getInt(3),
                        yy = cursor.getInt(4),
                        startTime = cursor.getString(5),
                        endTime = cursor.getString(6),
                        contactName = cursor.getString(7),
                        contactID = cursor.getString(8),
                        contactNumber = cursor.getString(9)
                    )
                )
            }
        }
    }

    private fun storeDatabaseReadToListForAllMeetings(context: Context) {
        val cursor: Cursor = databaseHelper.readAllMeeting()
        allMeetings.clear()

        if (cursor.count == 0) {
            Toast.makeText(context, "No Meetings", Toast.LENGTH_SHORT)
                .show()
        } else {
            while (cursor.moveToNext()) {
                allMeetings.add(
                    MeetingModelClass(
                        name = cursor.getString(1),
                        dd = cursor.getInt(2),
                        mm = cursor.getInt(3),
                        yy = cursor.getInt(4),
                        startTime = cursor.getString(5),
                        endTime = cursor.getString(6),
                        contactName = cursor.getString(7),
                        contactID = cursor.getString(8),
                        contactNumber = cursor.getString(9)
                    )
                )
            }
        }
    }

    fun readMeetingsList(): ArrayList<MeetingModelClass> = meetingsList

    fun readMeetings(
        date: Int,
        month: Int,
        year: Int,
        context: Context
    ): ArrayList<MeetingModelClass> {
        storeDatabaseReadToListForCurrentDate(date, month, year, context)
        return meetingsList
    }

    fun readAllMeetings(context: Context): ArrayList<MeetingModelClass> {
        storeDatabaseReadToListForAllMeetings(context)
        return allMeetings
    }

    fun parseMonthIntToString(month: Int): String {
        return when (month) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sept"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> "Null"
        }
    }

    fun parseMonthStringToInt(month: String): Int {
        return when (month) {
            "Jan" -> 1
            "Feb" -> 2
            "Mar" -> 3
            "Apr" -> 4
            "May" -> 5
            "Jun" -> 6
            "Jul" -> 7
            "Aug" -> 8
            "Sept" -> 9
            "Oct" -> 10
            "Nov" -> 11
            "Dec" -> 12
            else -> -1
        }
    }
}