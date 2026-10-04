package com.YashDoesCode.calculator.ui

data class CalculatorUiState(
    val expression: String = "",
    val previewResult: String = "0",
    val isScientificExpanded: Boolean = false,
    val errorMessage: String? = null,
    val history: List<CalculationHistoryItem> = emptyList()
)

data class CalculationHistoryItem(
    val expression: String,
    val result: String
)
