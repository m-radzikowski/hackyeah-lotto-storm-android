package com.blasthack.storm.lottostorm.database.friend

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FriendDao {
    @Query("SELECT * FROM friends")
    fun getFriends(): List<Friend>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(friends: List<Friend>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(friend: Friend)

    @Query("DELETE FROM friends")
    fun deleteAll()
}