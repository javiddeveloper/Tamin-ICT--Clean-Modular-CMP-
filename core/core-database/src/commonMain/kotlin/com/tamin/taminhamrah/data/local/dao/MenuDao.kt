package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tamin.taminhamrah.data.local.entity.MenuEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {
    /**
     * `sorting` is what the server orders the menu by, but it is null for every locally served row,
     * so `id` is the tie-break that actually decides the order on screen. Without it SQLite is free
     * to return equal-keyed rows in any order.
     */
    @Query("SELECT * FROM menu_items ORDER BY sorting ASC, id ASC")
    fun getMenuItems(): Flow<List<MenuEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(menuItems: List<MenuEntity>)

    @Query("DELETE FROM menu_items")
    suspend fun clearMenu()

    /**
     * Inserting alone only ever adds or overwrites, so a service that was removed from the menu — or
     * one whose id changed — stays cached forever and keeps rendering next to its replacement. The
     * delete and the insert run in one transaction so observers never see the table empty.
     */
    @Transaction
    suspend fun replaceAllMenuItems(menuItems: List<MenuEntity>) {
        clearMenu()
        insertMenuItems(menuItems)
    }
}
