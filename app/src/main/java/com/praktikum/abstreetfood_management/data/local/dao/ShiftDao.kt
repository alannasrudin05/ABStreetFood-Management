package com.praktikum.abstreetfood_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.praktikum.abstreetfood_management.data.local.entity.ShiftEntity

@Dao
interface ShiftDao {
    @Insert
    suspend fun openShift(shift: ShiftEntity)

    @Update
    suspend fun closeShift(shift: ShiftEntity)

    @Query("SELECT * FROM shifts WHERE userId = :userId AND isClosed = 0 LIMIT 1")
    suspend fun getCurrentOpenShift(userId: String): ShiftEntity?
}