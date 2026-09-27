package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.InspectionBranchPageEntity
import com.tamin.taminhamrah.data.local.entity.InspectionJobPageEntity
import com.tamin.taminhamrah.data.local.entity.InspectionPerformedPageEntity

@Dao
interface InspectionDao {

    // ---- Inspections ----

    @Query(
        "SELECT * FROM inspection_performed_pages WHERE listKey = :listKey " +
            "ORDER BY position ASC LIMIT :limit OFFSET :offset"
    )
    suspend fun getInspectionsSlice(listKey: String, limit: Int, offset: Int): List<InspectionPerformedPageEntity>

    @Upsert
    suspend fun upsertInspections(rows: List<InspectionPerformedPageEntity>)

    @Query("DELETE FROM inspection_performed_pages WHERE listKey = :listKey")
    suspend fun clearInspections(listKey: String)

    @Transaction
    suspend fun replaceInspections(listKey: String, rows: List<InspectionPerformedPageEntity>) {
        clearInspections(listKey)
        upsertInspections(rows)
    }

    // ---- Branches ----

    @Query(
        "SELECT * FROM inspection_branch_pages WHERE listKey = :listKey " +
            "ORDER BY position ASC LIMIT :limit OFFSET :offset"
    )
    suspend fun getBranchesSlice(listKey: String, limit: Int, offset: Int): List<InspectionBranchPageEntity>

    @Upsert
    suspend fun upsertBranches(rows: List<InspectionBranchPageEntity>)

    @Query("DELETE FROM inspection_branch_pages WHERE listKey = :listKey")
    suspend fun clearBranches(listKey: String)

    @Transaction
    suspend fun replaceBranches(listKey: String, rows: List<InspectionBranchPageEntity>) {
        clearBranches(listKey)
        upsertBranches(rows)
    }

    // ---- Job titles ----

    @Query(
        "SELECT * FROM inspection_job_pages WHERE listKey = :listKey " +
            "ORDER BY position ASC LIMIT :limit OFFSET :offset"
    )
    suspend fun getJobsSlice(listKey: String, limit: Int, offset: Int): List<InspectionJobPageEntity>

    @Upsert
    suspend fun upsertJobs(rows: List<InspectionJobPageEntity>)

    @Query("DELETE FROM inspection_job_pages WHERE listKey = :listKey")
    suspend fun clearJobs(listKey: String)

    @Transaction
    suspend fun replaceJobs(listKey: String, rows: List<InspectionJobPageEntity>) {
        clearJobs(listKey)
        upsertJobs(rows)
    }
}
