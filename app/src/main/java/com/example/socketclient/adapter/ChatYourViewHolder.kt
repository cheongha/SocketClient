package com.song2.chatting.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socketclient.R
import com.example.socketclient.data.ChatData

class ChatYourViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val user: TextView = view.findViewById(R.id.tc_yourchatitem_user)
    val message: TextView = view.findViewById(R.id.tv_yourchatitem_message)

    fun onBind(chatData: ChatData) {
        message.text = chatData.message
        user.text = chatData.user
    }
}