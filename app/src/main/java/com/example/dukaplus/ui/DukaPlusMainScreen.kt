package com.example.dukaplus.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dukaplus.data.SaleEntity
import com.example.dukaplus.data.SaleItemEntity
import com.example.dukaplus.viewmodel.DukaPlusViewModel

data class DukaNavTab(
    val titleSwahili: String,
    val titleEnglish: String,
    val icon: ImageVector,
    val tag: String
)

@Composable
fun DukaPlusMainScreen(
    viewModel: DukaPlusViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isSwahili by viewModel.isSwahili.collectAsState()
    val storeProfile by viewModel.storeProfile.collectAsState()

    var activeReceiptSale by remember { mutableStateOf<SaleEntity?>(null) }
    var activeReceiptItems by remember { mutableStateOf<List<SaleItemEntity>>(emptyList()) }
    var showStoreSettings by remember { mutableStateOf(false) }

    val navTabs = listOf(
        DukaNavTab("Dashibodi", "Dashboard", Icons.Default.Dashboard, "tab_dashboard"),
        DukaNavTab("Mauzo", "POS", Icons.Default.PointOfSale, "tab_pos"),
        DukaNavTab("Stoo", "Stock", Icons.Default.Inventory2, "tab_inventory"),
        DukaNavTab("Matumizi", "Expenses", Icons.Default.AccountBalanceWallet, "tab_expenses"),
        DukaNavTab("Ripoti", "Reports", Icons.Default.BarChart, "tab_reports")
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("dukaplus_bottom_nav")
            ) {
                navTabs.forEachIndexed { index, tab ->
                    val isSelected = currentTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(index) },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.titleEnglish
                            )
                        },
                        label = {
                            Text(
                                text = if (isSwahili) tab.titleSwahili else tab.titleEnglish,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DukaGreenPrimary,
                            selectedTextColor = DukaGreenPrimary,
                            indicatorColor = DukaGreenPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToPos = { viewModel.setTab(1) },
                    onNavigateToInventory = { viewModel.setTab(2) },
                    onNavigateToExpenses = { viewModel.setTab(3) },
                    onNavigateToReports = { viewModel.setTab(4) },
                    onOpenSettings = { showStoreSettings = true }
                )
                1 -> PosScreen(
                    viewModel = viewModel,
                    onSaleCompleted = { sale, items ->
                        activeReceiptSale = sale
                        activeReceiptItems = items
                    }
                )
                2 -> InventoryScreen(
                    viewModel = viewModel
                )
                3 -> ExpensesDebtsScreen(
                    viewModel = viewModel
                )
                4 -> ReportsScreen(
                    viewModel = viewModel,
                    onViewReceipt = { sale ->
                        activeReceiptSale = sale
                        activeReceiptItems = listOf(
                            SaleItemEntity(
                                saleId = sale.id,
                                productId = 0,
                                productName = "Mauzo ya Jumla (#${sale.receiptNumber})",
                                quantity = 1,
                                unitPrice = sale.totalAmount,
                                costPrice = sale.totalCost,
                                totalPrice = sale.totalAmount
                            )
                        )
                    }
                )
            }
        }
    }

    // Receipt Pop-up Dialog
    activeReceiptSale?.let { sale ->
        ReceiptDialog(
            sale = sale,
            items = activeReceiptItems,
            storeProfile = storeProfile,
            isSwahili = isSwahili,
            onDismiss = { activeReceiptSale = null }
        )
    }

    // Store Settings Dialog
    if (showStoreSettings) {
        StoreSettingsDialog(
            viewModel = viewModel,
            onDismiss = { showStoreSettings = false }
        )
    }
}
