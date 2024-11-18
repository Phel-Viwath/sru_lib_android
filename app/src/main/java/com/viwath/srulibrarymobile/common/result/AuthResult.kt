package com.viwath.srulibrarymobile.common.result

sealed class AuthResult<T> (val data: T?= null) {
    class Authorize<T>(data: T? = null): AuthResult<T>(data)
    class Unauthorized<T>: AuthResult<T>()
    class InternalServerError<T>: AuthResult<T>()
    class UnknownError<T>: AuthResult<T>()
    class BadRequest<T>: AuthResult<T>()
}
