package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Categories
import com.example.data.TransactionEntity
import com.example.data.TransactionType
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.Formatters
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionDialog(
    initialType: TransactionType = TransactionType.EXPENSE,
    transactionToEdit: TransactionEntity? = null,
    lang: AppLanguage = AppLanguage.SW,
    onDismiss: () -> Unit,
    onSave: (type: TransactionType, amount: Double, category: String, description: String, dateMillis: Long) -> Unit
) {
    var selectedType by remember {
        mutableStateOf(transactionToEdit?.type ?: initialType)
    }
    var amountInput by remember {
        mutableStateOf(transactionToEdit?.let {
            if (it.amount % 1.0 == 0.0) it.amount.toLong().toString() else it.amount.toString()
        } ?: "")
    }
    var selectedCategory by remember {
        mutableStateOf(transactionToEdit?.category ?: "")
    }
    var selectedPaymentMethod by remember {
        mutableStateOf("")
    }
    var descriptionInput by remember {
        mutableStateOf(transactionToEdit?.description ?: "")
    }
    var dateMillis by remember {
        mutableLongStateOf(transactionToEdit?.dateMillis ?: System.currentTimeMillis())
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }

    val categories = if (selectedType == TransactionType.INCOME) {
        Categories.incomeCategories
    } else {
        Categories.expenseCategories
    }

    // Set default category if none chosen
    if (selectedCategory.isBlank() && categories.isNotEmpty()) {
        selectedCategory = categories.first().getDisplayName(lang)
    }

    val isIncome = selectedType == TransactionType.INCOME
    val activeColor = if (isIncome) Color(0xFF22C55E) else Color(0xFFEF4444)

    val quickAmounts = if (isIncome) {
        listOf(10000L, 50000L, 100000L, 200000L, 500000L, 1000000L)
    } else {
        listOf(1000L, 2000L, 5000L, 10000L, 20000L, 50000L)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 680.dp)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (transactionToEdit == null) {
                            AppStrings.newTransaction(lang, isIncome)
                        } else {
                            AppStrings.editTransaction(lang)
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = AppStrings.cancelBtn(lang))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Type selector: Mapato (Income) vs Matumizi (Expense)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Expense Tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedType = TransactionType.EXPENSE
                                selectedCategory = Categories.expenseCategories.first().getDisplayName(lang)
                            },
                        color = if (!isIncome) Color(0xFFEF4444) else Color.Transparent
                    ) {
                        Text(
                            text = AppStrings.expenses(lang),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (!isIncome) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    // Income Tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedType = TransactionType.INCOME
                                selectedCategory = Categories.incomeCategories.first().getDisplayName(lang)
                            },
                        color = if (isIncome) Color(0xFF22C55E) else Color.Transparent
                    ) {
                        Text(
                            text = AppStrings.income(lang),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isIncome) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amount input
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = {
                        val filtered = it.filter { ch -> ch.isDigit() || ch == '.' }
                        amountInput = filtered
                        amountError = false
                    },
                    label = { Text(AppStrings.amountLabel(lang)) },
                    prefix = {
                        Text(
                            "TZS ",
                            fontWeight = FontWeight.Bold,
                            color = activeColor
                        )
                    },
                    isError = amountError,
                    supportingText = {
                        if (amountError) {
                            Text(
                                if (lang == AppLanguage.SW) "Tafadhali ingiza kiasi sahihi" else "Please enter a valid amount",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("amount_input_field")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Amount Chips
                Text(
                    text = AppStrings.quickAmounts(lang),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (amount in quickAmounts) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                amountInput = amount.toString()
                                amountError = false
                            }
                        ) {
                            Text(
                                text = "${amount / 1000}k",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Selection
                Text(
                    text = AppStrings.categoryLabel(lang),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (cat in categories) {
                        val catName = cat.getDisplayName(lang)
                        val isSelected = selectedCategory == catName || selectedCategory == cat.swName || selectedCategory == cat.enName
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategory = catName
                                categoryError = false
                            },
                            label = { Text(catName) },
                            leadingIcon = {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = activeColor.copy(alpha = 0.15f),
                                selectedLabelColor = activeColor
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tanzanian Payment Method (M-Pesa, Tigo Pesa, Halopesa, CRDB/NMB, Cash)
                Text(
                    text = AppStrings.paymentMethodLabel(lang),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (method in AppStrings.paymentMethodsList(lang)) {
                        val isSelected = selectedPaymentMethod == method
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedPaymentMethod = if (isSelected) "" else method
                            },
                            label = { Text(method, style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                OutlinedTextField(
                    value = descriptionInput,
                    onValueChange = { descriptionInput = it },
                    label = { Text(AppStrings.noteLabel(lang)) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("description_input_field")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date Picker Button
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${AppStrings.dateLabel(lang)}: ${Formatters.formatDate(dateMillis)}")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons (Save & Cancel)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(AppStrings.cancelBtn(lang))
                    }

                    Button(
                        onClick = {
                            val parsedAmount = amountInput.toDoubleOrNull()
                            if (parsedAmount == null || parsedAmount <= 0.0) {
                                amountError = true
                                return@Button
                            }
                            if (selectedCategory.isBlank()) {
                                categoryError = true
                                return@Button
                            }

                            val finalDesc = if (selectedPaymentMethod.isNotBlank()) {
                                if (descriptionInput.isNotBlank()) {
                                    "$descriptionInput • $selectedPaymentMethod"
                                } else {
                                    selectedPaymentMethod
                                }
                            } else {
                                descriptionInput
                            }

                            onSave(selectedType, parsedAmount, selectedCategory, finalDesc, dateMillis)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_transaction_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = activeColor)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(AppStrings.saveBtn(lang), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Material 3 Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dateMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        dateMillis = it
                    }
                    showDatePicker = false
                }) {
                    Text(AppStrings.saveBtn(lang))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(AppStrings.cancelBtn(lang))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
