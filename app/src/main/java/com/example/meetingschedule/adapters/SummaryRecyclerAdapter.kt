package com.example.meetingschedule.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meetingschedule.databinding.AllMeetingsCardBinding
import com.example.meetingschedule.model.MeetingModelClass

class SummaryRecyclerAdapter : RecyclerView.Adapter<SummaryRecyclerAdapter.MyCustomAdapter>() {
    private var allMeetingsList: List<MeetingModelClass> = emptyList()

    class MyCustomAdapter(val summaryBind: AllMeetingsCardBinding) :
        RecyclerView.ViewHolder(summaryBind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCustomAdapter =
        MyCustomAdapter(
            AllMeetingsCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: MyCustomAdapter, position: Int) {
        holder.summaryBind.apply {
            meetingNameId.text = allMeetingsList[position].name
            val date =
                "${allMeetingsList[position].dd}/${allMeetingsList[position].mm}/${allMeetingsList[position].yy}"
            meetingDateId.text = date
            val meeting_time =
                "${allMeetingsList[position].startTime} - ${allMeetingsList[position].endTime}"
            meetingTimeId.text = meeting_time
        }
    }

    override fun getItemCount(): Int = allMeetingsList.size

    fun setAllMeetingsList(allMeetingsList: List<MeetingModelClass>) {
        this.allMeetingsList = allMeetingsList
        notifyDataSetChanged()
    }
}