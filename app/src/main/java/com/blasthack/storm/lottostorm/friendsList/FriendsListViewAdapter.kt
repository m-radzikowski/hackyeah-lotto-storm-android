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
        val coupon = friends[position]
        val random = Random()

        fun rand(from: Int, to: Int) : Int {
            return random.nextInt(to - from) + from
        }

        val names = listOf("Jan Szewiec", "Kamil Korczan", "Agnieszka Gackowska", "Karolina Drabs", "Dawid Podmma")
        holder.nameView.text = names.get(rand(0,5))


/*        if (participant.photoBlob != null) {
            val decodedByte = Base64.decode(participant.photoBlob, 0)
            val bm = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
            holder.photoView.setImageBitmap(bm)
        } else {
            holder.initialsView.text = participant.getInitials()
            holder.initialsView.visibility = View.VISIBLE
        }*/
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    inner class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var nameView: TextView = itemView.findViewById(R.id.lecturer_tv)
        var photoView: ImageView = itemView.findViewById(R.id.lecturer_iv)
        var initialsView: TextView = itemView.findViewById(R.id.initials_tv)
        var lecturerDescription: TextView = itemView.findViewById(R.id.lecturerDescription)


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