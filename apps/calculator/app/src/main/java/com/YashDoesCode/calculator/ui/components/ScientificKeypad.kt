package com.YashDoesCode.calculator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.YashDoesCode.calculator.domain.model.CalculatorAction
import com.YashDoesCode.calculator.domain.model.Token

@Composable
fun ScientificKeypad(
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(
                text = "sin",
                style = ButtonStyle.FUNCTION,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Func(Token.Function.SIN)) }
            )
            CalculatorButton(
                text = "cos",
                style = ButtonStyle.FUNCTION,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Func(Token.Function.COS)) }
            )
            CalculatorButton(
                text = "tan",
                style = ButtonStyle.FUNCTION,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Func(Token.Function.TAN)) }
            )
            CalculatorButton(
                text = "π",
                style = ButtonStyle.FUNCTION,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Const(Token.Constant.PI)) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(
                text = "ln",
                style = ButtonStyle.FUNCTION,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Func(Token.Function.LN)) }
            )
            CalculatorButton(
                text = "log",
                style = ButtonStyle.FUNCTION,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Func(Token.Function.LOG10)) }
            )
            CalculatorButton(
                text = "√",
                style = ButtonStyle.FUNCTION,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Func(Token.Function.SQRT)) }
            )
            CalculatorButton(
                text = "e",
                style = ButtonStyle.FUNCTION,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Const(Token.Constant.E)) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(
                text = "(",
                style = ButtonStyle.FUNCTION,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.OpenParen) }
            )
            CalculatorButton(
                text = ")",
                style = ButtonStyle.FUNCTION,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.CloseParen) }
            )
            CalculatorButton(
                text = "^",
                style = ButtonStyle.FUNCTION,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Op(Token.Operator.POWER)) }
            )
            CalculatorButton(
                text = "x!",
                style = ButtonStyle.FUNCTION,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Op(Token.Operator.FACTORIAL)) }
            )
        }
    }
}
