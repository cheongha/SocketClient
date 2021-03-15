package com.song2.chatting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socketclient.R
import com.example.socketclient.data.ChatData

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data = arrayListOf<ChatData>()

    private val MY_CHAT = 0
    private val YOUR_CHAT = 1
    private val OTHER_CHAT = 2

    override fun getItemViewType(position: Int): Int {
        val chatMessage = data[position]

        return if (chatMessage.id=="me") {
            MY_CHAT
        } else if (chatMessage.id == "you"){
            YOUR_CHAT
        } else {
            OTHER_CHAT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        lateinit var viewHolder: RecyclerView.ViewHolder

        return when (viewType) {
            MY_CHAT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rv_item_chat_my, parent, false)

                ChatMyViewHolder(view)
            }
            YOUR_CHAT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rv_item_chat_your, parent, false)

                ChatYourViewHolder(view)
            }
            OTHER_CHAT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rv_item_chat_other, parent, false)

                ChatOtherViewHolder(view)
            }
            else -> {
                viewHolder
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder){
            is ChatMyViewHolder -> {
                //스마트캐스트
                holder.onBind(data[position])
            }
            is ChatYourViewHolder -> {
                holder.onBind(data[position])
            }
            is ChatOtherViewHolder -> {
                holder.onBind(data[position])
            }
        }
    }

    fun addItem(item: ChatData) {
        data.add(item)
    }

}