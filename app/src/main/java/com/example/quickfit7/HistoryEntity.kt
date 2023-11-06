package com.example.quickfit7

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history-table")
data class HistoryEntity(
    @PrimaryKey
    val date:String  //precise to every millisecond , hence pk,
)