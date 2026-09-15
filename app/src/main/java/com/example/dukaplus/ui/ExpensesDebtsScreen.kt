package com.example.dukaplus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.dukaplus.data.DebtEntity
import com.example.dukaplus.data.ExpenseEntity
import com.example.dukaplus.model.toFormattedDate
import com.example.dukaplus.model.toTzs
import com.example.dukaplus.viewmodel.DukaPlusViewModel

@Composable
fun ExpensesDebtsScreen(
    viewModel: DukaPlusViewModel,
    modifier: Modifier = Modifier
) {
    val isSwahili by viewModel.isSwahili.collectAsState()
    val expenses by viewModel.allExpenses.collectAsState()
    val debts by viewModel.allDebts.collectAsState()

    var selectedSubTab by remember { mutableStateOf(0) } // 0: Matumizi (Expenses), 1: Madeni (Debts)

    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showAddDebtDialog by remember { mutableStateOf(false) }
    var debtForPayment by remember { mutableStateOf<DebtEntity?>(null) }

    val totalExpenses = expenses.sumOf { it.amount }
    val activeDebtsTotal = debts.filter { !it.isPaid }.sumOf { it.remainingAmount }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (selectedSubTab == 0) showAddExpenseDialog = true else showAddDebtDialog = true
                },
                containerColor = if (selectedSubTab == 0) DukaRed else DukaPurple,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = {
                    Text(
                        text = if (selectedSubTab == 0) {
                            if (isSwahili) "Rekodi Matumizi" else "Add Expense"
                        } else {
                            if (isSwahili) "Rekodi Deni Jipya" else "Add New Debt"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("fab_add_expense_debt")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Segmented Tabs
            TabRow(
                selectedTabIndex = selectedSubTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Tab(
                    selected = selectedSubTab == 0,
                    onClick = { selectedSubTab = 0 },
                    text = {
                        Text(
                            if (isSwahili) "Matumizi ya Duka" else "Store Expenses",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedSubTab == 1,
                    onClick = { selectedSubTab = 1 },
                    text = {
                        Text(
                            if (isSwahili) "Daftari la Madeni" else "Debts Ledger",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            if (selectedSubTab == 0) {
                // EXPENSES VIEW
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DukaRed.copy(alpha = 0.1f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isSwahili) "Jumla ya Matumizi Yote:" else "Total Expenses:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = totalExpenses.toTzs(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = DukaRed
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(DukaRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.White)
                        }
                    }
                }

                if (expenses.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isSwahili) "Hakuna matumizi yaliyorekodiwa." else "No expenses recorded yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 140.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(expenses, key = { it.id }) { exp ->
                            ExpenseRowItem(
                                expense = exp,
                                onDelete = { viewModel.deleteExpense(exp) }
                            )
                        }
                    }
                }
            } else {
                // DEBTS VIEW
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DukaPurple.copy(alpha = 0.1f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isSwahili) "Madeni Yanayodaiwa:" else "Total Active Receivables:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = activeDebtsTotal.toTzs(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = DukaPurple
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(DukaPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.White)
                        }
                    }
                }

                if (debts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isSwahili) "Hakuna madeni kwenye daftari." else "No customer debts registered.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 140.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(debts, key = { it.id }) { debt ->
                            DebtRowItem(
                                debt = debt,
                                isSwahili = isSwahili,
                                onReceivePayment = { debtForPayment = debt },
                                onDelete = { viewModel.deleteDebt(debt) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Expense Dialog
    if (showAddExpenseDialog) {
        AddExpenseDialog(
            isSwahili = isSwahili,
            onDismiss = { showAddExpenseDialog = false },
            onConfirm = { title, amount, cat, note ->
                viewModel.addExpense(title, amount, cat, note)
                showAddExpenseDialog = false
            }
        )
    }

    // Add Debt Dialog
    if (showAddDebtDialog) {
        AddDebtDialog(
            isSwahili = isSwahili,
            onDismiss = { showAddDebtDialog = false },
            onConfirm = { name, phone, amount, dueDate, notes ->
                viewModel.addDebt(name, phone, amount, dueDate, notes)
                showAddDebtDialog = false
            }
        )
    }

    // Record Partial/Full Debt Payment Dialog
    debtForPayment?.let { debt ->
        RecordDebtPaymentDialog(
            debt = debt,
            isSwahili = isSwahili,
            onDismiss = { debtForPayment = null },
            onConfirm = { paidAmount ->
                viewModel.recordDebtPayment(debt, paidAmount)
                debtForPayment = null
            }
        )
    }
}

@Composable
fun ExpenseRowItem(expense: ExpenseEntity, onDelete: () -> Unit) {
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
                        .background(DukaRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💸", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(expense.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = "${expense.category} • ${expense.timestamp.toFormattedDate("dd/MM HH:mm")}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (expense.note.isNotBlank()) {
                        Text(expense.note, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "-${expense.amount.toTzs()}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = DukaRed
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun DebtRowItem(
    debt: DebtEntity,
    isSwahili: Boolean,
    onReceivePayment: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (debt.isPaid) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(debt.customerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    if (debt.customerPhone.isNotBlank()) {
                        Text("Simu: ${debt.customerPhone}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (debt.isPaid) DukaGreenPrimary.copy(alpha = 0.15f) else DukaPurple.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (debt.isPaid) (if (isSwahili) "IMELIPWA" else "PAID")
                        else (if (isSwahili) "INADAIWA" else "ACTIVE"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (debt.isPaid) DukaGreenPrimary else DukaPurple,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isSwahili) "Jumla: ${debt.originalAmount.toTzs()}" else "Total: ${debt.originalAmount.toTzs()}",
                    fontSize = 12.sp
                )
                Text(
                    text = if (isSwahili) "Imelipwa: ${debt.paidAmount.toTzs()}" else "Paid: ${debt.paidAmount.toTzs()}",
                    fontSize = 12.sp,
                    color = DukaGreenPrimary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isSwahili) "Baki Inayodaiwa:" else "Balance Due:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = debt.remainingAmount.toTzs(),
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = if (debt.isPaid) Color.Gray else DukaPurple
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!debt.isPaid) {
                        Button(
                            onClick = onReceivePayment,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DukaGreenPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(if (isSwahili) "Lipa Malipo" else "Pay", fontSize = 12.sp)
                        }
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Gray)
                    }
                }
            }

            if (debt.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Kumbukumbu: ${debt.notes}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AddExpenseDialog(
    isSwahili: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("LUKU & Maji") }
    var note by remember { mutableStateOf("") }

    val categories = listOf(
        "Kodi & Fremu",
        "LUKU & Maji",
        "Usafiri & Mizigo",
        "Mishahara & Vibaruwa",
        "Leseni & TRA",
        "Mengineyo"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isSwahili) "Rekodi Matumizi Mapya" else "Record Store Expense", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isSwahili) "Maelezo ya Matumizi (Mfano: LUKU ya Duka)" else "Expense Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(if (isSwahili) "Kiasi cha Pesa (TZS)" else "Amount (TZS)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(if (isSwahili) "Chagua Aina ya Matumizi:" else "Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(if (isSwahili) "Maelezo ya ziada (Hiari)" else "Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amt > 0) {
                        onConfirm(title.trim(), amt, category, note.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DukaRed)
            ) {
                Text(if (isSwahili) "Hifadhi Matumizi" else "Save Expense")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isSwahili) "Ghairi" else "Cancel")
            }
        }
    )
}

@Composable
fun AddDebtDialog(
    isSwahili: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, Long, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isSwahili) "Rekodi Deni Jipya la Mteja" else "Record Customer Debt", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isSwahili) "Jina la Mteja *" else "Customer Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isSwahili) "Namba ya Simu" else "Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(if (isSwahili) "Kiasi Anachodaiwa (TZS) *" else "Debt Amount (TZS) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (isSwahili) "Bidhaa alizochukua / Ahadi ya kulipa" else "Notes / Due date promise") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && amt > 0) {
                        val dueDate = System.currentTimeMillis() + (86400000L * 7)
                        onConfirm(name.trim(), phone.trim(), amt, dueDate, notes.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DukaPurple)
            ) {
                Text(if (isSwahili) "Hifadhi Deni" else "Save Debt")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isSwahili) "Ghairi" else "Cancel")
            }
        }
    )
}

@Composable
fun RecordDebtPaymentDialog(
    debt: DebtEntity,
    isSwahili: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var paymentText by remember { mutableStateOf(debt.remainingAmount.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isSwahili) "Pokea Malipo ya Deni" else "Record Debt Payment", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "${debt.customerName} - ${if (isSwahili) "Baki:" else "Balance:"} ${debt.remainingAmount.toTzs()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = paymentText,
                    onValueChange = { paymentText = it },
                    label = { Text(if (isSwahili) "Kiasi Alicholipa (TZS)" else "Amount Paid (TZS)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = paymentText.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onConfirm(amt)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DukaGreenPrimary)
            ) {
                Text(if (isSwahili) "Weka Malipo" else "Confirm Payment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isSwahili) "Ghairi" else "Cancel")
            }
        }
    )
}
