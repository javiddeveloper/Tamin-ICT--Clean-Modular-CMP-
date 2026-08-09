package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.DrugAllergyEntity
import com.tamin.taminhamrah.data.local.entity.HospitalizationEntity
import com.tamin.taminhamrah.data.local.entity.PatientGeneralEntity
import com.tamin.taminhamrah.data.local.entity.PatientImagingEntity
import com.tamin.taminhamrah.data.local.entity.PatientLabEntity
import com.tamin.taminhamrah.data.local.entity.PatientSelfDeclarativeEntity
import com.tamin.taminhamrah.data.local.entity.PatientVisitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {

    // --- Patient general (single record per natCode) ---
    @Upsert
    suspend fun upsertGeneral(item: PatientGeneralEntity)

    @Query("SELECT * FROM patient_general WHERE natCode = :natCode LIMIT 1")
    fun getGeneral(natCode: String): Flow<PatientGeneralEntity?>

    // --- Self-declarative (single record per natCode) ---
    @Upsert
    suspend fun upsertSelfDeclarative(item: PatientSelfDeclarativeEntity)

    @Query("SELECT * FROM patient_self_declarative WHERE natCode = :natCode LIMIT 1")
    fun getSelfDeclarative(natCode: String): Flow<PatientSelfDeclarativeEntity?>

    // --- Drug allergies (per-patient list) ---
    @Insert
    suspend fun insertDrugAllergies(items: List<DrugAllergyEntity>)

    @Query("DELETE FROM patient_drug_allergies WHERE natCode = :natCode")
    suspend fun clearDrugAllergies(natCode: String)

    @Query("SELECT * FROM patient_drug_allergies WHERE natCode = :natCode")
    fun getDrugAllergies(natCode: String): Flow<List<DrugAllergyEntity>>

    // Atomically replaces the cached drug-allergy list so a failed insert
    // can never leave the cache cleared with no rows re-inserted.
    @Transaction
    suspend fun replaceDrugAllergies(natCode: String, items: List<DrugAllergyEntity>) {
        clearDrugAllergies(natCode)
        insertDrugAllergies(items)
    }

    // --- Hospitalizations (per-patient list) ---
    @Insert
    suspend fun insertHospitalizations(items: List<HospitalizationEntity>)

    @Query("DELETE FROM patient_hospitalizations WHERE natCode = :natCode")
    suspend fun clearHospitalizations(natCode: String)

    @Query("SELECT * FROM patient_hospitalizations WHERE natCode = :natCode")
    fun getHospitalizations(natCode: String): Flow<List<HospitalizationEntity>>

    @Transaction
    suspend fun replaceHospitalizations(natCode: String, items: List<HospitalizationEntity>) {
        clearHospitalizations(natCode)
        insertHospitalizations(items)
    }

    // --- Visits (per-patient list) ---
    @Insert
    suspend fun insertVisits(items: List<PatientVisitEntity>)

    @Query("DELETE FROM patient_visits WHERE natCode = :natCode")
    suspend fun clearVisits(natCode: String)

    @Query("SELECT * FROM patient_visits WHERE natCode = :natCode")
    fun getVisits(natCode: String): Flow<List<PatientVisitEntity>>

    @Transaction
    suspend fun replaceVisits(natCode: String, items: List<PatientVisitEntity>) {
        clearVisits(natCode)
        insertVisits(items)
    }

    // --- Labs (per-patient list) ---
    @Insert
    suspend fun insertLabs(items: List<PatientLabEntity>)

    @Query("DELETE FROM patient_labs WHERE natCode = :natCode")
    suspend fun clearLabs(natCode: String)

    @Query("SELECT * FROM patient_labs WHERE natCode = :natCode")
    fun getLabs(natCode: String): Flow<List<PatientLabEntity>>

    @Transaction
    suspend fun replaceLabs(natCode: String, items: List<PatientLabEntity>) {
        clearLabs(natCode)
        insertLabs(items)
    }

    // --- Imaging (per-patient list) ---
    @Insert
    suspend fun insertImaging(items: List<PatientImagingEntity>)

    @Query("DELETE FROM patient_imaging WHERE natCode = :natCode")
    suspend fun clearImaging(natCode: String)

    @Query("SELECT * FROM patient_imaging WHERE natCode = :natCode")
    fun getImaging(natCode: String): Flow<List<PatientImagingEntity>>

    @Transaction
    suspend fun replaceImaging(natCode: String, items: List<PatientImagingEntity>) {
        clearImaging(natCode)
        insertImaging(items)
    }
}
