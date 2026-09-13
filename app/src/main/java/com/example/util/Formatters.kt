package com.example.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Formatters {
    private val tzsFormat = DecimalFormat("#,##0")

    fun formatTzs(amount: Double, includeSign: Boolean = false, isIncome: Boolean? = null): String {
        val formattedNumber = tzsFormat.format(Math.abs(amount))
        return when {
            includeSign && isIncome == true -> "+TZS $formattedNumber"
            includeSign && isIncome == false -> "-TZS $formattedNumber"
            amount < 0 -> "-TZS $formattedNumber"
            else -> "TZS $formattedNumber"
        }
    }

    fun formatDate(timeMillis: Long): String {
        val nowCal = Calendar.getInstance()
        val targetCal = Calendar.getInstance().apply { timeInMillis = timeMillis }

        val isToday = nowCal.get(Calendar.YEAR) == targetCal.get(Calendar.YEAR) &&
                nowCal.get(Calendar.DAY_OF_YEAR) == targetCal.get(Calendar.DAY_OF_YEAR)

        val isYesterday = nowCal.get(Calendar.YEAR) == targetCal.get(Calendar.YEAR) &&
                nowCal.get(Calendar.DAY_OF_YEAR) - targetCal.get(Calendar.DAY_OF_YEAR) == 1

        val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timeMillis))

        return when {
            isToday -> "Today, $timeStr"
            isYesterday -> "Yesterday, $timeStr"
            else -> SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault()).format(Date(timeMillis))
        }
    }

    fun formatDateShort(timeMillis: Long): String {
        return SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(timeMillis))
    }
}
