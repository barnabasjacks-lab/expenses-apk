package com.example.data

import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {
    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()

    suspend fun insert(transaction: TransactionEntity): Long = dao.insertTransaction(transaction)

    suspend fun update(transaction: TransactionEntity) = dao.updateTransaction(transaction)

    suspend fun delete(transaction: TransactionEntity) = dao.deleteTransaction(transaction)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun deleteAll() = dao.deleteAll()
}
