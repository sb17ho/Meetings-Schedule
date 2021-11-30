package com.example.meetingschedule.viewModel

import android.app.Application
import android.database.Cursor
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.meetingschedule.MainActivity
import com.example.meetingschedule.model.MeetingModelClass
import com.example.meetingschedule.sqlite.MyDatabaseHelper

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val READ_CONTACTS_PERMISSION = 777 // It's an arbitrary value (It can be anything)
    private var READ_PERMISSION_GRANTED = false
    private val meetingsList: ArrayList<MeetingModelClass> = ArrayList()

    private val databaseHelper: MyDatabaseHelper by lazy {
        MyDatabaseHelper(application.applicationContext)
    }

    fun readContacts() {

    }

    fun setReadContactsPermission(readPermission: Boolean) {
        this.READ_PERMISSION_GRANTED = readPermission
    }

    fun getPermissionCode() = READ_CONTACTS_PERMISSION

    fun addMeetingsToDatabase(meetings: MeetingModelClass) {
        databaseHelper.insertMeeting(meetings)
    }

    fun storeDatabaseReadToList(date: Int, month: Int, year: Int) {
        val cursor: Cursor = databaseHelper.readCurrentDateMeetings(date, month, year)
        if (cursor.count == 0) {
            Toast.makeText(MainActivity().applicationContext, "No Meetings", Toast.LENGTH_SHORT)
                .show()
        } else {
            while (cursor.moveToNext()) {
                meetingsList.add(
                    MeetingModelClass(
                        cursor.getString(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                    )
                )
            }
        }
    }
}