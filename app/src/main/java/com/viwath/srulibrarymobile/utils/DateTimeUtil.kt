/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils

import java.time.LocalDate


object DateTimeUtil {

    fun String.toLocalDate(): LocalDate {
        if (this.isEmpty()) return LocalDate.now()
        return LocalDate.parse(this)
    }

    fun borrowOverDueDate(borrowDate: LocalDate): Boolean {
        val currentDate = LocalDate.now()
        return borrowDate.plusDays(14) <= currentDate
    }
}