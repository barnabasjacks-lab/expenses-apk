package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Categories
import com.example.data.TransactionEntity
import com.example.data.TransactionType
import com.example.ui.theme.ExpenseRedDark
import com.example.ui.theme.ExpenseRedLight
import com.example.ui.theme.IncomeGreenDark
import com.example.ui.theme.IncomeGreenLight
import com.example.ui.theme.ThemeMode
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ExpenseTrackerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSystemDark = isSystemInDarkTheme()
    val isEffectiveDark = when (uiState.themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemDark
    }

    val lang = uiState.language

    var showAddDialog by remember { mutableStateOf(false) }
    var addDialogInitialType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var transactionToEdit by remember { mutableStateOf<TransactionEntity?>(null) }
    var transactionToView by remember { mutableStateOf<TransactionEntity?>(null) }
    var showResetConfirmation by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showSearchField by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF007A3D).copy(alpha = 0.15f),
                            modifier = Modifier.padding(end = 10.dp)
                        ) {
                            Text(
                                text = "🇹🇿 TZS",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF007A3D),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Column {
                            Text(
                                text = AppStrings.appTitle(lang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = AppStrings.appSubtitle(lang),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Language Switcher button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clickable { viewModel.toggleLanguage() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Badili Lugha / Language",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (lang == AppLanguage.SW) "🇹🇿 SWA" else "🇬🇧 ENG",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    // Search button
                    IconButton(
                        onClick = {
                            showSearchField = !showSearchField
                            if (!showSearchField) {
                                viewModel.setSearchQuery("")
                            }
                        },
                        modifier = Modifier.testTag("search_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (showSearchField) Icons.Default.Clear else Icons.Default.Search,
                            contentDescription = if (showSearchField) AppStrings.clearSearch(lang) else "Tafuta"
                        )
                    }

                    // Dark mode toggle
                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier.testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isEffectiveDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Badili Theme"
                        )
                    }

                    // Overflow menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.testTag("more_options_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Machaguo zaidi"
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (lang == AppLanguage.SW) "Badili Lugha (English)" else "Switch to Kiswahili") },
                                onClick = {
                                    viewModel.toggleLanguage()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Language, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(AppStrings.resetData(lang)) },
                                onClick = {
                                    showMenu = false
                                    showResetConfirmation = true
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.DeleteSweep,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Optional Search bar
            if (showSearchField) {
                item(key = "search_field") {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_text_input"),
                        placeholder = { Text(AppStrings.searchPlaceholder(lang)) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = AppStrings.clearSearch(lang))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
            }

            // Hero Tanzanian Card
            item(key = "balance_card") {
                TanzaniaBalanceCard(
                    balance = uiState.totalBalance,
                    income = uiState.totalIncome,
                    expense = uiState.totalExpense,
                    lang = lang,
                    isDark = isEffectiveDark,
                    onAddIncome = {
                        addDialogInitialType = TransactionType.INCOME
                        transactionToEdit = null
                        showAddDialog = true
                    },
                    onAddExpense = {
                        addDialogInitialType = TransactionType.EXPENSE
                        transactionToEdit = null
                        showAddDialog = true
                    }
                )
            }

            // Quick Category Overview / Progress if expenses exist
            if (uiState.totalExpense > 0 && uiState.categoryExpenses.isNotEmpty()) {
                item(key = "top_expense_card") {
                    TopExpenseCategoriesCard(
                        categoryExpenses = uiState.categoryExpenses,
                        totalExpense = uiState.totalExpense,
                        lang = lang
                    )
                }
            }

            // Filter Tabs
            item(key = "filter_tabs") {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.transactionHistory(lang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.filteredTransactions.size} ${if (lang == AppLanguage.SW) "miamala" else "items"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = uiState.selectedFilter == FilterType.ALL,
                                onClick = { viewModel.setFilter(FilterType.ALL) },
                                label = { Text(AppStrings.all(lang)) },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = uiState.selectedFilter == FilterType.EXPENSES,
                                onClick = { viewModel.setFilter(FilterType.EXPENSES) },
                                label = { Text(AppStrings.expenses(lang)) },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (isEffectiveDark) ExpenseRedDark else ExpenseRedLight,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = uiState.selectedFilter == FilterType.INCOME,
                                onClick = { viewModel.setFilter(FilterType.INCOME) },
                                label = { Text(AppStrings.income(lang)) },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (isEffectiveDark) IncomeGreenDark else IncomeGreenLight,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Transactions list or empty state
            if (uiState.filteredTransactions.isEmpty()) {
                item(key = "empty_state") {
                    EmptyTransactionsView(
                        lang = lang,
                        onAddIncome = {
                            addDialogInitialType = TransactionType.INCOME
                            transactionToEdit = null
                            showAddDialog = true
                        },
                        onAddExpense = {
                            addDialogInitialType = TransactionType.EXPENSE
                            transactionToEdit = null
                            showAddDialog = true
                        }
                    )
                }
            } else {
                items(
                    items = uiState.filteredTransactions,
                    key = { it.id }
                ) { transaction ->
                    TransactionItemRow(
                        transaction = transaction,
                        lang = lang,
                        isDark = isEffectiveDark,
                        onClick = { transactionToView = transaction },
                        onEdit = {
                            transactionToEdit = transaction
                            showAddDialog = true
                        },
                        onDelete = { viewModel.deleteTransaction(transaction) }
                    )
                }
            }
        }
    }

    // Add / Edit Transaction Dialog
    if (showAddDialog) {
        AddEditTransactionDialog(
            initialType = addDialogInitialType,
            transactionToEdit = transactionToEdit,
            lang = lang,
            onDismiss = {
                showAddDialog = false
                transactionToEdit = null
            },
            onSave = { type, amount, category, description, dateMillis ->
                if (transactionToEdit == null) {
                    viewModel.addTransaction(type, amount, category, description, dateMillis)
                } else {
                    val updated = transactionToEdit!!.copy(
                        type = type,
                        amount = amount,
                        category = category,
                        description = description,
                        dateMillis = dateMillis
                    )
                    viewModel.updateTransaction(updated)
                }
                showAddDialog = false
                transactionToEdit = null
            }
        )
    }

    // Transaction Detail Dialog
    transactionToView?.let { tx ->
        TransactionDetailDialog(
            transaction = tx,
            onDismiss = { transactionToView = null },
            onEdit = {
                transactionToView = null
                transactionToEdit = tx
                showAddDialog = true
            },
            onDelete = {
                viewModel.deleteTransaction(tx)
                transactionToView = null
            }
        )
    }

    // Reset Confirmation Dialog
    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text(AppStrings.resetConfirmTitle(lang)) },
            text = { Text(AppStrings.resetConfirmSub(lang)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllTransactions()
                        showResetConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(AppStrings.deleteBtn(lang))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmation = false }) {
                    Text(AppStrings.cancelBtn(lang))
                }
            }
        )
    }
}

@Composable
fun TanzaniaBalanceCard(
    balance: Double,
    income: Double,
    expense: Double,
    lang: AppLanguage,
    isDark: Boolean,
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit
) {
    // Beautiful gradient inspired by Tanzanian modern financial aesthetic
    val gradient = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF0F3822),
                Color(0xFF062113),
                Color(0xFF132B1C)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF007A3D),
                Color(0xFF005C2E),
                Color(0xFF004422)
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppStrings.currentBalance(lang),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "TZS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Big Clean Balance
                Text(
                    text = Formatters.formatCurrency(balance),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Income & Expense Sub Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Income Box
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF22C55E),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = AppStrings.income(lang),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = Formatters.formatCurrency(income),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Expense Box
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFEF4444),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = AppStrings.expenses(lang),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = Formatters.formatCurrency(expense),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons (+ Income / - Expense)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onAddIncome,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("hero_add_income_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF22C55E),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppStrings.addIncome(lang),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = onAddExpense,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("hero_add_expense_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppStrings.addExpense(lang),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopExpenseCategoriesCard(
    categoryExpenses: Map<String, Double>,
    totalExpense: Double,
    lang: AppLanguage
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (lang == AppLanguage.SW) "Matumizi Makuu kwa Makundi" else "Top Expense Breakdown",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            val sortedCategories = categoryExpenses.toList()
                .sortedByDescending { it.second }
                .take(3)

            for ((category, amount) in sortedCategories) {
                val ratio = if (totalExpense > 0) (amount / totalExpense).toFloat() else 0f
                val percentage = (ratio * 100).toInt()

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${Formatters.formatCurrency(amount)} ($percentage%)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { ratio.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFFEF4444),
                        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItemRow(
    transaction: TransactionEntity,
    lang: AppLanguage,
    isDark: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val amountColor = if (isIncome) {
        if (isDark) IncomeGreenDark else IncomeGreenLight
    } else {
        if (isDark) ExpenseRedDark else ExpenseRedLight
    }

    val icon = Categories.getIconForCategory(transaction.category, transaction.type)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("transaction_item_${transaction.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon
            Surface(
                shape = CircleShape,
                color = amountColor.copy(alpha = 0.15f),
                modifier = Modifier.size(46.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = transaction.category,
                    tint = amountColor,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Transaction Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (transaction.description.isNotBlank()) {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = Formatters.formatDate(transaction.dateMillis),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount
            Text(
                text = (if (isIncome) "+ " else "- ") + Formatters.formatCurrency(transaction.amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                color = amountColor
            )
        }
    }
}

@Composable
fun EmptyTransactionsView(
    lang: AppLanguage,
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF007A3D).copy(alpha = 0.12f),
                modifier = Modifier.size(76.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Wallet,
                    contentDescription = null,
                    tint = Color(0xFF007A3D),
                    modifier = Modifier.padding(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = AppStrings.noTransactions(lang),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = AppStrings.noTransactionsSub(lang),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onAddIncome,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E))
                ) {
                    Text(AppStrings.addIncome(lang), fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onAddExpense,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text(AppStrings.addExpense(lang), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
