package com.song2.chatting.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socketclient.R
import com.example.socketclient.data.ChatData

class ChatMyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val message: TextView = view.findViewById(R.id.tv_chatmyitem_contents)

    fun onBind(chatData: ChatData) {
        message.text = chatData.message
    }
}