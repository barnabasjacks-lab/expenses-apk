package com.example.dukaplus.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dukaplus.data.StoreProfileEntity
import com.example.dukaplus.viewmodel.DukaPlusViewModel

@Composable
fun StoreSettingsDialog(
    viewModel: DukaPlusViewModel,
    onDismiss: () -> Unit
) {
    val storeProfile by viewModel.storeProfile.collectAsState()
    val isSwahili by viewModel.isSwahili.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var storeName by remember(storeProfile) { mutableStateOf(storeProfile?.storeName ?: "DukaPlus Mini Supermarket") }
    var phone by remember(storeProfile) { mutableStateOf(storeProfile?.phone ?: "+255 754 000 111") }
    var location by remember(storeProfile) { mutableStateOf(storeProfile?.location ?: "Kariakoo, Dar es Salaam") }
    var tinNumber by remember(storeProfile) { mutableStateOf(storeProfile?.tinNumber ?: "128-940-512") }
    var vrnNumber by remember(storeProfile) { mutableStateOf(storeProfile?.vrnNumber ?: "40-008921-X") }
    var receiptMessage by remember(storeProfile) { mutableStateOf(storeProfile?.receiptMessage ?: "Asante kwa kununua nasi! Karibu tena DukaPlus.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isSwahili) "Mipangilio ya Duka & Risiti" else "Store & Receipt Profile",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    label = { Text(if (isSwahili) "Jina la Duka / Biashara" else "Store Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isSwahili) "Namba ya Simu ya Duka" else "Store Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(if (isSwahili) "Mahali / Mtaa (Location)" else "Location / Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tinNumber,
                        onValueChange = { tinNumber = it },
                        label = { Text("TIN Number") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = vrnNumber,
                        onValueChange = { vrnNumber = it },
                        label = { Text("VRN Number") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = receiptMessage,
                    onValueChange = { receiptMessage = it },
                    label = { Text(if (isSwahili) "Ujumbe wa Chini ya Risiti" else "Receipt Footer Note") },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isSwahili) "Mandhari Meusi (Dark Mode)" else "Dark Theme")
                    Switch(checked = isDarkMode, onCheckedChange = { viewModel.toggleTheme() })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isSwahili) "Lugha ya Kiswahili" else "Kiswahili Language")
                    Switch(checked = isSwahili, onCheckedChange = { viewModel.toggleLanguage() })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = StoreProfileEntity(
                        id = 1,
                        storeName = storeName.trim(),
                        phone = phone.trim(),
                        location = location.trim(),
                        tinNumber = tinNumber.trim(),
                        vrnNumber = vrnNumber.trim(),
                        receiptMessage = receiptMessage.trim(),
                        currency = "TZS",
                        taxRatePercent = 18.0
                    )
                    viewModel.updateStoreProfile(updated)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DukaGreenPrimary)
            ) {
                Text(if (isSwahili) "Hifadhi" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isSwahili) "Ghairi" else "Cancel")
            }
        }
    )
}
