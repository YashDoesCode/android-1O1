package com.keeper.components.fusion

class KotlinIdiomaticProcessor {

    fun filterAndTransform(inputs: List<String?>?): List<String> {
        return inputs?.asSequence()
            ?.filterNotNull()
            ?.filter { it.length > 3 }
            ?.map { it.trim().uppercase() }
            ?.toList() ?: emptyList()
    }
}
