package com.tamin.taminhamrah.data.local.ai.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tamin.taminhamrah.data.entity.ai.AiHistoryCategoryEntity
import com.tamin.taminhamrah.data.entity.ai.CategoryWithLastMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentChatCategoryDao {

    @Query("SELECT * FROM aicategory  WHERE userNationalCode = :userNationalCode ORDER BY  lastMessageDate DESC")
    fun getAll(userNationalCode: String?): Flow<List<AiHistoryCategoryEntity>>

    @Query("DELETE FROM aicategory WHERE userNationalCode = :userNationCode")
    fun deleteAll(userNationCode: String?): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: AiHistoryCategoryEntity)

    @Query("SELECT * FROM aicategory WHERE id = :id")
    suspend fun getCategory(id: String): AiHistoryCategoryEntity?

    @Query("DELETE FROM aicategory WHERE id = :id")
    suspend fun deleteCategory(id: String): Int

    @Delete
    suspend fun deleteCategory(category: AiHistoryCategoryEntity): Int

    @Query("DELETE FROM aicategory")
    suspend fun deleteAll()

    @Query("UPDATE aicategory SET title = :title WHERE id = :id")
    fun updateCategory(id: String, title: String): Int

    @Update
    suspend fun updateCategory(categoryEntity: AiHistoryCategoryEntity): Int?

    @Query("""
        UPDATE aicategory 
        SET lastMessageDate = :lastMessageDate, 
            messageCount = messageCount + 1 
        WHERE id = :categoryId
    """)
    suspend fun updateCategoryStats(categoryId: String, lastMessageDate: Long)

    @Query("""
        SELECT 
            c.id, c.title, c.date, c.lastMessageDate, c.messageCount, c.userNationalCode,
            (SELECT jsonData FROM chat_messages 
             WHERE categoryId = c.id 
             ORDER BY messageOrder DESC 
             LIMIT 1) as lastMessage
        FROM aicategory c
        ORDER BY c.lastMessageDate DESC
    """)
    suspend fun getCategoriesWithLastMessage(): List<CategoryWithLastMessage>
}
