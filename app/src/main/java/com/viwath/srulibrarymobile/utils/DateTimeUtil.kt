/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils

import java.time.LocalDate


/**
 * `DateTimeUtil` is a utility object that provides helpful functions for working with dates,
 * specifically for library-related date calculations.
 */
object DateTimeUtil {

    fun String.toLocalDate(): LocalDate {
        if (this.isEmpty()) return LocalDate.now()
        return LocalDate.parse(this)
    }

    fun borrowOverDueDate(borrowDate: LocalDate, isExtend: Boolean): Boolean {
        val currentDate = LocalDate.now()
        val dueDate = if (!isExtend) borrowDate.plusDays(14) else borrowDate.plusDays(21)
        return dueDate <= currentDate
    }

    fun String.extendBorrowDate(): String{
        val date = this.toLocalDate()
        return date.plusDays(7).toString()
    }
}