package com.example.meetingschedule.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.meetingschedule.R
import com.example.meetingschedule.databinding.MeetingsCardViewBinding
import com.example.meetingschedule.fragments.HomeFragmentDirections
import com.example.meetingschedule.model.MeetingModelClass

class HomeRecyclerAdapter : RecyclerView.Adapter<HomeRecyclerAdapter.MyRecyclerAdapter>() {

    var meetings_list: List<MeetingModelClass> = emptyList()

    inner class MyRecyclerAdapter(val meetingsCardBind: MeetingsCardViewBinding) :
        RecyclerView.ViewHolder(meetingsCardBind.root) {
        init {
            meetingsCardBind.additionalFunctionsDropdown.setOnClickListener {
                val optionsMenu = PopupMenu(it.context, it)
                optionsMenu.inflate(R.menu.popup_meetings_card)

                optionsMenu.setOnMenuItemClickListener { menuItem ->

                    when (menuItem.itemId) {
                        R.id.delete_current_meeting -> {
                            itemOnClick.onItemDeleteListener(meetings_list[adapterPosition])
                            true
                        }
                        R.id.delete_contact_pop -> {
                            itemOnClick.onDeleteContactListener(meetings_list[adapterPosition])
                            true
                        }
                        else -> {
                            false
                        }
                    }

                }
                optionsMenu.show()
            }
        }
    }

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

            meetingViewCard.setOnClickListener {
                root.findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToUpdateFragment(meetings_list[position])
                )
            }
        }
    }

    override fun getItemCount(): Int = meetings_list.size

    fun setMeetingsList(meetings_list: List<MeetingModelClass>) {
        this.meetings_list = meetings_list
        notifyDataSetChanged()
    }

    lateinit var itemOnClick: OnItemClickListener

    interface OnItemClickListener {
        fun onItemDeleteListener(
            meetings: MeetingModelClass
        )

        fun onDeleteContactListener(
            meetings: MeetingModelClass
        )
    }

    fun setOnItemClickListener(listenerUI: OnItemClickListener) {
        this.itemOnClick = listenerUI
    }
}

//            meetingsCardBind.deleteImage.setOnClickListener {
//                itemOnClick.onItemClickListener(meetings_list[adapterPosition])
//            }