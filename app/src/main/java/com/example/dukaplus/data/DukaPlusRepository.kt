package com.example.dukaplus.data

import kotlinx.coroutines.flow.Flow

class DukaPlusRepository(private val database: DukaPlusDatabase) {

    private val productDao = database.productDao()
    private val saleDao = database.saleDao()
    private val expenseDao = database.expenseDao()
    private val debtDao = database.debtDao()
    private val storeProfileDao = database.storeProfileDao()

    // Products
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val lowStockProducts: Flow<List<ProductEntity>> = productDao.getLowStockProducts()

    suspend fun getProductByBarcode(barcode: String): ProductEntity? = productDao.getProductByBarcode(barcode)
    suspend fun insertProduct(product: ProductEntity): Long = productDao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: ProductEntity) = productDao.deleteProduct(product)
    suspend fun restock(productId: Long, qty: Int) = productDao.restock(productId, qty)

    // Sales
    val allSales: Flow<List<SaleEntity>> = saleDao.getAllSales()
    suspend fun getSaleItems(saleId: Long): List<SaleItemEntity> = saleDao.getItemsForSale(saleId)

    suspend fun recordSale(
        sale: SaleEntity,
        items: List<SaleItemEntity>
    ): Long {
        val saleId = saleDao.insertSale(sale)
        val itemsWithSaleId = items.map { it.copy(saleId = saleId) }
        saleDao.insertSaleItems(itemsWithSaleId)

        // Deduct inventory stock for each product sold
        for (item in items) {
            productDao.deductStock(item.productId, item.quantity)
        }
        return saleId
    }

    // Expenses
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    suspend fun insertExpense(expense: ExpenseEntity): Long = expenseDao.insertExpense(expense)
    suspend fun deleteExpense(expense: ExpenseEntity) = expenseDao.deleteExpense(expense)

    // Debts
    val allDebts: Flow<List<DebtEntity>> = debtDao.getAllDebts()
    suspend fun insertDebt(debt: DebtEntity): Long = debtDao.insertDebt(debt)
    suspend fun updateDebt(debt: DebtEntity) = debtDao.updateDebt(debt)
    suspend fun deleteDebt(debt: DebtEntity) = debtDao.deleteDebt(debt)

    // Store Profile
    val storeProfile: Flow<StoreProfileEntity?> = storeProfileDao.getStoreProfile()
    suspend fun updateStoreProfile(profile: StoreProfileEntity) = storeProfileDao.insertOrUpdate(profile)
}
