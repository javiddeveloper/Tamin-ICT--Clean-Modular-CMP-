package com.tamin.taminhamrah.data.repository.workshops

import com.tamin.taminhamrah.data.local.dao.EmployerServicesPageDao
import com.tamin.taminhamrah.data.local.entity.WorkshopContractRowPageEntity
import com.tamin.taminhamrah.data.local.entity.WorkshopWithoutContractPageEntity

/** In-memory page tables keyed like Room's composite primary key (listKey, position). */
class FakeEmployerServicesPageDao : EmployerServicesPageDao {
    private val withoutContract = mutableListOf<WorkshopWithoutContractPageEntity>()
    private val contractRows = mutableListOf<WorkshopContractRowPageEntity>()

    override suspend fun getWorkshopsWithoutContractSlice(listKey: String, limit: Int, offset: Int) =
        withoutContract.filter { it.listKey == listKey }.sortedBy { it.position }.drop(offset).take(limit)

    override suspend fun upsertWorkshopsWithoutContract(rows: List<WorkshopWithoutContractPageEntity>) {
        rows.forEach { row -> withoutContract.removeAll { it.listKey == row.listKey && it.position == row.position } }
        withoutContract += rows
    }

    override suspend fun clearWorkshopsWithoutContract(listKey: String) {
        withoutContract.removeAll { it.listKey == listKey }
    }

    override suspend fun getContractRowsSlice(listKey: String, limit: Int, offset: Int) =
        contractRows.filter { it.listKey == listKey }.sortedBy { it.position }.drop(offset).take(limit)

    override suspend fun upsertContractRows(rows: List<WorkshopContractRowPageEntity>) {
        rows.forEach { row -> contractRows.removeAll { it.listKey == row.listKey && it.position == row.position } }
        contractRows += rows
    }

    override suspend fun clearContractRows(listKey: String) {
        contractRows.removeAll { it.listKey == listKey }
    }
}
