/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.borrow_usecase

/**
 * `BorrowUseCase` is a data class that encapsulates all the use cases related to borrowing books.
 * It acts as a single point of access for various borrowing operations within the application.
 *
 * This class aggregates several specialized use cases, each responsible for a specific aspect
 * of the borrowing process, such as fetching borrow data, initiating a borrow, managing active
 * borrows, searching, extending, and returning books.
 *
 * @property getAllBorrowUseCase A use case responsible for retrieving all borrow records.
 * @property borrowBookUseCase A use case responsible for initiating the borrowing process of a book.
 * @property getActiveBorrowsDetailUseCase A use case responsible for fetching detailed information
 *                                        about currently active borrows.
 * @property searchBorrowUseCase A use case responsible for searching for borrow records based on
 *                              specific criteria.
 * @property extendBorrowUseCase A use case responsible for extending the due date of a borrowed book.
 * @property returnBookUseCase A use case responsible for handling the return of a borrowed book.
 */
data class BorrowUseCase(
    val getAllBorrowUseCase: GetAllBorrowUseCase,
    val borrowBookUseCase: BorrowBookUseCase,
    val getActiveBorrowsDetailUseCase: GetActiveBorrowsDetailUseCase,
    val searchBorrowUseCase: SearchBorrowUseCase,
    val extendBorrowUseCase: ExtendBorrowUseCase,
    val returnBookUseCase: ReturnBookUseCase
)
