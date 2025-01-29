/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.borrow_usecase

data class BorrowUseCase(
    val borrowBookUseCase: BorrowBookUseCase,
    val getBorrowsUseCase: GetBorrowsUseCase
)
