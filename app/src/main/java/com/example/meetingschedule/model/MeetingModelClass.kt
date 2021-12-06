package com.example.meetingschedule.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeetingModelClass(
    val _id: Long,
    val name: String,
    val dd: Int,
    val mm: Int,
    val yy: Int,
    val startTime: String,
    val endTime: String,
    val contactName: String,
    val contactNumber: String
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MeetingModelClass

        if (name != other.name) return false
        if (dd != other.dd) return false
        if (mm != other.mm) return false
        if (yy != other.yy) return false
        if (startTime != other.startTime) return false
        if (endTime != other.endTime) return false
        if (contactName != other.contactName) return false
        if (contactNumber != other.contactNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + dd.hashCode()
        result = 31 * result + mm.hashCode()
        result = 31 * result + yy.hashCode()
        result = 31 * result + startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + contactName.hashCode()
        result = 31 * result + contactNumber.hashCode()
        return result
    }
}