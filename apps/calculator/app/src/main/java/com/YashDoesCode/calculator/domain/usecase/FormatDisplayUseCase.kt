package com.YashDoesCode.calculator.domain.usecase

import com.YashDoesCode.calculator.domain.evaluator.MathExpressionEvaluator

class FormatDisplayUseCase(
    private val evaluator: MathExpressionEvaluator = MathExpressionEvaluator()
) {
    operator fun invoke(value: Double): String {
        return evaluator.formatNumber(value)
    }
}
