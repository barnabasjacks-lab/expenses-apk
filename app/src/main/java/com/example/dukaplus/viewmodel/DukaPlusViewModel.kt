package com.example.dukaplus.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dukaplus.data.*
import com.example.dukaplus.model.CartItem
import com.example.dukaplus.model.DashboardStats
import com.example.dukaplus.model.PaymentMethod
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class DukaPlusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DukaPlusRepository

    init {
        val db = DukaPlusDatabase.getDatabase(application, viewModelScope)
        repository = DukaPlusRepository(db)
    }

    // App Preferences
    private val _isSwahili = MutableStateFlow(true)
    val isSwahili: StateFlow<Boolean> = _isSwahili.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Navigation Active Tab
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // Products & Inventory
    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockProducts: StateFlow<List<ProductEntity>> = repository.lowStockProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _productSearchQuery = MutableStateFlow("")
    val productSearchQuery: StateFlow<String> = _productSearchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Zote")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Filtered Products for POS and Inventory
    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        allProducts,
        _productSearchQuery,
        _selectedCategory
    ) { products, query, cat ->
        products.filter { p ->
            val matchesQuery = query.isBlank() ||
                    p.name.contains(query, ignoreCase = true) ||
                    p.barcode.contains(query, ignoreCase = true)
            val matchesCat = cat == "Zote" || cat == "All" || p.category == cat
            matchesQuery && matchesCat
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart Management for POS
    private val _cartItems = MutableStateFlow<Map<Long, CartItem>>(emptyMap())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.map { it.values.toList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartSubtotal: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.totalPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartTotalProfit: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.profit }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _cartDiscountAmount = MutableStateFlow(0.0)
    val cartDiscountAmount: StateFlow<Double> = _cartDiscountAmount.asStateFlow()

    // Sales & Receipts
    val allSales: StateFlow<List<SaleEntity>> = repository.allSales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _lastCompletedSale = MutableStateFlow<SaleEntity?>(null)
    val lastCompletedSale: StateFlow<SaleEntity?> = _lastCompletedSale.asStateFlow()

    private val _lastSaleItems = MutableStateFlow<List<SaleItemEntity>>(emptyList())
    val lastSaleItems: StateFlow<List<SaleItemEntity>> = _lastSaleItems.asStateFlow()

    // Expenses
    val allExpenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Debts
    val allDebts: StateFlow<List<DebtEntity>> = repository.allDebts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Store Profile
    val storeProfile: StateFlow<StoreProfileEntity?> = repository.storeProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Dashboard Statistics Calculation
    val dashboardStats: StateFlow<DashboardStats> = combine(
        allSales,
        allExpenses,
        allProducts,
        allDebts
    ) { sales, expenses, products, debts ->
        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val todaySales = sales.filter { it.timestamp >= startOfToday }
        val todayExpenses = expenses.filter { it.timestamp >= startOfToday }

        val todaySalesTotal = todaySales.sumOf { it.totalAmount }
        val todayProfitTotal = todaySales.sumOf { it.profit }
        val todayExpensesTotal = todayExpenses.sumOf { it.amount }

        val inventoryValue = products.sumOf { it.stockQuantity * it.sellPrice }
        val lowCount = products.count { it.stockQuantity <= it.minStockAlert }
        val activeDebtsTotal = debts.filter { !it.isPaid }.sumOf { it.remainingAmount }

        DashboardStats(
            todaySalesTotal = todaySalesTotal,
            todayProfitTotal = todayProfitTotal,
            todayExpensesTotal = todayExpensesTotal,
            totalInventoryValue = inventoryValue,
            lowStockCount = lowCount,
            totalTransactionsCount = todaySales.size,
            totalActiveDebts = activeDebtsTotal
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    // Actions
    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun toggleLanguage() {
        _isSwahili.value = !_isSwahili.value
    }

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setProductSearchQuery(q: String) {
        _productSearchQuery.value = q
    }

    fun setSelectedCategory(cat: String) {
        _selectedCategory.value = cat
    }

    // POS Cart Actions
    fun addToCart(product: ProductEntity) {
        val current = _cartItems.value.toMutableMap()
        val existing = current[product.id]
        if (existing != null) {
            // Check stock availability
            if (existing.quantity < product.stockQuantity) {
                current[product.id] = existing.copy(quantity = existing.quantity + 1)
            }
        } else {
            if (product.stockQuantity > 0) {
                current[product.id] = CartItem(product, 1)
            }
        }
        _cartItems.value = current
    }

    fun updateCartQuantity(productId: Long, newQty: Int) {
        val current = _cartItems.value.toMutableMap()
        val existing = current[productId] ?: return
        if (newQty <= 0) {
            current.remove(productId)
        } else {
            val maxStock = existing.product.stockQuantity
            val allowedQty = if (newQty > maxStock) maxStock else newQty
            current[productId] = existing.copy(quantity = allowedQty)
        }
        _cartItems.value = current
    }

    fun removeFromCart(productId: Long) {
        val current = _cartItems.value.toMutableMap()
        current.remove(productId)
        _cartItems.value = current
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
        _cartDiscountAmount.value = 0.0
    }

    fun setCartDiscount(discount: Double) {
        _cartDiscountAmount.value = discount.coerceAtLeast(0.0)
    }

    // Complete Sale (Checkout)
    fun completeSale(
        paymentMethod: PaymentMethod,
        customerName: String = "",
        customerPhone: String = "",
        onSuccess: (SaleEntity, List<SaleItemEntity>) -> Unit
    ) {
        val items = _cartItems.value.values.toList()
        if (items.isEmpty()) return

        val subtotal = items.sumOf { it.totalPrice }
        val discount = _cartDiscountAmount.value
        val finalTotal = (subtotal - discount).coerceAtLeast(0.0)
        val totalCost = items.sumOf { it.totalCost }
        val profit = finalTotal - totalCost

        val receiptSeq = (System.currentTimeMillis() % 100000).toString().padStart(5, '0')
        val receiptNumber = "DP-${receiptSeq}"

        val saleEntity = SaleEntity(
            receiptNumber = receiptNumber,
            totalAmount = finalTotal,
            discountAmount = discount,
            taxAmount = finalTotal * 0.18, // 18% VAT (TRA standard)
            paymentMethod = paymentMethod.id,
            customerName = customerName,
            customerPhone = customerPhone,
            totalCost = totalCost,
            profit = profit,
            timestamp = System.currentTimeMillis()
        )

        val saleItemsList = items.map {
            SaleItemEntity(
                saleId = 0,
                productId = it.product.id,
                productName = it.product.name,
                quantity = it.quantity,
                unitPrice = it.product.sellPrice,
                costPrice = it.product.buyPrice,
                totalPrice = it.totalPrice
            )
        }

        viewModelScope.launch {
            val saleId = repository.recordSale(saleEntity, saleItemsList)
            val savedSale = saleEntity.copy(id = saleId)
            val savedItems = saleItemsList.map { it.copy(saleId = saleId) }

            // If sold on credit/debt, record in debts automatically
            if (paymentMethod == PaymentMethod.DEBT) {
                repository.insertDebt(
                    DebtEntity(
                        customerName = customerName.ifBlank { "Mteja wa Mauzo ($receiptNumber)" },
                        customerPhone = customerPhone,
                        originalAmount = finalTotal,
                        paidAmount = 0.0,
                        remainingAmount = finalTotal,
                        dueDate = System.currentTimeMillis() + (86400000L * 7), // 7 days
                        notes = "Mauzo ya risiti namba $receiptNumber",
                        isPaid = false
                    )
                )
            }

            _lastCompletedSale.value = savedSale
            _lastSaleItems.value = savedItems
            clearCart()
            onSuccess(savedSale, savedItems)
        }
    }

    // Inventory Product Operations
    fun saveProduct(product: ProductEntity) {
        viewModelScope.launch {
            if (product.id == 0L) {
                repository.insertProduct(product)
            } else {
                repository.updateProduct(product)
            }
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun restockProduct(productId: Long, qty: Int) {
        viewModelScope.launch {
            repository.restock(productId, qty)
        }
    }

    // Expenses Operations
    fun addExpense(title: String, amount: Double, category: String, note: String) {
        viewModelScope.launch {
            repository.insertExpense(
                ExpenseEntity(
                    title = title,
                    amount = amount,
                    category = category,
                    note = note
                )
            )
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // Debts Operations
    fun addDebt(name: String, phone: String, amount: Double, dueDate: Long, notes: String) {
        viewModelScope.launch {
            repository.insertDebt(
                DebtEntity(
                    customerName = name,
                    customerPhone = phone,
                    originalAmount = amount,
                    paidAmount = 0.0,
                    remainingAmount = amount,
                    dueDate = dueDate,
                    notes = notes,
                    isPaid = false
                )
            )
        }
    }

    fun recordDebtPayment(debt: DebtEntity, amountPaid: Double) {
        viewModelScope.launch {
            val totalPaid = debt.paidAmount + amountPaid
            val remaining = (debt.originalAmount - totalPaid).coerceAtLeast(0.0)
            val updated = debt.copy(
                paidAmount = totalPaid,
                remainingAmount = remaining,
                isPaid = remaining <= 0.0
            )
            repository.updateDebt(updated)
        }
    }

    fun deleteDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
        }
    }

    // Store Profile Update
    fun updateStoreProfile(profile: StoreProfileEntity) {
        viewModelScope.launch {
            repository.updateStoreProfile(profile)
        }
    }
}
