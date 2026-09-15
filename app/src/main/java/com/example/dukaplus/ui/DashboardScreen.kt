package com.example.dukaplus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.dukaplus.data.ProductEntity
import com.example.dukaplus.data.SaleEntity
import com.example.dukaplus.model.DashboardStats
import com.example.dukaplus.model.toFormattedDate
import com.example.dukaplus.model.toTzs
import com.example.dukaplus.viewmodel.DukaPlusViewModel

@Composable
fun DashboardScreen(
    viewModel: DukaPlusViewModel,
    modifier: Modifier = Modifier,
    onNavigateToPos: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToReports: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val isSwahili by viewModel.isSwahili.collectAsState()
    val stats by viewModel.dashboardStats.collectAsState()
    val lowStockList by viewModel.lowStockProducts.collectAsState()
    val recentSales by viewModel.allSales.collectAsState()
    val storeProfile by viewModel.storeProfile.collectAsState()

    var showRestockDialogFor by remember { mutableStateOf<ProductEntity?>(null) }
    var restockQtyText by remember { mutableStateOf("10") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp, top = 12.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DukaGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "D+",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = storeProfile?.storeName ?: "DukaPlus",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSwahili) "Offline-First • Iko Tayari" else "Offline-Ready • Synced",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row {
                    IconButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier.testTag("btn_toggle_lang")
                    ) {
                        Text(
                            text = if (isSwahili) "EN" else "SW",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp
                        )
                    }
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.testTag("btn_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Action Buttons Row (Mauzo Mapya, Weka Mzigo, Matumizi, Ripoti)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToPos,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("btn_quick_pos"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DukaGreenPrimary)
                ) {
                    Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSwahili) "Mauzo Mapya" else "New Sale",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                FilledTonalButton(
                    onClick = onNavigateToInventory,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("btn_quick_inventory"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSwahili) "Stoo / Bidhaa" else "Inventory",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Overview Metric Cards Grid
        item {
            DukaSectionHeader(
                title = if (isSwahili) "Muhtasari wa Biashara Leo" else "Today's Business Summary"
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatMetricCard(
                        title = if (isSwahili) "Mauzo ya Leo" else "Today's Sales",
                        value = stats.todaySalesTotal.toTzs(),
                        subtitle = "${stats.totalTransactionsCount} ${if (isSwahili) "miamala" else "orders"}",
                        icon = Icons.Default.AttachMoney,
                        iconBgColor = DukaGreenPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToReports
                    )
                    StatMetricCard(
                        title = if (isSwahili) "Faida Halisi Leo" else "Net Profit",
                        value = stats.todayProfitTotal.toTzs(),
                        subtitle = if (isSwahili) "Faida ya bidhaa" else "Gross margin",
                        icon = Icons.Default.TrendingUp,
                        iconBgColor = DukaTeal,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToReports
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatMetricCard(
                        title = if (isSwahili) "Matumizi ya Leo" else "Expenses",
                        value = stats.todayExpensesTotal.toTzs(),
                        subtitle = if (isSwahili) "Kodi, luku, nk" else "Rent, bills, etc",
                        icon = Icons.Default.ReceiptLong,
                        iconBgColor = DukaRed,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToExpenses
                    )
                    StatMetricCard(
                        title = if (isSwahili) "Thamani ya Stoo" else "Stock Value",
                        value = stats.totalInventoryValue.toTzs(),
                        subtitle = if (isSwahili) "Thamani ya kuuzia" else "Retail value",
                        icon = Icons.Default.Warehouse,
                        iconBgColor = DukaBlue,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToInventory
                    )
                }
            }
        }

        // Low Stock Alert Banner
        item {
            Spacer(modifier = Modifier.height(14.dp))
            if (lowStockList.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DukaGold.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = DukaGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSwahili) "Tahadhari ya Bidhaa Zilizopungua (${lowStockList.size})"
                                else "Low Stock Alerts (${lowStockList.size})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = if (isSwahili) "Bidhaa hizi ziko chini ya kiwango cha usalama. Bofya kuongeza mzigo."
                            else "These products are running out. Tap to restock.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                        )

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(lowStockList) { prod ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    shadowElevation = 2.dp,
                                    modifier = Modifier.clickable {
                                        showRestockDialogFor = prod
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = prod.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = if (isSwahili) "Zimebaki: ${prod.stockQuantity} ${prod.unit}"
                                                else "Remaining: ${prod.stockQuantity} ${prod.unit}",
                                                fontSize = 11.sp,
                                                color = DukaRed,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Icon(
                                            imageVector = Icons.Default.AddCircle,
                                            contentDescription = "Restock",
                                            tint = DukaGreenPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Debts Summary Banner
        item {
            if (stats.totalActiveDebts > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DukaPurple.copy(alpha = 0.12f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToExpenses() }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(DukaPurple),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isSwahili) "Daftari la Madeni ya Wateja" else "Customer Debts Ledger",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isSwahili) "Jumla unayodai: ${stats.totalActiveDebts.toTzs()}"
                                    else "Total receivable: ${stats.totalActiveDebts.toTzs()}",
                                    fontSize = 12.sp,
                                    color = DukaPurple,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        }

        // Recent Transactions
        item {
            Spacer(modifier = Modifier.height(14.dp))
            DukaSectionHeader(
                title = if (isSwahili) "Mauzo ya Hivi Karibuni" else "Recent Sales",
                actionText = if (isSwahili) "Tazama Yote" else "View All",
                onActionClick = onNavigateToReports
            )

            if (recentSales.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isSwahili) "Bado hujafanya mauzo leo." else "No sales recorded yet today.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                        TextButton(onClick = onNavigateToPos) {
                            Text(if (isSwahili) "Fanya Mauzo ya Kwanza" else "Make First Sale")
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    recentSales.take(5).forEach { sale ->
                        RecentSaleItemRow(sale = sale, isSwahili = isSwahili)
                    }
                }
            }
        }
    }

    // Restock Dialog
    showRestockDialogFor?.let { prod ->
        AlertDialog(
            onDismissRequest = { showRestockDialogFor = null },
            title = {
                Text(if (isSwahili) "Ongeza Mzigo: ${prod.name}" else "Restock: ${prod.name}")
            },
            text = {
                Column {
                    Text(
                        text = if (isSwahili) "Iliyopo sasa: ${prod.stockQuantity} ${prod.unit}"
                        else "Current stock: ${prod.stockQuantity} ${prod.unit}",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = restockQtyText,
                        onValueChange = { restockQtyText = it },
                        label = { Text(if (isSwahili) "Idadi ya kuongeza" else "Quantity to add") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = restockQtyText.toIntOrNull() ?: 0
                        if (qty > 0) {
                            viewModel.restockProduct(prod.id, qty)
                        }
                        showRestockDialogFor = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DukaGreenPrimary)
                ) {
                    Text(if (isSwahili) "Ongeza" else "Restock")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestockDialogFor = null }) {
                    Text(if (isSwahili) "Ghairi" else "Cancel")
                }
            }
        )
    }
}

@Composable
fun RecentSaleItemRow(sale: SaleEntity, isSwahili: Boolean) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
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
                    Text("🛍️", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "#${sale.receiptNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${sale.timestamp.toFormattedDate("HH:mm")} • ${sale.paymentMethod}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = sale.totalAmount.toTzs(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = DukaGreenPrimary
                )
                Text(
                    text = if (isSwahili) "Faida: +${sale.profit.toTzs()}" else "Profit: +${sale.profit.toTzs()}",
                    fontSize = 11.sp,
                    color = DukaTeal,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
