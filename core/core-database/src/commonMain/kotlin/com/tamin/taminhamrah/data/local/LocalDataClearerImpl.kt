package com.tamin.taminhamrah.data.local

import androidx.room.Transactor
import androidx.room.execSQL
import androidx.room.useWriterConnection
import com.tamin.taminhamrah.repository.LocalDataClearer

/**
 * Empties every user table, which is what logging out has to leave behind.
 *
 * `RoomDatabase.clearAllTables()` is Android-only — it is absent from Room's native klib in every
 * release — so the tables are enumerated and emptied directly instead. Enumerating rather than
 * listing them means a newly added `@Entity` is cleared too, without anyone remembering this file.
 */
class LocalDataClearerImpl(
    private val database: TaminXDatabase
) : LocalDataClearer {

    override suspend fun clearAll() {
        database.useWriterConnection { transactor ->
            transactor.withTransaction(Transactor.SQLiteTransactionType.IMMEDIATE) {
                // `PRAGMA foreign_keys` cannot be changed inside a transaction; deferring instead
                // lets parents be emptied before children and re-arms the checks on commit. Without
                // it, deleting in table order trips any FK that outlives its parent row.
                execSQL("PRAGMA defer_foreign_keys = TRUE")

                val tables = usePrepared(SELECT_USER_TABLES) { statement ->
                    buildList { while (statement.step()) add(statement.getText(0)) }
                }
                // Deleted row by row rather than dropped: the schema, and so Room's identity hash,
                // must survive — a dropped table reads as a schema change on next open.
                tables.forEach { execSQL("DELETE FROM `$it`") }
            }
        }
    }
}

/**
 * Every table the app owns.
 *
 * `sqlite_%` is SQLite's own bookkeeping, `room_%` is Room's identity and invalidation state, and
 * `android_metadata` is the platform's — clearing any of them corrupts the database rather than
 * emptying it. No entity in this schema uses those prefixes.
 */
private const val SELECT_USER_TABLES = """
    SELECT name FROM sqlite_master
    WHERE type = 'table'
      AND name NOT LIKE 'sqlite_%'
      AND name NOT LIKE 'room_%'
      AND name != 'android_metadata'
"""
