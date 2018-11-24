package com.blasthack.storm.lottostorm.database.friend

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class Friend(
        @PrimaryKey @ColumnInfo(name = "id") val friendId: Int,
        val friendName: String
)