/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.borrow_usecase

data class BorrowUseCase(
    val getAllBorrowUseCase: GetAllBorrowUseCase,
    val borrowBookUseCase: BorrowBookUseCase,
    val getActiveBorrowsDetailUseCase: GetActiveBorrowsDetailUseCase,
    val searchBorrowUseCase: SearchBorrowUseCase,
    val extendBorrowUseCase: ExtendBorrowUseCase,
    val returnBookUseCase: ReturnBookUseCase
)
