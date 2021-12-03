package com.example.meetingschedule.util

import androidx.recyclerview.widget.DiffUtil
import com.example.meetingschedule.model.MeetingModelClass

class MeetingsDiffUtil(
    private val oldList: List<MeetingModelClass>,
    private val newList: List<MeetingModelClass>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}