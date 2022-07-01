package com.example.dealwithexpenses.entities

import androidx.room.TypeConverter
import java.util.*

class DateConverter {

    //converting type of date from long to date
    @TypeConverter
    fun toDate(time: Long): Date {
        return Date(time)
    }

    //converting type of date from date to long
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }


}