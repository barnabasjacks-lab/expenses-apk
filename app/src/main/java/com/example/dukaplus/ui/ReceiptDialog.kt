package com.example.dukaplus.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.dukaplus.data.SaleEntity
import com.example.dukaplus.data.SaleItemEntity
import com.example.dukaplus.data.StoreProfileEntity
import com.example.dukaplus.model.toFormattedDate
import com.example.dukaplus.model.toTzs

@Composable
fun ReceiptDialog(
    sale: SaleEntity,
    items: List<SaleItemEntity>,
    storeProfile: StoreProfileEntity?,
    isSwahili: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val storeName = storeProfile?.storeName ?: "DukaPlus Mini Supermarket"
    val storePhone = storeProfile?.phone ?: "+255 754 000 111"
    val storeLocation = storeProfile?.location ?: "Kariakoo, Dar es Salaam"
    val tinNumber = storeProfile?.tinNumber ?: "128-940-512"
    val vrnNumber = storeProfile?.vrnNumber ?: "40-008921-X"
    val receiptMessage = storeProfile?.receiptMessage ?: "Asante kwa kununua nasi! Karibu tena DukaPlus."

    val receiptText = buildString {
        appendLine("================================")
        appendLine(storeName.uppercase())
        appendLine(storeLocation)
        appendLine("Simu: $storePhone")
        appendLine("TIN: $tinNumber | VRN: $vrnNumber")
        appendLine("--------------------------------")
        appendLine("RISITI YA MAUZO: #${sale.receiptNumber}")
        appendLine("Tarehe: ${sale.timestamp.toFormattedDate()}")
        appendLine("Malipo: ${sale.paymentMethod}")
        if (sale.customerName.isNotBlank()) {
            appendLine("Mteja: ${sale.customerName} (${sale.customerPhone})")
        }
        appendLine("--------------------------------")
        appendLine("BIDHAA               BEI   JUMLA")
        for (item in items) {
            appendLine("${item.productName} x${item.quantity} = ${item.totalPrice.toTzs()}")
        }
        appendLine("--------------------------------")
        appendLine("Punguzo (Discount): ${sale.discountAmount.toTzs()}")
        appendLine("Kodi (VAT 18%): ${sale.taxAmount.toTzs()}")
        appendLine("JUMLA KUU: ${sale.totalAmount.toTzs()}")
        appendLine("================================")
        appendLine(receiptMessage)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("receipt_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header badge
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(DukaGreenPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = DukaGreenPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isSwahili) "RISITI YA MAUZO" else "SALES RECEIPT",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "#${sale.receiptNumber}",
                    style = MaterialTheme.typography.labelMedium,
                    color = DukaGreenPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Receipt Slip Canvas / Paper Style
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = storeName,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = storeLocation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Simu: $storePhone",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "TIN: $tinNumber • VRN: $vrnNumber",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = sale.timestamp.toFormattedDate(),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Njia: ${sale.paymentMethod}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DukaGreenPrimary
                            )
                        }

                        if (sale.customerName.isNotBlank()) {
                            Text(
                                text = "Mteja: ${sale.customerName} (${sale.customerPhone})",
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        // Items list
                        for (item in items) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.productName,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${item.quantity} x ${item.unitPrice.toTzs()}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = item.totalPrice.toTzs(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        // Totals
                        if (sale.discountAmount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isSwahili) "Punguzo (Discount):" else "Discount:",
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "-${sale.discountAmount.toTzs()}",
                                    fontSize = 12.sp,
                                    color = DukaRed
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Kodi (VAT 18%):",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = sale.taxAmount.toTzs(),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isSwahili) "JUMLA KUU:" else "TOTAL:",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            Text(
                                text = sale.totalAmount.toTzs(),
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = DukaGreenPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = receiptMessage,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Share / Print / Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, receiptText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Tuma Risiti")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isSwahili) "Tuma" else "Share")
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DukaGreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isSwahili) "Tayari" else "Done")
                    }
                }
            }
        }
    }
}
