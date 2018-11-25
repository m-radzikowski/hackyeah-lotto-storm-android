package com.blasthack.storm.lottostorm.database.token

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tokens")
data class Token(
        @PrimaryKey @ColumnInfo(name = "id") val tokenId: Int,
        val token: String
)