package com.YashDoesCode.calculator.domain.model

sealed interface CalculatorAction {
    data class Number(val digit: Int) : CalculatorAction
    data object Decimal : CalculatorAction
    data class Op(val operator: Token.Operator) : CalculatorAction
    data class Func(val function: Token.Function) : CalculatorAction
    data class Const(val constant: Token.Constant) : CalculatorAction
    data object OpenParen : CalculatorAction
    data object CloseParen : CalculatorAction
    data object Clear : CalculatorAction
    data object ClearEntry : CalculatorAction
    data object Delete : CalculatorAction
    data object Calculate : CalculatorAction
    data object ToggleSign : CalculatorAction
    data object ToggleScientific : CalculatorAction
    data object Percentage : CalculatorAction
}
