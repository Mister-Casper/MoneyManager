package com.sgcdeveloper.moneymanager.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sgcdeveloper.moneymanager.data.db.entry.GoalEntry

@Dao
interface GoalDao {

    @Query("SELECT * FROM GoalEntry")
    fun getGoalsOnce(): List<GoalEntry>

    @Query("SELECT * FROM GoalEntry")
    fun getGoals(): LiveData<List<GoalEntry>>

    @Query("SELECT * FROM GoalEntry")
    suspend fun getAsyncWGoals(): List<GoalEntry>

    @Query("SELECT * FROM GoalEntry WHERE id == :id")
    suspend fun getGoal(id:Long): GoalEntry

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(budgetEntry: GoalEntry):Long

    @Query("DELETE FROM GoalEntry WHERE id = :id")
    suspend fun removeGoal(id: Long)

    @Query("DELETE FROM GoalEntry")
    suspend fun deleteAllGoals()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(budgetEntries: List<GoalEntry>)
}
