/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.dto

import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.BookQuantity
import com.viwath.srulibrarymobile.domain.model.BookTitle
import com.viwath.srulibrarymobile.domain.model.BorrowDate
import com.viwath.srulibrarymobile.domain.model.BorrowId
import com.viwath.srulibrarymobile.domain.model.GiveBackDate
import com.viwath.srulibrarymobile.domain.model.IsBringBack
import com.viwath.srulibrarymobile.domain.model.StudentId
import com.viwath.srulibrarymobile.domain.model.StudentName

data class BorrowDetailDto(
    val borrowId: BorrowId?,
    val bookId: BookId,
    val bookTitle: BookTitle,
    val bookQuan: BookQuantity,
    val studentId: StudentId,
    val studentName: StudentName,
    val borrowDate: BorrowDate,
    val giveBackDate: GiveBackDate,
    val isBringBack: IsBringBack,
    val isExtend: Boolean
)