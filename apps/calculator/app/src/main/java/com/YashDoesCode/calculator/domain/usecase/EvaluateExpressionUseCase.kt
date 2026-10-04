package com.YashDoesCode.calculator.domain.usecase

import com.YashDoesCode.calculator.domain.evaluator.MathExpressionEvaluator
import com.YashDoesCode.calculator.domain.model.CalculationResult

class EvaluateExpressionUseCase(
    private val evaluator: MathExpressionEvaluator = MathExpressionEvaluator()
) {
    operator fun invoke(expression: String): CalculationResult {
        return evaluator.evaluate(expression)
    }
}
