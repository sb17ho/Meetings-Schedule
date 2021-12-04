package com.example.meetingschedule.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meetingschedule.databinding.MeetingsCardViewBinding
import com.example.meetingschedule.model.MeetingModelClass

class HomeRecyclerAdapter : RecyclerView.Adapter<HomeRecyclerAdapter.MyRecyclerAdapter>() {

    var meetings_list: List<MeetingModelClass> = emptyList()

    class MyRecyclerAdapter(val meetingsCardBind: MeetingsCardViewBinding) :
        RecyclerView.ViewHolder(meetingsCardBind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerAdapter =
        MyRecyclerAdapter(
            MeetingsCardViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: MyRecyclerAdapter, position: Int) {
        holder.meetingsCardBind.apply {
            meetingNameId.text = meetings_list[position].name
            val meeting_time =
                "${meetings_list[position].startTime} - ${meetings_list[position].endTime}"
            meetingTimeId.text = meeting_time
        }
    }

    override fun getItemCount(): Int = meetings_list.size

    fun setMeetingsList(meetings_list: List<MeetingModelClass>) {
        this.meetings_list = meetings_list
        notifyDataSetChanged()
    }
}
