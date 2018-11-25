package com.blasthack.storm.lottostorm.database.token

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TokenDao {

    @Query("SELECT * FROM tokens WHERE id IS :tokenId")
    fun getToken(tokenId: Int): Token

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(friend: Token)

    @Query("DELETE FROM tokens")
    fun deleteAll()
}