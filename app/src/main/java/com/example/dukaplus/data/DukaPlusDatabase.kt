package com.example.dukaplus.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProductEntity::class,
        SaleEntity::class,
        SaleItemEntity::class,
        ExpenseEntity::class,
        DebtEntity::class,
        StoreProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DukaPlusDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun debtDao(): DebtDao
    abstract fun storeProfileDao(): StoreProfileDao

    companion object {
        @Volatile
        private var INSTANCE: DukaPlusDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): DukaPlusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DukaPlusDatabase::class.java,
                    "dukaplus_pos.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DukaPlusDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DukaPlusDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            suspend fun populateInitialData(db: DukaPlusDatabase) {
                val productDao = db.productDao()
                val profileDao = db.storeProfileDao()
                val expenseDao = db.expenseDao()
                val debtDao = db.debtDao()

                // Default Store Profile
                profileDao.insertOrUpdate(
                    StoreProfileEntity(
                        id = 1,
                        storeName = "DukaPlus Mini Supermarket",
                        phone = "+255 754 892 341",
                        location = "Msimbazi St, Kariakoo, Dar es Salaam",
                        tinNumber = "128-940-512",
                        vrnNumber = "40-008921-X",
                        receiptMessage = "Asante sana kwa kufanya manunuzi nasi! Karibu tena DukaPlus.",
                        currency = "TZS",
                        taxRatePercent = 18.0
                    )
                )

                // Starter Tanzanian Products
                val initialProducts = listOf(
                    ProductEntity(name = "Azam Unga Ngano 1kg", barcode = "6161101001", buyPrice = 2000.0, sellPrice = 2500.0, stockQuantity = 34, minStockAlert = 5, unit = "pakiti", category = "Vyakula"),
                    ProductEntity(name = "Mchele Safi wa Mbeya (Kyela) 1kg", barcode = "6161101002", buyPrice = 2600.0, sellPrice = 3200.0, stockQuantity = 60, minStockAlert = 10, unit = "kg", category = "Vyakula"),
                    ProductEntity(name = "Mafuta ya Korie Safi 1L", barcode = "6161101003", buyPrice = 4800.0, sellPrice = 5800.0, stockQuantity = 18, minStockAlert = 4, unit = "lita", category = "Vyakula"),
                    ProductEntity(name = "Sukari ya Kilombero 1kg", barcode = "6161101004", buyPrice = 2700.0, sellPrice = 3300.0, stockQuantity = 45, minStockAlert = 8, unit = "kg", category = "Vyakula"),
                    ProductEntity(name = "Sabuni ya Jamaa (Baa Kubwa)", barcode = "6161101005", buyPrice = 1800.0, sellPrice = 2400.0, stockQuantity = 22, minStockAlert = 6, unit = "pcs", category = "Usafi"),
                    ProductEntity(name = "Maji ya Uhai 500ml", barcode = "6161101006", buyPrice = 400.0, sellPrice = 600.0, stockQuantity = 72, minStockAlert = 12, unit = "chupa", category = "Vinywaji"),
                    ProductEntity(name = "Chai ya Green Label (Kilima) 100g", barcode = "6161101007", buyPrice = 1200.0, sellPrice = 1700.0, stockQuantity = 15, minStockAlert = 5, unit = "pakiti", category = "Vinywaji"),
                    ProductEntity(name = "Dawa ya Meno Colgate 140g", barcode = "6161101008", buyPrice = 2500.0, sellPrice = 3500.0, stockQuantity = 14, minStockAlert = 3, unit = "pcs", category = "Usafi"),
                    ProductEntity(name = "Maziwa ya Asas Fresh 500ml", barcode = "6161101009", buyPrice = 1500.0, sellPrice = 2000.0, stockQuantity = 3, minStockAlert = 5, unit = "pakiti", category = "Vinywaji"), // Low stock demo!
                    ProductEntity(name = "Maharage ya Mbeya (Soya) 1kg", barcode = "6161101010", buyPrice = 2800.0, sellPrice = 3600.0, stockQuantity = 2, minStockAlert = 5, unit = "kg", category = "Vyakula") // Low stock demo!
                )

                for (p in initialProducts) {
                    productDao.insertProduct(p)
                }

                // Sample Expenses
                expenseDao.insertExpense(
                    ExpenseEntity(
                        title = "LUKU Umeme wa Duka",
                        amount = 35000.0,
                        category = "LUKU & Maji",
                        note = "Mwezi huu mwanzo"
                    )
                )
                expenseDao.insertExpense(
                    ExpenseEntity(
                        title = "Mizigo kutoka Kariakoo Gerezani",
                        amount = 15000.0,
                        category = "Usafiri & Mizigo",
                        note = "Bodaboda ya kubeba mifuko"
                    )
                )

                // Sample Debts
                debtDao.insertDebt(
                    DebtEntity(
                        customerName = "Mama Juma",
                        customerPhone = "0714 555 888",
                        originalAmount = 45000.0,
                        paidAmount = 15000.0,
                        remainingAmount = 30000.0,
                        dueDate = System.currentTimeMillis() + (86400000L * 3), // siku 3 zijazo
                        notes = "Alichukua mafuta na mchele, amelipa nusu",
                        isPaid = false
                    )
                )
                debtDao.insertDebt(
                    DebtEntity(
                        customerName = "Mwalimu Denis",
                        customerPhone = "0755 222 333",
                        originalAmount = 18500.0,
                        paidAmount = 0.0,
                        remainingAmount = 18500.0,
                        dueDate = System.currentTimeMillis() + (86400000L * 7),
                        notes = "Amesema atalipa mwisho wa mwezi mshahara ukitoka",
                        isPaid = false
                    )
                )
            }
        }
    }
}
