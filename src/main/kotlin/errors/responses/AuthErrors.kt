package com.bieniucieniu.errors.responses

class UnauthorizedException(message: String = "Unauthorized", cause: Throwable? = null) : Throwable(message, cause)
