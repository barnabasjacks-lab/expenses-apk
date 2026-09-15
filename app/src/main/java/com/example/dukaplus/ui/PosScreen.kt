package com.example.dukaplus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dukaplus.data.ProductEntity
import com.example.dukaplus.data.SaleEntity
import com.example.dukaplus.data.SaleItemEntity
import com.example.dukaplus.model.CartItem
import com.example.dukaplus.model.PaymentMethod
import com.example.dukaplus.model.toTzs
import com.example.dukaplus.viewmodel.DukaPlusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(
    viewModel: DukaPlusViewModel,
    modifier: Modifier = Modifier,
    onSaleCompleted: (SaleEntity, List<SaleItemEntity>) -> Unit
) {
    val isSwahili by viewModel.isSwahili.collectAsState()
    val products by viewModel.filteredProducts.collectAsState()
    val cartList by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val discount by viewModel.cartDiscountAmount.collectAsState()
    val searchQuery by viewModel.productSearchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var showCheckoutSheet by remember { mutableStateOf(false) }
    var showBarcodeModal by remember { mutableStateOf(false) }

    val categories = listOf(
        if (isSwahili) "Zote" else "All",
        "Vyakula",
        "Vinywaji",
        "Usafi",
        "Mengineyo"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            // Search and Barcode Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setProductSearchQuery(it) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_pos_search"),
                    placeholder = {
                        Text(
                            if (isSwahili) "Tafuta bidhaa au barcode..." else "Search product or barcode...",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setProductSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                FilledTonalIconButton(
                    onClick = { showBarcodeModal = true },
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("btn_barcode_scanner"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Barcode", tint = DukaGreenPrimary)
                }
            }

            // Categories Filter Bar
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat || (cat == "All" && selectedCategory == "Zote")
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedCategory(cat) },
                        label = { Text(cat, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DukaGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Product Grid
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📦",
                            fontSize = 44.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isSwahili) "Hakuna bidhaa inayolingana." else "No products found.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 4.dp),
                    contentPadding = PaddingValues(bottom = 140.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(products) { product ->
                        val inCart = cartList.find { it.product.id == product.id }
                        PosProductCard(
                            product = product,
                            inCartQty = inCart?.quantity ?: 0,
                            isSwahili = isSwahili,
                            onAddToCart = { viewModel.addToCart(product) },
                            onReduce = {
                                if (inCart != null) {
                                    viewModel.updateCartQuantity(product.id, inCart.quantity - 1)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Floating Cart Summary Bar (Bottom)
        if (cartList.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 84.dp)
                    .testTag("floating_cart_bar"),
                shape = RoundedCornerShape(18.dp),
                color = DukaGreenDark,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DukaGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${cartList.sumOf { it.quantity }} ${if (isSwahili) "bidhaa" else "items"}",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                            Text(
                                text = subtotal.toTzs(),
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Button(
                        onClick = { showCheckoutSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DukaGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_proceed_checkout")
                    ) {
                        Text(
                            text = if (isSwahili) "LIPISHA ➡️" else "CHECKOUT ➡️",
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    // Checkout Bottom Sheet
    if (showCheckoutSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCheckoutSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            CheckoutSheetContent(
                cartItems = cartList,
                subtotal = subtotal,
                discount = discount,
                isSwahili = isSwahili,
                onUpdateQty = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                onSetDiscount = { viewModel.setCartDiscount(it) },
                onClearCart = {
                    viewModel.clearCart()
                    showCheckoutSheet = false
                },
                onCompleteSale = { method, cName, cPhone ->
                    viewModel.completeSale(method, cName, cPhone) { sale, items ->
                        showCheckoutSheet = false
                        onSaleCompleted(sale, items)
                    }
                }
            )
        }
    }

    // Barcode Scanner Modal Simulation
    if (showBarcodeModal) {
        BarcodeScannerDialog(
            isSwahili = isSwahili,
            onBarcodeDetected = { code ->
                viewModel.setProductSearchQuery(code)
                showBarcodeModal = false
            },
            onDismiss = { showBarcodeModal = false }
        )
    }
}

@Composable
fun PosProductCard(
    product: ProductEntity,
    inCartQty: Int,
    isSwahili: Boolean,
    onAddToCart: () -> Unit,
    onReduce: () -> Unit
) {
    val isOutOfStock = product.stockQuantity <= 0

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (inCartQty > 0) DukaGreenPrimary.copy(alpha = 0.08f)
            else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isOutOfStock) { onAddToCart() }
            .testTag("pos_card_${product.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = product.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (product.stockQuantity <= product.minStockAlert) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DukaRed.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (isOutOfStock) (if (isSwahili) "Imeisha" else "Empty")
                            else (if (isSwahili) "Imepungua" else "Low"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DukaRed,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = product.sellPrice.toTzs(),
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = DukaGreenPrimary
            )

            Text(
                text = if (isSwahili) "Stoo: ${product.stockQuantity} ${product.unit}"
                else "Stock: ${product.stockQuantity} ${product.unit}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Add to Cart Button / Quantity Controller
            if (inCartQty > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DukaGreenPrimary.copy(alpha = 0.15f)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onReduce,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = DukaGreenPrimary)
                    }

                    Text(
                        text = "$inCartQty",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = DukaGreenDark
                    )

                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier.size(34.dp),
                        enabled = inCartQty < product.stockQuantity
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = DukaGreenPrimary)
                    }
                }
            } else {
                Button(
                    onClick = onAddToCart,
                    enabled = !isOutOfStock,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DukaGreenPrimary),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isOutOfStock) (if (isSwahili) "Imeisha" else "Out of stock")
                        else (if (isSwahili) "Ongeza" else "Add"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CheckoutSheetContent(
    cartItems: List<CartItem>,
    subtotal: Double,
    discount: Double,
    isSwahili: Boolean,
    onUpdateQty: (Long, Int) -> Unit,
    onSetDiscount: (Double) -> Unit,
    onClearCart: () -> Unit,
    onCompleteSale: (PaymentMethod, String, String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var discountInput by remember { mutableStateOf(if (discount > 0) discount.toInt().toString() else "") }

    val finalTotal = (subtotal - discount).coerceAtLeast(0.0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isSwahili) "Kikapu cha Mauzo" else "Cart & Checkout",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onClearCart) {
                Text(if (isSwahili) "Futa Kikapu" else "Clear", color = DukaRed)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Cart items list
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            cartItems.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.product.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text(
                            text = "${item.product.sellPrice.toTzs()} x ${item.quantity} = ${item.totalPrice.toTzs()}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onUpdateQty(item.product.id, item.quantity - 1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, tint = DukaRed)
                        }
                        Text(
                            text = "${item.quantity}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                        IconButton(
                            onClick = { onUpdateQty(item.product.id, item.quantity + 1) },
                            modifier = Modifier.size(32.dp),
                            enabled = item.quantity < item.product.stockQuantity
                        ) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = DukaGreenPrimary)
                        }
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // Discount Field
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = discountInput,
                onValueChange = {
                    discountInput = it
                    val d = it.toDoubleOrNull() ?: 0.0
                    onSetDiscount(d)
                },
                label = { Text(if (isSwahili) "Punguzo (TZS Discount)" else "Discount (TZS)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(if (isSwahili) "Kodi (18% VAT)" else "Tax (18% VAT)", fontSize = 11.sp)
                Text((finalTotal * 0.18).toTzs(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Payment Method Selection (Tanzania context)
        Text(
            text = if (isSwahili) "Njia ya Malipo (Tanzania):" else "Payment Method (Tanzania):",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            PaymentMethod.values().forEach { method ->
                val isSelected = selectedMethod == method
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color(method.colorHex).copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(method.colorHex)) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedMethod = method }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(method.iconEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = method.getLabel(isSwahili),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f)
                        )
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedMethod = method },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(method.colorHex))
                        )
                    }
                }
            }
        }

        // Optional Customer Info (mandatory if DEBT)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (selectedMethod == PaymentMethod.DEBT) {
                if (isSwahili) "Taarifa za Mteja wa Deni (Lazima):" else "Debtor Details (Required):"
            } else {
                if (isSwahili) "Taarifa za Mteja (Hiari):" else "Customer Details (Optional):"
            },
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (selectedMethod == PaymentMethod.DEBT) DukaPurple else MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it },
                label = { Text(if (isSwahili) "Jina la Mteja" else "Customer Name") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = customerPhone,
                onValueChange = { customerPhone = it },
                label = { Text(if (isSwahili) "Namba ya Simu" else "Phone Number") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Total and Complete Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isSwahili) "Jumla ya Kulipa:" else "Total to Pay:",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = finalTotal.toTzs(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = DukaGreenPrimary,
                    fontSize = 22.sp
                )
            }

            Button(
                onClick = {
                    onCompleteSale(selectedMethod, customerName, customerPhone)
                },
                modifier = Modifier
                    .height(54.dp)
                    .testTag("btn_confirm_complete_sale"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DukaGreenPrimary)
            ) {
                Icon(Icons.Default.Receipt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSwahili) "Kamilisha Mauzo" else "Complete Sale",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun BarcodeScannerDialog(
    isSwahili: Boolean,
    onBarcodeDetected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var manualBarcode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = DukaGreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isSwahili) "Kichanganuzi cha Msimbomstari (Barcode)" else "Barcode / QR Scanner")
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Viewfinder visual simulation
                Box(
                    modifier = Modifier
                        .size(200.dp, 120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(2.dp)
                                .background(Color.Red)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isSwahili) "Weka barcode mbele ya kamera" else "Align barcode in frame",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick test barcode chips (Starter items)
                Text(
                    text = if (isSwahili) "Au chagua barcode ya mfano:" else "Or pick a sample barcode:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SuggestionChip(
                        onClick = { onBarcodeDetected("6161101001") },
                        label = { Text("Unga (001)", fontSize = 11.sp) }
                    )
                    SuggestionChip(
                        onClick = { onBarcodeDetected("6161101003") },
                        label = { Text("Mafuta (003)", fontSize = 11.sp) }
                    )
                    SuggestionChip(
                        onClick = { onBarcodeDetected("6161101006") },
                        label = { Text("Uhai (006)", fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = manualBarcode,
                    onValueChange = { manualBarcode = it },
                    label = { Text(if (isSwahili) "Weka tarakimu za barcode" else "Manual barcode input") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (manualBarcode.isNotBlank()) {
                        onBarcodeDetected(manualBarcode)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DukaGreenPrimary)
            ) {
                Text(if (isSwahili) "Tafuta" else "Search")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isSwahili) "Funga" else "Close")
            }
        }
    )
}
