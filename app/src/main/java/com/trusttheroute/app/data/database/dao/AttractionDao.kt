package com.trusttheroute.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trusttheroute.app.data.database.entity.AttractionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttractionDao {
    @Query("SELECT * FROM attractions WHERE routeId = :routeId")
    fun getAttractionsByRouteId(routeId: String): Flow<List<AttractionEntity>>

    @Query("SELECT * FROM attractions WHERE id = :attractionId")
    suspend fun getAttractionById(attractionId: String): AttractionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttraction(attraction: AttractionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttractions(attractions: List<AttractionEntity>)

    @Query("DELETE FROM attractions WHERE routeId = :routeId")
    suspend fun deleteAttractionsByRouteId(routeId: String)

    @Query("DELETE FROM attractions")
    suspend fun deleteAllAttractions()
}
