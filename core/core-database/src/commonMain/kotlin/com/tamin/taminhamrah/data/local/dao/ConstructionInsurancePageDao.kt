package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.ConstructionBeneficiaryPageEntity
import com.tamin.taminhamrah.data.local.entity.InstallmentConstructionPageEntity
import com.tamin.taminhamrah.data.local.entity.InstallmentDebitPageEntity
import com.tamin.taminhamrah.data.local.entity.InstallmentLetterPageEntity

@Dao
interface ConstructionInsurancePageDao {

    // ---- Beneficiaries ----

    @Query(
        "SELECT * FROM construction_beneficiary_pages WHERE listKey = :listKey " +
            "ORDER BY position ASC LIMIT :limit OFFSET :offset"
    )
    suspend fun getBeneficiariesSlice(listKey: String, limit: Int, offset: Int): List<ConstructionBeneficiaryPageEntity>

    @Upsert
    suspend fun upsertBeneficiaries(rows: List<ConstructionBeneficiaryPageEntity>)

    @Query("DELETE FROM construction_beneficiary_pages WHERE listKey = :listKey")
    suspend fun clearBeneficiaries(listKey: String)

    @Transaction
    suspend fun replaceBeneficiaries(listKey: String, rows: List<ConstructionBeneficiaryPageEntity>) {
        clearBeneficiaries(listKey)
        upsertBeneficiaries(rows)
    }

    // ---- Installment letters ----

    @Query(
        "SELECT * FROM installment_letter_pages WHERE listKey = :listKey " +
            "ORDER BY position ASC LIMIT :limit OFFSET :offset"
    )
    suspend fun getInstallmentLettersSlice(listKey: String, limit: Int, offset: Int): List<InstallmentLetterPageEntity>

    @Upsert
    suspend fun upsertInstallmentLetters(rows: List<InstallmentLetterPageEntity>)

    @Query("DELETE FROM installment_letter_pages WHERE listKey = :listKey")
    suspend fun clearInstallmentLetters(listKey: String)

    @Transaction
    suspend fun replaceInstallmentLetters(listKey: String, rows: List<InstallmentLetterPageEntity>) {
        clearInstallmentLetters(listKey)
        upsertInstallmentLetters(rows)
    }

    // ---- Debit list ----

    @Query(
        "SELECT * FROM installment_debit_pages WHERE listKey = :listKey " +
            "ORDER BY position ASC LIMIT :limit OFFSET :offset"
    )
    suspend fun getDebitsSlice(listKey: String, limit: Int, offset: Int): List<InstallmentDebitPageEntity>

    @Upsert
    suspend fun upsertDebits(rows: List<InstallmentDebitPageEntity>)

    @Query("DELETE FROM installment_debit_pages WHERE listKey = :listKey")
    suspend fun clearDebits(listKey: String)

    @Transaction
    suspend fun replaceDebits(listKey: String, rows: List<InstallmentDebitPageEntity>) {
        clearDebits(listKey)
        upsertDebits(rows)
    }

    // ---- Installments ----

    @Query(
        "SELECT * FROM installment_construction_pages WHERE listKey = :listKey " +
            "ORDER BY position ASC LIMIT :limit OFFSET :offset"
    )
    suspend fun getInstallmentsSlice(listKey: String, limit: Int, offset: Int): List<InstallmentConstructionPageEntity>

    @Upsert
    suspend fun upsertInstallments(rows: List<InstallmentConstructionPageEntity>)

    @Query("DELETE FROM installment_construction_pages WHERE listKey = :listKey")
    suspend fun clearInstallments(listKey: String)

    @Transaction
    suspend fun replaceInstallments(listKey: String, rows: List<InstallmentConstructionPageEntity>) {
        clearInstallments(listKey)
        upsertInstallments(rows)
    }
}
