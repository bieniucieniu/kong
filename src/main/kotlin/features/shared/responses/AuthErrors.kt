package com.bieniucieniu.features.shared.responses

class UnauthorizedException(message: String = "Unauthorized", cause: Throwable? = null) : Throwable(message, cause)
