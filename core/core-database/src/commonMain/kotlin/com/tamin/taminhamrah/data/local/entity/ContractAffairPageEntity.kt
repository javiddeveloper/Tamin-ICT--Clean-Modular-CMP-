package com.tamin.taminhamrah.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity

/**
 * One row of the امور قراردادها و پرداخت contracts list, cached offline-first.
 *
 * Reuses [ContractEntity]'s columns (the affair's contract model has the same shape) but lives in
 * its own table, so the contracts feature's `contracts` cache and this list never touch each other.
 * [listKey] identifies the list (its filters/sorts), [position] is the row's offset in the
 * server's order, so a page is read back with `ORDER BY position LIMIT OFFSET`.
 */
@Entity(
    tableName = "contract_affair_pages",
    primaryKeys = ["listKey", "position"],
)
data class ContractAffairPageEntity(
    val listKey: String,
    val position: Int,
    @Embedded val contract: ContractEntity,
)
