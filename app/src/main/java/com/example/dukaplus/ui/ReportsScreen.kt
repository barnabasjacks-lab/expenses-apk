package com.example.dukaplus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dukaplus.data.SaleEntity
import com.example.dukaplus.data.SaleItemEntity
import com.example.dukaplus.model.toFormattedDate
import com.example.dukaplus.model.toTzs
import com.example.dukaplus.viewmodel.DukaPlusViewModel

@Composable
fun ReportsScreen(
    viewModel: DukaPlusViewModel,
    modifier: Modifier = Modifier,
    onViewReceipt: (SaleEntity) -> Unit
) {
    val isSwahili by viewModel.isSwahili.collectAsState()
    val sales by viewModel.allSales.collectAsState()
    val expenses by viewModel.allExpenses.collectAsState()

    var selectedPeriod by remember { mutableStateOf(0) } // 0: Leo (Today), 1: Wiki Hii (This Week), 2: Mwezi Huu (This Month)

    val periodSales = remember(sales, selectedPeriod) {
        val now = System.currentTimeMillis()
        val durationMs = when (selectedPeriod) {
            0 -> 86400000L // 1 day
            1 -> 86400000L * 7 // 7 days
            else -> 86400000L * 30 // 30 days
        }
        val startTime = now - durationMs
        sales.filter { it.timestamp >= startTime }
    }

    val periodExpenses = remember(expenses, selectedPeriod) {
        val now = System.currentTimeMillis()
        val durationMs = when (selectedPeriod) {
            0 -> 86400000L
            1 -> 86400000L * 7
            else -> 86400000L * 30
        }
        val startTime = now - durationMs
        expenses.filter { it.timestamp >= startTime }
    }

    val totalSales = periodSales.sumOf { it.totalAmount }
    val totalProfit = periodSales.sumOf { it.profit }
    val totalExpenses = periodExpenses.sumOf { it.amount }
    val netTakeHome = totalProfit - totalExpenses

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = if (isSwahili) "Ripoti za Mauzo & Faida" else "Sales & Profit Reports",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isSwahili) "Uchambuzi wa mapato, faida halisi na matumizi" else "Revenue, gross profit and expenses analysis",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Period Selector
        item {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                val periods = listOf(
                    if (isSwahili) "Leo" else "Today",
                    if (isSwahili) "Wiki Hii" else "This Week",
                    if (isSwahili) "Mwezi Huu" else "This Month"
                )
                periods.forEachIndexed { index, label ->
                    SegmentedButton(
                        selected = selectedPeriod == index,
                        onClick = { selectedPeriod = index },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = periods.size)
                    ) {
                        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // P&L Overview Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isSwahili) "FAIDA HALISI YA BIASHARA (Net Earnings)" else "NET BUSINESS PROFIT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = netTakeHome.toTzs(),
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        color = if (netTakeHome >= 0) DukaGreenPrimary else DukaRed
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(if (isSwahili) "Mauzo Yote" else "Gross Sales", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(totalSales.toTzs(), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column {
                            Text(if (isSwahili) "Faida ya Bidhaa" else "Gross Margin", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(totalProfit.toTzs(), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DukaTeal)
                        }
                        Column {
                            Text(if (isSwahili) "Matumizi" else "Expenses", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(totalExpenses.toTzs(), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DukaRed)
                        }
                    }
                }
            }
        }

        // Visual Comparison Bars
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isSwahili) "Mlinganyo wa Mauzo na Matumizi" else "Sales vs Expenses Ratio",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val maxVal = maxOf(totalSales, totalExpenses, 1.0)
                    val salesFraction = (totalSales / maxVal).toFloat().coerceIn(0.05f, 1f)
                    val expensesFraction = (totalExpenses / maxVal).toFloat().coerceIn(0.05f, 1f)

                    // Sales bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isSwahili) "Mauzo" else "Sales", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(totalSales.toTzs(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(salesFraction)
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(DukaGreenPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Expenses bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isSwahili) "Matumizi" else "Expenses", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(totalExpenses.toTzs(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(expensesFraction)
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(DukaRed)
                        )
                    }
                }
            }
        }

        // Sales History & Receipts List
        item {
            DukaSectionHeader(
                title = if (isSwahili) "Historia ya Risiti (${periodSales.size})" else "Receipts History (${periodSales.size})"
            )
        }

        if (periodSales.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isSwahili) "Hakuna mauzo katika kipindi hiki." else "No sales in this selected period.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(periodSales, key = { it.id }) { sale ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewReceipt(sale) }
                        .testTag("report_receipt_${sale.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(DukaGreenPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Receipt, contentDescription = null, tint = DukaGreenPrimary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "#${sale.receiptNumber}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${sale.timestamp.toFormattedDate()} • ${sale.paymentMethod}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (sale.customerName.isNotBlank()) {
                                    Text(
                                        text = "Mteja: ${sale.customerName}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = sale.totalAmount.toTzs(),
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = DukaGreenPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isSwahili) "Fungua Risiti" else "View Receipt",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
