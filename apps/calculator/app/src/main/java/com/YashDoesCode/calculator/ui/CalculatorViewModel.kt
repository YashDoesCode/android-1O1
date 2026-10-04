package com.YashDoesCode.calculator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.YashDoesCode.calculator.domain.model.CalculationResult
import com.YashDoesCode.calculator.domain.model.CalculatorAction
import com.YashDoesCode.calculator.domain.model.Token
import com.YashDoesCode.calculator.domain.usecase.EvaluateExpressionUseCase
import com.YashDoesCode.calculator.domain.usecase.FormatDisplayUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorViewModel(
    private val evaluateExpressionUseCase: EvaluateExpressionUseCase = EvaluateExpressionUseCase(),
    private val formatDisplayUseCase: FormatDisplayUseCase = FormatDisplayUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> appendNumber(action.digit)
            CalculatorAction.Decimal -> appendDecimal()
            is CalculatorAction.Op -> appendOperator(action.operator)
            is CalculatorAction.Func -> appendFunction(action.function)
            is CalculatorAction.Const -> appendConstant(action.constant)
            CalculatorAction.OpenParen -> appendOpenParen()
            CalculatorAction.CloseParen -> appendCloseParen()
            CalculatorAction.Percentage -> appendPercentage()
            CalculatorAction.ToggleSign -> toggleSign()
            CalculatorAction.Delete -> deleteLast()
            CalculatorAction.Clear -> clearAll()
            CalculatorAction.ClearEntry -> clearEntry()
            CalculatorAction.Calculate -> calculateFinal()
            CalculatorAction.ToggleScientific -> toggleScientific()
        }
    }

    private fun appendNumber(digit: Int) {
        _uiState.update { current ->
            val newExpr = if (current.expression == "0") digit.toString() else current.expression + digit
            current.copy(
                expression = newExpr,
                errorMessage = null
            )
        }
        updatePreview()
    }

    private fun appendDecimal() {
        _uiState.update { current ->
            val expr = current.expression
            val lastNumberSegment = expr.takeLastWhile { it.isDigit() || it == '.' }
            if (lastNumberSegment.contains('.')) {
                current
            } else {
                val newExpr = if (expr.isEmpty() || !expr.last().isDigit()) "${expr}0." else "$expr."
                current.copy(expression = newExpr, errorMessage = null)
            }
        }
        updatePreview()
    }

    private fun appendOperator(op: Token.Operator) {
        val symbol = when (op) {
            Token.Operator.ADD -> " + "
            Token.Operator.SUBTRACT -> " - "
            Token.Operator.MULTIPLY -> " × "
            Token.Operator.DIVIDE -> " ÷ "
            Token.Operator.MODULO -> " % "
            Token.Operator.POWER -> " ^ "
            Token.Operator.FACTORIAL -> "!"
            Token.Operator.UNARY_MINUS -> "-"
        }
        _uiState.update { current ->
            val expr = current.expression.trimEnd()
            val newExpr = if (expr.isEmpty()) {
                if (op == Token.Operator.SUBTRACT) "-" else ""
            } else {
                expr + symbol
            }
            current.copy(expression = newExpr, errorMessage = null)
        }
        updatePreview()
    }

    private fun appendFunction(func: Token.Function) {
        val funcStr = when (func) {
            Token.Function.SIN -> "sin("
            Token.Function.COS -> "cos("
            Token.Function.TAN -> "tan("
            Token.Function.LN -> "ln("
            Token.Function.LOG10 -> "log("
            Token.Function.SQRT -> "√("
        }
        _uiState.update { current ->
            current.copy(expression = current.expression + funcStr, errorMessage = null)
        }
        updatePreview()
    }

    private fun appendConstant(constant: Token.Constant) {
        val constStr = when (constant) {
            Token.Constant.PI -> "π"
            Token.Constant.E -> "e"
        }
        _uiState.update { current ->
            current.copy(expression = current.expression + constStr, errorMessage = null)
        }
        updatePreview()
    }

    private fun appendOpenParen() {
        _uiState.update { current ->
            current.copy(expression = current.expression + "(", errorMessage = null)
        }
        updatePreview()
    }

    private fun appendCloseParen() {
        _uiState.update { current ->
            val openCount = current.expression.count { it == '(' }
            val closeCount = current.expression.count { it == ')' }
            if (openCount > closeCount) {
                current.copy(expression = current.expression + ")", errorMessage = null)
            } else {
                current
            }
        }
        updatePreview()
    }

    private fun appendPercentage() {
        _uiState.update { current ->
            if (current.expression.isNotEmpty() && current.expression.last().isDigit()) {
                current.copy(expression = current.expression + "%", errorMessage = null)
            } else {
                current
            }
        }
        updatePreview()
    }

    private fun toggleSign() {
        _uiState.update { current ->
            val expr = current.expression
            if (expr.isEmpty()) return@update current
            // If the expression starts with negative sign, strip it; otherwise prefix
            val newExpr = if (expr.startsWith("-")) {
                expr.substring(1)
            } else {
                "-$expr"
            }
            current.copy(expression = newExpr, errorMessage = null)
        }
        updatePreview()
    }

    private fun deleteLast() {
        _uiState.update { current ->
            val expr = current.expression
            if (expr.isEmpty()) return@update current
            val newExpr = if (expr.endsWith(" ")) {
                expr.dropLast(3)
            } else {
                expr.dropLast(1)
            }
            current.copy(expression = newExpr, errorMessage = null)
        }
        updatePreview()
    }

    private fun clearAll() {
        _uiState.update { current ->
            current.copy(
                expression = "",
                previewResult = "0",
                errorMessage = null
            )
        }
    }

    private fun clearEntry() {
        _uiState.update { current ->
            current.copy(
                expression = "",
                previewResult = "0",
                errorMessage = null
            )
        }
    }

    private fun calculateFinal() {
        val current = _uiState.value
        if (current.expression.isBlank()) return

        when (val result = evaluateExpressionUseCase(current.expression)) {
            is CalculationResult.Success -> {
                val newHistory = listOf(CalculationHistoryItem(current.expression, result.formatted)) + current.history.take(9)
                _uiState.update {
                    it.copy(
                        expression = result.formatted,
                        previewResult = result.formatted,
                        errorMessage = null,
                        history = newHistory
                    )
                }
            }
            is CalculationResult.Error.DivisionByZero -> {
                _uiState.update { it.copy(errorMessage = "Cannot divide by zero") }
            }
            is CalculationResult.Error.DomainError -> {
                _uiState.update { it.copy(errorMessage = "Domain error (invalid input)") }
            }
            is CalculationResult.Error.MismatchedParentheses -> {
                _uiState.update { it.copy(errorMessage = "Mismatched parentheses") }
            }
            is CalculationResult.Error.MalformedExpression,
            is CalculationResult.Error.Overflow,
            is CalculationResult.Error.Generic -> {
                _uiState.update { it.copy(errorMessage = "Invalid expression format") }
            }
        }
    }

    private fun toggleScientific() {
        _uiState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
    }

    private fun updatePreview() {
        val expr = _uiState.value.expression
        if (expr.isBlank()) {
            _uiState.update { it.copy(previewResult = "0") }
            return
        }
        when (val result = evaluateExpressionUseCase(expr)) {
            is CalculationResult.Success -> {
                _uiState.update { it.copy(previewResult = result.formatted) }
            }
            is CalculationResult.Error -> {
                // Keep the last valid preview or clear when midway typing
            }
        }
    }
}
