/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model.borrow

import com.viwath.srulibrarymobile.data.dto.BorrowDetailDto
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.BookQuantity
import com.viwath.srulibrarymobile.domain.model.BookTitle
import com.viwath.srulibrarymobile.domain.model.BorrowDate
import com.viwath.srulibrarymobile.domain.model.BorrowId
import com.viwath.srulibrarymobile.domain.model.GiveBackDate
import com.viwath.srulibrarymobile.domain.model.IsBringBack
import com.viwath.srulibrarymobile.domain.model.StudentId
import com.viwath.srulibrarymobile.domain.model.StudentName

data class Borrow(
    val borrowId: BorrowId?,
    val bookId: BookId,
    val bookTitle: BookTitle,
    val bookQuan: BookQuantity,
    val studentId: StudentId,
    val studentName: StudentName,
    val borrowDate: BorrowDate,
    val giveBackDate: GiveBackDate,
    val isBringBack: IsBringBack,
    var isExpanded: Boolean = false
)

fun BorrowDetailDto.toBorrow(): Borrow = Borrow(
    borrowId = borrowId,
    bookId = bookId,
    bookTitle = bookTitle,
    bookQuan = bookQuan,
    studentId = studentId,
    studentName = studentName,
    borrowDate = borrowDate,
    giveBackDate = giveBackDate,
    isBringBack = isBringBack,
    isExpanded = false
)
