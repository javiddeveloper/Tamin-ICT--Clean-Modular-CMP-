package com.tamin.taminhamrah.data.local.ai.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tamin.taminhamrah.data.entity.ai.ChatMessageEntity

@Dao
interface ChatMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Query("SELECT * FROM chat_messages WHERE categoryId = :categoryId ORDER BY messageOrder ASC")
    suspend fun getMessagesByCategory(categoryId: String): List<ChatMessageEntity>

    @Query("SELECT * FROM chat_messages WHERE categoryId = :categoryId ORDER BY messageOrder ASC")
    fun getMessagesByCategoryPaging(categoryId: String): PagingSource<Int, ChatMessageEntity>

    @Query("SELECT MAX(messageOrder) FROM chat_messages WHERE categoryId = :categoryId")
    suspend fun getLastMessageOrder(categoryId: String): Int?

    @Query("DELETE FROM chat_messages WHERE categoryId = :categoryId")
    suspend fun deleteMessagesByCategory(categoryId: String)

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllMessages()
}
