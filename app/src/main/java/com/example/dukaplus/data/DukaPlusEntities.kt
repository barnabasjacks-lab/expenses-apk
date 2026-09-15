package com.example.dukaplus.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val barcode: String = "",
    val buyPrice: Double,
    val sellPrice: Double,
    val stockQuantity: Int,
    val minStockAlert: Int = 5,
    val unit: String = "pcs", // pcs, kg, lita, box, pakiti, chupa
    val category: String = "Vyakula",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val receiptNumber: String,
    val totalAmount: Double,
    val discountAmount: Double = 0.0,
    val taxAmount: Double = 0.0,
    val paymentMethod: String, // CASH, MPESA, TIGOPESA, AIRTEL, HALOPESA, BANK_NMB, BANK_CRDB, DEBT
    val customerName: String = "",
    val customerPhone: String = "",
    val totalCost: Double = 0.0, // Used to compute instant profit
    val profit: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true
)

@Entity(tableName = "sale_items")
data class SaleItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val saleId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val costPrice: Double,
    val totalPrice: Double
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String, // Kodi & Fremu, LUKU & Maji, Usafiri & Mizigo, Mishahara, Leseni & TRA, Mengineyo
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerName: String,
    val customerPhone: String,
    val originalAmount: Double,
    val paidAmount: Double = 0.0,
    val remainingAmount: Double,
    val dueDate: Long,
    val notes: String = "",
    val isPaid: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "store_profile")
data class StoreProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val storeName: String = "DukaPlus Mini Supermarket",
    val phone: String = "+255 754 000 111",
    val location: String = "Kariakoo Msimbazi, Dar es Salaam",
    val tinNumber: String = "143-890-542",
    val vrnNumber: String = "40-009812-B",
    val receiptMessage: String = "Asante kwa kununua nasi! Karibu tena DukaPlus.",
    val currency: String = "TZS",
    val taxRatePercent: Double = 18.0
)
