package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.PersonalInboxItemEntity
import com.tamin.taminhamrah.data.local.entity.PersonalInboxSizeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalInboxDao {

    @Query("SELECT * FROM personal_inbox_items ORDER BY sentDate DESC")
    fun getInboxItems(): Flow<List<PersonalInboxItemEntity>>

    @Upsert
    suspend fun upsertInboxItems(items: List<PersonalInboxItemEntity>)

    @Query("DELETE FROM personal_inbox_items")
    suspend fun clearInboxItems()

    @Transaction
    suspend fun replaceAllInboxItems(items: List<PersonalInboxItemEntity>) {
        clearInboxItems()
        upsertInboxItems(items)
    }

    @Query("SELECT * FROM personal_inbox_size WHERE id = :id LIMIT 1")
    fun getInboxSize(id: Int = PersonalInboxSizeEntity.SINGLE_ROW_ID): Flow<PersonalInboxSizeEntity?>

    @Upsert
    suspend fun upsertInboxSize(size: PersonalInboxSizeEntity)
}
