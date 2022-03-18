package com.sgcdeveloper.moneymanager.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sgcdeveloper.moneymanager.data.db.entry.BudgetEntry

@Dao
interface BudgetDao {

    @Query("SELECT * FROM BudgetEntry")
    fun getBudgetsOnce(): List<BudgetEntry>

    @Query("SELECT * FROM BudgetEntry")
    fun getBudgets(): LiveData<List<BudgetEntry>>

    @Query("SELECT * FROM BudgetEntry")
    suspend fun getAsyncWBudgets(): List<BudgetEntry>

    @Query("SELECT * FROM BudgetEntry WHERE id == :id")
    suspend fun getBudget(id:Long): BudgetEntry

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budgetEntry: BudgetEntry):Long

    @Query("DELETE FROM BudgetEntry WHERE id = :id")
    suspend fun removeBudget(id: Long)

    @Query("DELETE FROM BudgetEntry")
    suspend fun deleteAllBudgets()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgets(budgetEntries: List<BudgetEntry>)
}
