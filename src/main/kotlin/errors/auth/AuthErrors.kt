package com.bieniucieniu.errors.auth

class UnauthorizedException(message: String = "Unauthorized", cause: Throwable? = null) : Throwable(message, cause)
