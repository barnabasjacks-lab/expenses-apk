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
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.util.AppLanguage

data class CategoryItem(
    val id: String,
    val swName: String,
    val enName: String,
    val type: TransactionType,
    val icon: ImageVector
) {
    fun getDisplayName(lang: AppLanguage): String {
        return if (lang == AppLanguage.SW) swName else enName
    }
}

object Categories {
    val expenseCategories = listOf(
        CategoryItem("food", "Chakula & Vyakula", "Food & Groceries", TransactionType.EXPENSE, Icons.Default.Restaurant),
        CategoryItem("transport", "Nauli (Daladala / Boda)", "Transport & Commute", TransactionType.EXPENSE, Icons.Default.DirectionsBus),
        CategoryItem("airtime", "Vocha & Vifurushi", "Airtime & Internet", TransactionType.EXPENSE, Icons.Default.PhoneAndroid),
        CategoryItem("utilities", "LUKU & Maji", "Utilities (Luku & Water)", TransactionType.EXPENSE, Icons.Default.Bolt),
        CategoryItem("rent", "Kodi ya Nyumba / Chumba", "Rent & Housing", TransactionType.EXPENSE, Icons.Default.Home),
        CategoryItem("health", "Afya & Dawa", "Health & Pharmacy", TransactionType.EXPENSE, Icons.Default.LocalHospital),
        CategoryItem("education", "Ada & Shule", "School Fees & Education", TransactionType.EXPENSE, Icons.Default.School),
        CategoryItem("shopping", "Manunuzi & Mavazi", "Shopping & Clothes", TransactionType.EXPENSE, Icons.Default.ShoppingBag),
        CategoryItem("family", "Michango & Familia", "Family & Contributions", TransactionType.EXPENSE, Icons.Default.People),
        CategoryItem("entertainment", "Burudani & Michezo", "Outing & Entertainment", TransactionType.EXPENSE, Icons.Default.Celebration),
        CategoryItem("other_expense", "Mengineyo", "Other Expenses", TransactionType.EXPENSE, Icons.Default.Category)
    )

    val incomeCategories = listOf(
        CategoryItem("salary", "Mshahara", "Salary / Employment", TransactionType.INCOME, Icons.Default.Payments),
        CategoryItem("business", "Mauzo ya Biashara", "Business & Sales", TransactionType.INCOME, Icons.Default.Storefront),
        CategoryItem("side_hustle", "Kibarua / Dili", "Side Hustle / Gig", TransactionType.INCOME, Icons.Default.Work),
        CategoryItem("investments", "Gawio / Uwekezaji", "Investment Returns", TransactionType.INCOME, Icons.Default.TrendingUp),
        CategoryItem("gift", "Zawadi / Kutumiwa", "Gift & Remittance", TransactionType.INCOME, Icons.Default.CardGiftcard),
        CategoryItem("other_income", "Mapato Mengine", "Other Income", TransactionType.INCOME, Icons.Default.AccountBalance)
    )

    fun getIconForCategory(categoryName: String, type: TransactionType): ImageVector {
        val list = if (type == TransactionType.INCOME) incomeCategories else expenseCategories
        val found = list.find { 
            it.id.equals(categoryName, ignoreCase = true) || 
            it.swName.equals(categoryName, ignoreCase = true) || 
            it.enName.equals(categoryName, ignoreCase = true) 
        }
        return found?.icon ?: if (type == TransactionType.INCOME) Icons.Default.AccountBalance else Icons.Default.Category
    }
}
