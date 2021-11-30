package com.example.meetingschedule.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.meetingschedule.MainActivity
import com.example.meetingschedule.model.MeetingModelClass

class MyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "EmployeeDatabase.db"

        private const val TABLE_NAME = "meetings_table"
        private const val COLUMN_ID = "_id"
        private const val NAME_COLUMN = "meetings_name"
        private const val DATE_COLUMN = "meetings_date"
        private const val MONTH_COLUMN = "meetings_month"
        private const val YEAR_COLUMN = "meetings_year"
        private const val TIME_COLUMN = "meetings_time"
        private const val CONTACT_NAME_COLUMN = "meetings_contact_name"
        private const val CONTACT_ID_COLUMN = "meetings_contact_id"
        private const val CONTACT_NUMBER_COLUMN = "meetings_contact_number"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query: String =
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $NAME_COLUMN TEXT," +
                    " $DATE_COLUMN TEXT, $TIME_COLUMN TEXT, $CONTACT_ID_COLUMN TEXT, $CONTACT_NAME_COLUMN TEXT, $CONTACT_NUMBER_COLUMN TEXT)"

        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertMeeting(meeting: MeetingModelClass) {
        val sqlInstance: SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(NAME_COLUMN, meeting.name)
        contentValues.put(DATE_COLUMN, meeting.dd)
        contentValues.put(DATE_COLUMN, meeting.mm)
        contentValues.put(DATE_COLUMN, meeting.yy)
        contentValues.put(TIME_COLUMN, meeting.time)
        contentValues.put(CONTACT_ID_COLUMN, meeting.contactID)
        contentValues.put(CONTACT_NAME_COLUMN, meeting.contactName)
        contentValues.put(CONTACT_NUMBER_COLUMN, meeting.contactNumber)

        val databaseReturn: Long = sqlInstance.insert(TABLE_NAME, null, contentValues)
        if (databaseReturn.toInt() == -1) {
            Toast.makeText(MainActivity().applicationContext, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(MainActivity().applicationContext, "Contact Added", Toast.LENGTH_SHORT)
                .show()
        }
    }

    //TODO: Should I use Global Async?
    fun readCurrentDateMeetings(date: Int, month: Int, year: Int): Cursor {
        val dbRead = readableDatabase
        return dbRead.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $DATE_COLUMN = $date AND $MONTH_COLUMN = $month AND $YEAR_COLUMN = $year",
            null
        )
    }

    fun readCurrentMonthMeetings(date: Int, month: Int, year: Int): Cursor {
        val dbRead = readableDatabase
        return dbRead.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $MONTH_COLUMN = $month AND $YEAR_COLUMN = $year",
            null
        )
    }
}