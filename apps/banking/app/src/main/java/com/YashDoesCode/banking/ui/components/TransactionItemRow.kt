package com.YashDoesCode.banking.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.YashDoesCode.banking.domain.model.Transaction
import com.YashDoesCode.banking.domain.model.TransactionType
import com.YashDoesCode.banking.domain.model.formatMoney
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionItemRow(
    transaction: Transaction,
    showRunningBalance: Boolean = true,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    val categoryIcon = getCategoryIcon(transaction.category.iconName)

    val (amountColor, amountPrefix) = when (transaction.type) {
        TransactionType.CREDIT -> Color(0xFF2E7D32) to "+"
        TransactionType.DEBIT -> MaterialTheme.colorScheme.onSurface to "-"
        TransactionType.TRANSFER -> Color(0xFF512DA8) to ""
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Icon Circle
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(transaction.category.colorHex).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryIcon,
                contentDescription = transaction.category.name,
                tint = Color(transaction.category.colorHex),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Merchant/Payee & Description/Time
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.merchantOrPayee,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = dateFormat.format(Date(transaction.timestamp)),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Amount & Running Balance
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$amountPrefix${formatMoney(transaction.amountMinor)}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
            )
            if (showRunningBalance && transaction.runningBalanceMinor > 0) {
                Text(
                    text = "Bal: ${formatMoney(transaction.runningBalanceMinor)}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

fun getCategoryIcon(iconName: String): ImageVector {
    return when (iconName) {
        "Restaurant" -> Icons.Default.Restaurant
        "ShoppingCart" -> Icons.Default.ShoppingCart
        "ShoppingBag" -> Icons.Default.ShoppingBag
        "DirectionsCar" -> Icons.Default.DirectionsCar
        "Movie" -> Icons.Default.Movie
        "Bolt" -> Icons.Default.Bolt
        "Payments" -> Icons.Default.Payments
        "SwapHoriz" -> Icons.Default.SwapHoriz
        else -> Icons.Default.Category
    }
}
