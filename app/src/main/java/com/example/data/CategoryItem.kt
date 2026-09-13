package com.example.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryItem(
    val id: String,
    val name: String,
    val type: TransactionType,
    val icon: ImageVector
)

object Categories {
    val expenseCategories = listOf(
        CategoryItem("food", "Food & Groceries (Chakula)", TransactionType.EXPENSE, Icons.Default.Restaurant),
        CategoryItem("transport", "Transport (Daladala / Boda)", TransactionType.EXPENSE, Icons.Default.DirectionsBus),
        CategoryItem("rent", "Rent & Housing (Kodi)", TransactionType.EXPENSE, Icons.Default.Home),
        CategoryItem("utilities", "Utilities (LUKU & Maji)", TransactionType.EXPENSE, Icons.Default.Bolt),
        CategoryItem("airtime", "Airtime & Internet (Vocha)", TransactionType.EXPENSE, Icons.Default.Wifi),
        CategoryItem("health", "Health & Medical (Afya)", TransactionType.EXPENSE, Icons.Default.LocalHospital),
        CategoryItem("education", "School & Education (Ada)", TransactionType.EXPENSE, Icons.Default.School),
        CategoryItem("shopping", "Shopping (Manunuzi)", TransactionType.EXPENSE, Icons.Default.ShoppingBag),
        CategoryItem("entertainment", "Entertainment (Burudani)", TransactionType.EXPENSE, Icons.Default.Celebration),
        CategoryItem("family", "Family & Giving (Michango)", TransactionType.EXPENSE, Icons.Default.People),
        CategoryItem("other_expense", "Other Expenses (Mengineyo)", TransactionType.EXPENSE, Icons.Default.Category)
    )

    val incomeCategories = listOf(
        CategoryItem("salary", "Salary (Mshahara)", TransactionType.INCOME, Icons.Default.Payments),
        CategoryItem("business", "Business / Sales (Biashara)", TransactionType.INCOME, Icons.Default.Storefront),
        CategoryItem("side_hustle", "Side Hustle (Kibarua)", TransactionType.INCOME, Icons.Default.Work),
        CategoryItem("investments", "Investment (Uwekezaji)", TransactionType.INCOME, Icons.Default.TrendingUp),
        CategoryItem("gift", "Gift / Allowance (Zawadi)", TransactionType.INCOME, Icons.Default.CardGiftcard),
        CategoryItem("other_income", "Other Income (Mapato)", TransactionType.INCOME, Icons.Default.AccountBalance)
    )

    fun getIconForCategory(categoryName: String, type: TransactionType): ImageVector {
        val list = if (type == TransactionType.INCOME) incomeCategories else expenseCategories
        val found = list.find { it.name.equals(categoryName, ignoreCase = true) || it.id.equals(categoryName, ignoreCase = true) }
        return found?.icon ?: if (type == TransactionType.INCOME) Icons.Default.AccountBalance else Icons.Default.Category
    }
}
