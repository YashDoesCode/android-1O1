package com.keeper.components.fusion

object KotlinNullSafeGateway {

    fun sanitizeInput(input: String?): String {
        return input?.trim().orEmpty()
    }
}
