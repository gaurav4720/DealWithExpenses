package com.example.dealwithexpenses.entities

import androidx.room.TypeConverter
import java.util.*

class DateConverter {

    @TypeConverter
    fun toDate(time: Long): Date {
        return Date(time)
    }

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }


}