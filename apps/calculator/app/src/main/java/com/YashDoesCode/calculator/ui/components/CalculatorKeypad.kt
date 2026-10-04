package com.YashDoesCode.calculator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.YashDoesCode.calculator.domain.model.CalculatorAction
import com.YashDoesCode.calculator.domain.model.Token

@Composable
fun CalculatorKeypad(
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Row 1: AC, +/-, %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(
                text = "AC",
                style = ButtonStyle.ACTION,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Clear) }
            )
            CalculatorButton(
                text = "±",
                style = ButtonStyle.ACTION,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.ToggleSign) }
            )
            CalculatorButton(
                text = "%",
                style = ButtonStyle.ACTION,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Percentage) }
            )
            CalculatorButton(
                text = "÷",
                style = ButtonStyle.OPERATOR,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Op(Token.Operator.DIVIDE)) }
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(
                text = "7",
                style = ButtonStyle.NUMBER,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Number(7)) }
            )
            CalculatorButton(
                text = "8",
                style = ButtonStyle.NUMBER,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Number(8)) }
            )
            CalculatorButton(
                text = "9",
                style = ButtonStyle.NUMBER,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Number(9)) }
            )
            CalculatorButton(
                text = "×",
                style = ButtonStyle.OPERATOR,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Op(Token.Operator.MULTIPLY)) }
            )
        }

        // Row 3: 4, 5, 6, -
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(
                text = "4",
                style = ButtonStyle.NUMBER,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Number(4)) }
            )
            CalculatorButton(
                text = "5",
                style = ButtonStyle.NUMBER,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Number(5)) }
            )
            CalculatorButton(
                text = "6",
                style = ButtonStyle.NUMBER,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Number(6)) }
            )
            CalculatorButton(
                text = "-",
                style = ButtonStyle.OPERATOR,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Op(Token.Operator.SUBTRACT)) }
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(
                text = "1",
                style = ButtonStyle.NUMBER,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Number(1)) }
            )
            CalculatorButton(
                text = "2",
                style = ButtonStyle.NUMBER,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Number(2)) }
            )
            CalculatorButton(
                text = "3",
                style = ButtonStyle.NUMBER,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Number(3)) }
            )
            CalculatorButton(
                text = "+",
                style = ButtonStyle.OPERATOR,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Op(Token.Operator.ADD)) }
            )
        }

        // Row 5: ⌫, 0, ., =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(
                text = "⌫",
                style = ButtonStyle.ACTION,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Delete) }
            )
            CalculatorButton(
                text = "0",
                style = ButtonStyle.NUMBER,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Number(0)) }
            )
            CalculatorButton(
                text = ".",
                style = ButtonStyle.NUMBER,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Decimal) }
            )
            CalculatorButton(
                text = "=",
                style = ButtonStyle.ACCENT,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Calculate) }
            )
        }
    }
}
