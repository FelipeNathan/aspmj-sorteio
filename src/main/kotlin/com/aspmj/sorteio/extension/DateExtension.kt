package com.aspmj.sorteio.extension

import java.util.*

fun Date?.maxTime(): Date {
    val calendar = Calendar.getInstance()

    calendar.time = this ?: Date()
    calendar.set(Calendar.HOUR, calendar.getActualMaximum(Calendar.HOUR_OF_DAY))
    calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE))
    calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND))

    return calendar.time
}