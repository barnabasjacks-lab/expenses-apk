package com.example.dukaplus.model

import com.example.dukaplus.data.ProductEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CartItem(
    val product: ProductEntity,
    val quantity: Int = 1
) {
    val totalPrice: Double
        get() = product.sellPrice * quantity

    val totalCost: Double
        get() = product.buyPrice * quantity

    val profit: Double
        get() = totalPrice - totalCost
}

enum class PaymentMethod(
    val id: String,
    val displayNameSwahili: String,
    val displayNameEnglish: String,
    val iconEmoji: String,
    val colorHex: Long
) {
    CASH("CASH", "Pesa Taslimu (Cash)", "Cash", "💵", 0xFF10B981),
    MPESA("MPESA", "M-Pesa (Vodacom)", "M-Pesa", "📱", 0xFFDC2626),
    TIGOPESA("TIGOPESA", "Tigo Pesa (Yas)", "Tigo Pesa", "📲", 0xFF2563EB),
    AIRTEL("AIRTEL", "Airtel Money", "Airtel Money", "💳", 0xFFE11D48),
    HALOPESA("HALOPESA", "Halopesa", "Halopesa", "📞", 0xFFEA580C),
    BANK_NMB("BANK_NMB", "Benki NMB", "NMB Bank", "🏦", 0xFFD97706),
    BANK_CRDB("BANK_CRDB", "Benki CRDB", "CRDB Bank", "🏛️", 0xFF059669),
    DEBT("DEBT", "Deni (Lipa Baadaye)", "Credit / Debt", "📝", 0xFF7C3AED);

    fun getLabel(isSwahili: Boolean): String = if (isSwahili) displayNameSwahili else displayNameEnglish
}

data class DashboardStats(
    val todaySalesTotal: Double = 0.0,
    val todayProfitTotal: Double = 0.0,
    val todayExpensesTotal: Double = 0.0,
    val totalInventoryValue: Double = 0.0,
    val lowStockCount: Int = 0,
    val totalTransactionsCount: Int = 0,
    val totalActiveDebts: Double = 0.0
)

fun Double.toTzs(): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    return "TZS ${formatter.format(this)}"
}

fun Long.toFormattedDate(pattern: String = "dd/MM/yyyy HH:mm"): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(Date(this))
}
