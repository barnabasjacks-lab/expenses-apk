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
import com.example.dukaplus.data.ProductEntity
import com.example.dukaplus.model.toTzs
import com.example.dukaplus.viewmodel.DukaPlusViewModel

@Composable
fun InventoryScreen(
    viewModel: DukaPlusViewModel,
    modifier: Modifier = Modifier
) {
    val isSwahili by viewModel.isSwahili.collectAsState()
    val products by viewModel.allProducts.collectAsState()
    val lowStockList by viewModel.lowStockProducts.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var filterOnlyLowStock by remember { mutableStateOf(false) }

    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val displayedProducts = products.filter { p ->
        val matchesSearch = searchQuery.isBlank() ||
                p.name.contains(searchQuery, ignoreCase = true) ||
                p.barcode.contains(searchQuery, ignoreCase = true)
        val matchesLowStock = !filterOnlyLowStock || (p.stockQuantity <= p.minStockAlert)
        matchesSearch && matchesLowStock
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingProduct = null
                    showAddDialog = true
                },
                containerColor = DukaGreenPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(if (isSwahili) "Ongeza Bidhaa" else "Add Product", fontWeight = FontWeight.Bold) },
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("fab_add_product")
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
            // Screen Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isSwahili) "Stoo na Bidhaa" else "Inventory & Stock",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${products.size} ${if (isSwahili) "bidhaa zimesajiliwa" else "products recorded"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(if (isSwahili) "Tafuta jina au barcode..." else "Search by name or barcode...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = !filterOnlyLowStock,
                    onClick = { filterOnlyLowStock = false },
                    label = { Text(if (isSwahili) "Zote (${products.size})" else "All (${products.size})") }
                )

                FilterChip(
                    selected = filterOnlyLowStock,
                    onClick = { filterOnlyLowStock = true },
                    label = {
                        Text(
                            if (isSwahili) "Zilizopungua (${lowStockList.size})" else "Low Stock (${lowStockList.size})",
                            color = if (lowStockList.isNotEmpty()) DukaRed else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DukaRed.copy(alpha = 0.2f),
                        selectedLabelColor = DukaRed
                    )
                )
            }

            // Products List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayedProducts, key = { it.id }) { product ->
                    InventoryProductRow(
                        product = product,
                        isSwahili = isSwahili,
                        onEdit = {
                            editingProduct = product
                            showAddDialog = true
                        },
                        onRestock = { qty ->
                            viewModel.restockProduct(product.id, qty)
                        }
                    )
                }
            }
        }
    }

    // Add or Edit Product Dialog
    if (showAddDialog) {
        AddEditProductDialog(
            product = editingProduct,
            isSwahili = isSwahili,
            onDismiss = { showAddDialog = false },
            onSave = { savedProduct ->
                viewModel.saveProduct(savedProduct)
                showAddDialog = false
            },
            onDelete = {
                if (editingProduct != null) {
                    viewModel.deleteProduct(editingProduct!!)
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
fun InventoryProductRow(
    product: ProductEntity,
    isSwahili: Boolean,
    onEdit: () -> Unit,
    onRestock: (Int) -> Unit
) {
    val isLowStock = product.stockQuantity <= product.minStockAlert

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLowStock) DukaRed.copy(alpha = 0.05f)
            else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .testTag("inventory_item_${product.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = product.category,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (product.barcode.isNotBlank()) {
                            Text(
                                text = " • SKU: ${product.barcode}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isLowStock) DukaRed.copy(alpha = 0.15f)
                    else DukaGreenPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${product.stockQuantity} ${product.unit}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = if (isLowStock) DukaRed else DukaGreenPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isSwahili) "Kununulia: ${product.buyPrice.toTzs()}" else "Cost: ${product.buyPrice.toTzs()}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isSwahili) "Kuuzia: ${product.sellPrice.toTzs()}" else "Price: ${product.sellPrice.toTzs()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DukaGreenPrimary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { onRestock(10) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isSwahili) "+10 Mzigo" else "+10 Restock", fontSize = 11.sp)
                    }

                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditProductDialog(
    product: ProductEntity?,
    isSwahili: Boolean,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var barcode by remember { mutableStateOf(product?.barcode ?: "") }
    var buyPriceText by remember { mutableStateOf(product?.buyPrice?.toInt()?.toString() ?: "") }
    var sellPriceText by remember { mutableStateOf(product?.sellPrice?.toInt()?.toString() ?: "") }
    var stockText by remember { mutableStateOf(product?.stockQuantity?.toString() ?: "10") }
    var minStockText by remember { mutableStateOf(product?.minStockAlert?.toString() ?: "5") }
    var category by remember { mutableStateOf(product?.category ?: "Vyakula") }
    var unit by remember { mutableStateOf(product?.unit ?: "pcs") }

    val buyPrice = buyPriceText.toDoubleOrNull() ?: 0.0
    val sellPrice = sellPriceText.toDoubleOrNull() ?: 0.0
    val expectedMargin = sellPrice - buyPrice

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (product == null) {
                    if (isSwahili) "Ongeza Bidhaa Mpya" else "Add New Product"
                } else {
                    if (isSwahili) "Hariri Bidhaa" else "Edit Product"
                },
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
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isSwahili) "Jina la Bidhaa *" else "Product Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("Barcode / SKU") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text(if (isSwahili) "Kategoria" else "Category") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = buyPriceText,
                        onValueChange = { buyPriceText = it },
                        label = { Text(if (isSwahili) "Bei ya Kununua (TZS)" else "Cost Price (TZS)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = sellPriceText,
                        onValueChange = { sellPriceText = it },
                        label = { Text(if (isSwahili) "Bei ya Kuuzia (TZS)" else "Selling Price (TZS)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (sellPrice > 0 && buyPrice > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DukaGreenPrimary.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isSwahili) "Faida Tarajiwa: +${expectedMargin.toTzs()} kwa kila $unit"
                            else "Expected Profit: +${expectedMargin.toTzs()} per $unit",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DukaGreenPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it },
                        label = { Text(if (isSwahili) "Idadi Stoo" else "Quantity") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text(if (isSwahili) "Kipimo (pcs/kg/lita)" else "Unit") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = minStockText,
                    onValueChange = { minStockText = it },
                    label = { Text(if (isSwahili) "Kiwango cha Tahadhari (Low Alert)" else "Low Stock Alert Level") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && sellPrice > 0) {
                        val newProduct = ProductEntity(
                            id = product?.id ?: 0L,
                            name = name.trim(),
                            barcode = barcode.trim(),
                            buyPrice = buyPrice,
                            sellPrice = sellPrice,
                            stockQuantity = stockText.toIntOrNull() ?: 0,
                            minStockAlert = minStockText.toIntOrNull() ?: 5,
                            unit = unit.trim().ifBlank { "pcs" },
                            category = category.trim().ifBlank { "Vyakula" }
                        )
                        onSave(newProduct)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DukaGreenPrimary)
            ) {
                Text(if (isSwahili) "Hifadhi" else "Save")
            }
        },
        dismissButton = {
            Row {
                if (product != null) {
                    TextButton(onClick = onDelete) {
                        Text(if (isSwahili) "Futa" else "Delete", color = DukaRed)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(if (isSwahili) "Ghairi" else "Cancel")
                }
            }
        }
    )
}
