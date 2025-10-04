package com.YashDoesCode.banking.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.YashDoesCode.banking.domain.model.Account
import com.YashDoesCode.banking.domain.model.AccountType

@Composable
fun AccountCard(
    account: Account,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val (gradientColors, icon) = when (account.type) {
        AccountType.CHECKING -> listOf(Color(0xFF1B5E20), Color(0xFF2E7D32)) to Icons.Default.AccountBalance
        AccountType.SAVINGS -> listOf(Color(0xFF004D40), Color(0xFF00796B)) to Icons.Default.Savings
        AccountType.CREDIT_CARD -> listOf(Color(0xFF37474F), Color(0xFF546E7A)) to Icons.Default.CreditCard
        AccountType.INVESTMENT -> listOf(Color(0xFF311B92), Color(0xFF512DA8)) to Icons.Default.TrendingUp
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(gradientColors))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row: Account Name & Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = account.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = account.accountNumber,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.7f),
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = account.type.name,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Bottom row: Account Type label & Formatted Balance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(bottom = 2.dp)
                    ) {
                        Text(
                            text = account.type.name.replace("_", " "),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Balance",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        )
                        Text(
                            text = account.formattedBalance,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}
