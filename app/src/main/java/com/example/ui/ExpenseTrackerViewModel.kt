package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.TransactionEntity
import com.example.data.TransactionRepository
import com.example.data.TransactionType
import com.example.ui.theme.ThemeMode
import com.example.util.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FilterType {
    ALL,
    EXPENSES,
    INCOME
}

data class DashboardUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val filteredTransactions: List<TransactionEntity> = emptyList(),
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val selectedFilter: FilterType = FilterType.ALL,
    val searchQuery: String = "",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.SW,
    val categoryExpenses: Map<String, Double> = emptyMap()
)

class ExpenseTrackerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository
    private val prefs = application.getSharedPreferences("tzs_expense_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(loadSavedThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode

    private val _language = MutableStateFlow(loadSavedLanguage())
    val language: StateFlow<AppLanguage> = _language

    private val _selectedFilter = MutableStateFlow(FilterType.ALL)
    private val _searchQuery = MutableStateFlow("")

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TransactionRepository(database.transactionDao())
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.allTransactions,
        _selectedFilter,
        _searchQuery,
        _themeMode,
        _language
    ) { transactions, filter, query, theme, lang ->
        var incomeSum = 0.0
        var expenseSum = 0.0
        val categoryExpenseMap = mutableMapOf<String, Double>()

        for (tx in transactions) {
            if (tx.type == TransactionType.INCOME) {
                incomeSum += tx.amount
            } else {
                expenseSum += tx.amount
                categoryExpenseMap[tx.category] = (categoryExpenseMap[tx.category] ?: 0.0) + tx.amount
            }
        }

        val balance = incomeSum - expenseSum

        val filtered = transactions.filter { tx ->
            val matchesFilter = when (filter) {
                FilterType.ALL -> true
                FilterType.EXPENSES -> tx.type == TransactionType.EXPENSE
                FilterType.INCOME -> tx.type == TransactionType.INCOME
            }
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                tx.description.contains(query, ignoreCase = true) ||
                        tx.category.contains(query, ignoreCase = true)
            }
            matchesFilter && matchesQuery
        }

        DashboardUiState(
            transactions = transactions,
            filteredTransactions = filtered,
            totalBalance = balance,
            totalIncome = incomeSum,
            totalExpense = expenseSum,
            selectedFilter = filter,
            searchQuery = query,
            themeMode = theme,
            language = lang,
            categoryExpenses = categoryExpenseMap
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(themeMode = _themeMode.value, language = _language.value)
    )

    fun setFilter(filter: FilterType) {
        _selectedFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleLanguage() {
        val nextLang = if (_language.value == AppLanguage.SW) AppLanguage.EN else AppLanguage.SW
        _language.value = nextLang
        prefs.edit().putString("app_language", nextLang.name).apply()
    }

    private fun loadSavedLanguage(): AppLanguage {
        val saved = prefs.getString("app_language", AppLanguage.SW.name)
        return try {
            AppLanguage.valueOf(saved ?: AppLanguage.SW.name)
        } catch (e: Exception) {
            AppLanguage.SW
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    fun toggleDarkMode() {
        val nextMode = when (_themeMode.value) {
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.SYSTEM -> ThemeMode.DARK
        }
        setThemeMode(nextMode)
    }

    private fun loadSavedThemeMode(): ThemeMode {
        val saved = prefs.getString("theme_mode", ThemeMode.SYSTEM.name)
        return try {
            ThemeMode.valueOf(saved ?: ThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }

    fun addTransaction(
        type: TransactionType,
        amount: Double,
        category: String,
        description: String,
        dateMillis: Long
    ) {
        viewModelScope.launch {
            val transaction = TransactionEntity(
                type = type,
                amount = amount,
                category = category.trim(),
                description = description.trim(),
                dateMillis = dateMillis
            )
            repository.insert(transaction)
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.update(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }

    fun clearAllTransactions() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}
