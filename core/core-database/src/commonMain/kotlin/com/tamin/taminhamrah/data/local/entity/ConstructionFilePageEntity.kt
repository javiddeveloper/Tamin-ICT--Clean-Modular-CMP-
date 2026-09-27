package com.tamin.taminhamrah.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity

/**
 * One row of the paged construction-files list, cached offline-first.
 *
 * Kept apart from `construction_files`, which the file-search flow replaces wholesale with its
 * own results. [listKey] identifies the list (its filters/sorts), [position] is the row's
 * offset in the server's order, so a page is read back with `ORDER BY position LIMIT OFFSET`.
 */
@Entity(
    tableName = "construction_file_pages",
    primaryKeys = ["listKey", "position"],
)
data class ConstructionFilePageEntity(
    val listKey: String,
    val position: Int,
    @Embedded val file: ConstructionFileEntity,
)
