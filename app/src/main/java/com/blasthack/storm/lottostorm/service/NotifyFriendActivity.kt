package com.blasthack.storm.lottostorm.service

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.blasthack.storm.lottostorm.R
import com.blasthack.storm.lottostorm.database.AppDatabase
import com.blasthack.storm.lottostorm.database.friend.Friend
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_notify_friend.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class NotifyFriendActivity : AppCompatActivity() {

    private lateinit var friends: MutableList<Friend>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notify_friend)

        friends = mutableListOf()
        fetchData()

        addFriend.setOnClickListener {

            StormBackendService.create(StormRepository::class.java).find(FriendUserName(friendName.text.toString()))                .subscribeOn(
                Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d("sd", "Successfully registered new chat channel.")

                        Toast.makeText(this, it.id, Toast.LENGTH_LONG).show()
                        val friend = Friend(it.id.toInt(),friendName.text.toString())
                        AppDatabase.getInstance(applicationContext).friendDao().insert(friend)
                        if (!friends.contains(friend)){
                            friends.add(friend)
                            updateFriendList()
                        }

                    },
                    { _: Throwable? ->
                        Log.d("ssd", "Failed to register new chat channel!")

                        Toast.makeText(this, "Nie znaleziono użytkownika o takiej nazwie.", Toast.LENGTH_LONG).show()
                    }
                )
        }


    }

    private fun updateFriendList() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun fetchData() = runBlocking {
        val job = GlobalScope.launch {
            friends = AppDatabase.getInstance(applicationContext).friendDao().getFriends() as MutableList<Friend>
        }
        job.join()
    }



}
