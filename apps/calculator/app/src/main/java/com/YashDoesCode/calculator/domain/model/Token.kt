package com.YashDoesCode.calculator.domain.model

sealed interface Token {
    data class Number(val value: Double) : Token

    enum class Operator(
        val symbol: String,
        val precedence: Int,
        val isRightAssociative: Boolean = false
    ) : Token {
        ADD("+", 1),
        SUBTRACT("-", 1),
        MULTIPLY("×", 2),
        DIVIDE("÷", 2),
        MODULO("%", 2),
        POWER("^", 3, isRightAssociative = true),
        UNARY_MINUS("neg", 4, isRightAssociative = true),
        FACTORIAL("!", 4)
    }

    enum class Function(val symbol: String) : Token {
        SIN("sin"),
        COS("cos"),
        TAN("tan"),
        LN("ln"),
        LOG10("log"),
        SQRT("√")
    }

    sealed interface Parenthesis : Token {
        object Open : Parenthesis
        object Close : Parenthesis
    }

    enum class Constant(val symbol: String, val value: Double) : Token {
        PI("π", Math.PI),
        E("e", Math.E)
    }
}
