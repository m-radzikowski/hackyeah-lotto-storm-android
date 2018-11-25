package com.blasthack.storm.lottostorm.friendsList

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blasthack.storm.lottostorm.R
import com.blasthack.storm.lottostorm.database.friend.Friend
import java.util.*


class FriendsListViewAdapter internal constructor(
    val context: Context,
    private val friends: List<Friend>
) : RecyclerView.Adapter<FriendsListViewAdapter.TitleViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleViewHolder {
        val view = mInflater.inflate(R.layout.i_friend, parent, false)
        return TitleViewHolder(view)
    }

    override fun onBindViewHolder(holder: TitleViewHolder, position: Int) {
        val friend = friends[position]
        val random = Random()

        fun rand(from: Int, to: Int) : Int {
            return random.nextInt(to - from) + from
        }

        rand(0,2)
        holder.nameView.text = friend.friendName


        holder.photoView.setImageResource(R.drawable.photo1)
        if (friend.friendId == 1){
            holder.photoView.setImageResource(R.drawable.photo2)
        }
        if (friend.friendId == 2){
            holder.photoView.setImageResource(R.drawable.photo3)
        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    inner class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var nameView: TextView = itemView.findViewById(R.id.lecturer_tv)
        var photoView: ImageView = itemView.findViewById(R.id.lecturer_iv)
        var initialsView: TextView = itemView.findViewById(R.id.initials_tv)


        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }
    }

    internal fun getItem(id: Int): Friend {
        return friends[id]
    }

    internal fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}