package com.dentalcare.app.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
    private val timeFormat = SimpleDateFormat(Constants.TIME_FORMAT, Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat(Constants.DATETIME_FORMAT, Locale.getDefault())
    
    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
    
    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }
    
    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }
    
    fun formatTime(date: Date): String {
        return timeFormat.format(date)
    }
    
    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }
    
    fun formatDateTime(date: Date): String {
        return dateTimeFormat.format(date)
    }
    
    fun parseDate(dateString: String): Date? {
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    fun parseTime(timeString: String): Date? {
        return try {
            timeFormat.parse(timeString)
        } catch (e: Exception) {
            null
        }
    }
    
    fun isDateInPast(dateString: String): Boolean {
        val date = parseDate(dateString) ?: return false
        return date.before(Date())
    }
    
    fun isDateToday(dateString: String): Boolean {
        val date = parseDate(dateString) ?: return false
        val today = Calendar.getInstance()
        val checkDate = Calendar.getInstance().apply { time = date }
        
        return today.get(Calendar.YEAR) == checkDate.get(Calendar.YEAR) &&
               today.get(Calendar.DAY_OF_YEAR) == checkDate.get(Calendar.DAY_OF_YEAR)
    }
    
    fun getTodayString(): String {
        return formatDate(Date())
    }
    
    fun getCurrentTimeString(): String {
        return formatTime(Date())
    }
    
    fun combineDateAndTime(dateString: String, timeString: String): Long {
        return try {
            val dateTime = "$dateString $timeString"
            dateTimeFormat.parse(dateTime)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}
