package com.blasthack.storm.lottostorm.service

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.blasthack.storm.lottostorm.PreferencesHelper
import com.blasthack.storm.lottostorm.R
import com.blasthack.storm.lottostorm.database.AppDatabase
import com.blasthack.storm.lottostorm.database.friend.Friend
import com.blasthack.storm.lottostorm.friendsList.FriendsListViewAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_notify_friend.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class NotifyFriendActivity : AppCompatActivity(), FriendsListViewAdapter.ItemClickListener {

    private lateinit var friends: MutableList<Friend>
    private var myId = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notify_friend)
        val preferencesHelper = PreferencesHelper(this)

        myId = preferencesHelper.myId.toInt()

        friends = mutableListOf()
        fetchData()

        addFriend.setOnClickListener {
            addFriendDialog()
        }
        val layoutManager = LinearLayoutManager(this)
        var sceneAdapter = FriendsListViewAdapter(this.applicationContext, friends)
        sceneAdapter.setClickListener(this)


        list_rv.layoutManager = layoutManager
        list_rv.adapter = sceneAdapter


    }

    fun addFriendDialog() {
        val builder = AlertDialog.Builder(this)

        // Set the alert dialog title
        builder.setTitle("Dodaj znajomego")


        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.alert_dialog, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editText)
        editText.hint = "Podaj nazwę użytkownika"
        builder.setView(dialogLayout)
        // Display a negative button on alert dialog
        builder.setNegativeButton("Nie") { dialog, which ->
            //Toast.makeText(applicationContext, "Anulowano", Toast.LENGTH_SHORT).show()
        }
        builder.setPositiveButton("Tak") { dialog, which ->
            val text = editText.text.toString()
            StormBackendService.create(StormRepository::class.java).find(FriendUserName(text)).subscribeOn(
                Schedulers.io()
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d("sd", "Successfully registered new chat channel.")

                        //Toast.makeText(this, it.id, Toast.LENGTH_LONG).show()
                        val friend = Friend(it.id.toInt(), text)
                        saveFriend(friend)
                        if (!friends.contains(friend)) {
                            friends.add(friend)

                            empty.visibility = View.GONE
                            updateFriendList()
                        }

                    },
                    { _: Throwable? ->
                        Log.d("ssd", "Failed to register new chat channel!")

                        //Toast.makeText(this, "Nie znaleziono użytkownika o takiej nazwie.", Toast.LENGTH_LONG).show()
                    }
                )
        }
        val dialog: AlertDialog = builder.create()

        dialog.show()
    }

    override fun onItemClick(view: View, position: Int) {
        val item = (list_rv.adapter as FriendsListViewAdapter).getItem(position)
        val builder = AlertDialog.Builder(this)

        // Set the alert dialog title
        builder.setTitle("Wysłać powiadomienie?")

        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.alert_dialog, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editText)
        editText.maxLines = 7
        editText.minLines = 5
        editText.setText("Hej! Burza jest niedaleko Ciebie, wejdź do gry!")
        builder.setView(dialogLayout)
        // Display a negative button on alert dialog
        builder.setNegativeButton("Nie") { dialog, which ->
            //Toast.makeText(applicationContext, "Anulowano", Toast.LENGTH_SHORT).show()
        }
        builder.setPositiveButton("Tak") { dialog, which ->
            val text = editText.text.toString()
            val friendId = item.friendId.toString()
            val myId = myId.toString()
            StormBackendService.create(StormRepository::class.java)
                .sendNotification(NotifyFriendBody(myId, friendId, text)).subscribeOn(
                Schedulers.io()
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d("sd", "Successfully registered new chat channel.")
                        //Toast.makeText(this, "udało się", Toast.LENGTH_LONG).show()

                    },
                    { _: Throwable? ->
                        Log.d("ssd", "Failed to register new chat channel!")

                        //Toast.makeText(this, "Nie znaleziono użytkownika o takiej nazwie.", Toast.LENGTH_LONG).show()
                    }
                )
        }
        val dialog: AlertDialog = builder.create()

        dialog.show()
    }

    private fun updateFriendList() {
        var sceneAdapter = FriendsListViewAdapter(this.applicationContext, friends)
        sceneAdapter.setClickListener(this)

        list_rv.adapter = sceneAdapter
    }

    private fun fetchData() = runBlocking {
        val job = GlobalScope.launch {
            friends = AppDatabase.getInstance(applicationContext).friendDao().getFriends() as MutableList<Friend>

            if (friends.size > 0) {
                empty.visibility = View.GONE
            }
        }
        job.join()
    }

    private fun saveFriend(friend: Friend) = runBlocking {
        val job = GlobalScope.launch {
            AppDatabase.getInstance(applicationContext).friendDao().insert(friend)
        }
        job.join()
    }


}
