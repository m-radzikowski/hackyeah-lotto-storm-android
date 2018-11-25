package com.blasthack.storm.lottostorm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.blasthack.storm.lottostorm.database.friend.Friend
import com.blasthack.storm.lottostorm.database.friend.FriendDao
import com.blasthack.storm.lottostorm.database.token.Token
import com.blasthack.storm.lottostorm.database.token.TokenDao

@Database(entities = [Friend::class, Token::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun friendDao(): FriendDao
    abstract fun tokenDao(): TokenDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "storm_db")
                        // TODO: remove this later
                        // this is for tests not to write migration between small db changes
                        .fallbackToDestructiveMigration()
                        .build()

    }
}
