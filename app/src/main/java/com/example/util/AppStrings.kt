package com.example.util

enum class AppLanguage {
    SW, // Kiswahili
    EN  // English
}

object AppStrings {
    fun appTitle(lang: AppLanguage) = if (lang == AppLanguage.SW) "Kifuatilia Matumizi" else "TZS Expense Tracker"
    fun appSubtitle(lang: AppLanguage) = if (lang == AppLanguage.SW) "Shilingi ya Tanzania (TZS)" else "Tanzanian Shillings (TZS)"
    
    fun currentBalance(lang: AppLanguage) = if (lang == AppLanguage.SW) "Salio Lililopo" else "Current Balance"
    fun totalIncome(lang: AppLanguage) = if (lang == AppLanguage.SW) "Jumla ya Mapato" else "Total Income"
    fun totalExpense(lang: AppLanguage) = if (lang == AppLanguage.SW) "Jumla ya Matumizi" else "Total Expenses"
    
    fun addIncome(lang: AppLanguage) = if (lang == AppLanguage.SW) "+ Ingiza Mapato" else "+ Add Income"
    fun addExpense(lang: AppLanguage) = if (lang == AppLanguage.SW) "- Weka Matumizi" else "- Add Expense"
    
    fun transactionHistory(lang: AppLanguage) = if (lang == AppLanguage.SW) "Historia ya Miamala" else "Transaction History"
    fun all(lang: AppLanguage) = if (lang == AppLanguage.SW) "Yote" else "All"
    fun expenses(lang: AppLanguage) = if (lang == AppLanguage.SW) "Matumizi" else "Expenses"
    fun income(lang: AppLanguage) = if (lang == AppLanguage.SW) "Mapato" else "Income"
    
    fun noTransactions(lang: AppLanguage) = if (lang == AppLanguage.SW) "Bado haujaweka muamala wowote" else "No transactions yet"
    fun noTransactionsSub(lang: AppLanguage) = if (lang == AppLanguage.SW) "Bofya vitufe vya juu kuongeza mapato au matumizi ya leo." else "Add your first income or expense to begin tracking!"
    
    fun searchPlaceholder(lang: AppLanguage) = if (lang == AppLanguage.SW) "Tafuta muamala au kundi..." else "Search transactions or category..."
    fun clearSearch(lang: AppLanguage) = if (lang == AppLanguage.SW) "Futa utafutaji" else "Clear search"
    
    fun newTransaction(lang: AppLanguage, isIncome: Boolean) = if (lang == AppLanguage.SW) {
        if (isIncome) "Weka Mapato Mapya" else "Weka Matumizi Mapya"
    } else {
        if (isIncome) "Add New Income" else "Add New Expense"
    }
    
    fun editTransaction(lang: AppLanguage) = if (lang == AppLanguage.SW) "Hariri Muamala" else "Edit Transaction"
    
    fun amountLabel(lang: AppLanguage) = if (lang == AppLanguage.SW) "Kiasi cha Pesa (TZS)" else "Amount (TZS)"
    fun paymentMethodLabel(lang: AppLanguage) = if (lang == AppLanguage.SW) "Njia ya Malipo" else "Payment Method"
    fun categoryLabel(lang: AppLanguage) = if (lang == AppLanguage.SW) "Kundi" else "Category"
    fun noteLabel(lang: AppLanguage) = if (lang == AppLanguage.SW) "Maelezo Mafupi (Hiari)" else "Description / Note (Optional)"
    fun dateLabel(lang: AppLanguage) = if (lang == AppLanguage.SW) "Tarehe" else "Date"
    
    fun saveBtn(lang: AppLanguage) = if (lang == AppLanguage.SW) "Hifadhi" else "Save"
    fun cancelBtn(lang: AppLanguage) = if (lang == AppLanguage.SW) "Ghairi" else "Cancel"
    fun deleteBtn(lang: AppLanguage) = if (lang == AppLanguage.SW) "Futa" else "Delete"
    fun editBtn(lang: AppLanguage) = if (lang == AppLanguage.SW) "Rekebisha" else "Edit"
    fun closeBtn(lang: AppLanguage) = if (lang == AppLanguage.SW) "Funga" else "Close"
    
    fun resetData(lang: AppLanguage) = if (lang == AppLanguage.SW) "Futa Takwimu Zote" else "Reset All Data"
    fun resetConfirmTitle(lang: AppLanguage) = if (lang == AppLanguage.SW) "Una uhakika unataka kufuta miamala yote?" else "Are you sure you want to reset all data?"
    fun resetConfirmSub(lang: AppLanguage) = if (lang == AppLanguage.SW) "Hatua hii itafuta kabisa historia ya miamala yako yote na salio litakuwa TZS 0." else "This will permanently remove all your transaction history."
    
    fun quickAmounts(lang: AppLanguage) = if (lang == AppLanguage.SW) "Kiasi cha Haraka:" else "Quick Amounts:"
    
    fun paymentMethodsList(lang: AppLanguage): List<String> = listOf(
        "M-Pesa 🟢",
        "Airtel Money 🔴",
        "Tigo Pesa 🔵",
        "Halopesa 🟠",
        "Benki (NMB/CRDB/N.K) 🏦",
        if (lang == AppLanguage.SW) "Pesa Taslimu (Cash) 💵" else "Cash 💵"
    )
}
