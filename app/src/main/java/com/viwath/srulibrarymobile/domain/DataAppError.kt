/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain

/**
 * Represents an error that occurred during data operations.
 * This interface serves as a sealed hierarchy for different types of data-related errors,
 * categorized as either remote (network-related) or local (device-related).
 *
 * Using a sealed interface ensures that all possible error types are known at compile time,
 * which can help in writing exhaustive and robust error-handling logic.
 */
sealed interface DataAppError : AppError{
    enum class Remote: DataAppError{
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        FORBIDDEN,
        UNKNOWN
    }
    enum class Local: DataAppError{
        DISK_FULL,
        UNKNOWN
    }
}