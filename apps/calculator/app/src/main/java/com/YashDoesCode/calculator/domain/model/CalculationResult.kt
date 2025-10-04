package com.YashDoesCode.calculator.domain.model

sealed interface CalculationResult {
    data class Success(val value: Double, val formatted: String) : CalculationResult

    sealed interface Error : CalculationResult {
        data object DivisionByZero : Error
        data object DomainError : Error
        data object MismatchedParentheses : Error
        data object MalformedExpression : Error
        data object Overflow : Error
        data class Generic(val message: String) : Error
    }
}
