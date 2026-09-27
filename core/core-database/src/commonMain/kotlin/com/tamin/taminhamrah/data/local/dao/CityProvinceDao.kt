package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.CityEntity
import com.tamin.taminhamrah.data.local.entity.ProvinceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityProvinceDao {
    @Upsert
    suspend fun upsertCity(city: CityEntity)

    @Upsert
    suspend fun upsertCities(cities: List<CityEntity>)

    @Query("SELECT * FROM cities WHERE cityCode = :cityCode LIMIT 1")
    fun getCity(cityCode: String): Flow<CityEntity?>

    @Query("SELECT * FROM cities WHERE provinceCode = :provinceCode ORDER BY cityName ASC")
    fun getCitiesByProvinceCode(provinceCode: String): Flow<List<CityEntity>>

    /**
     * One page of cached cities, filtered like the server's city search (`null` = no filter).
     * Province codes are compared ignoring leading zeros — endpoints disagree on "07" vs "7".
     */
    @Query(
        "SELECT * FROM cities " +
            "WHERE (:cityName IS NULL OR cityName LIKE '%' || :cityName || '%') " +
            "AND (:provinceCode IS NULL OR LTRIM(provinceCode, '0') = LTRIM(:provinceCode, '0')) " +
            "ORDER BY cityName ASC, cityCode ASC " +
            "LIMIT :limit OFFSET :offset"
    )
    suspend fun getCitiesSlice(
        cityName: String?,
        provinceCode: String?,
        limit: Int,
        offset: Int,
    ): List<CityEntity>

    /** Deletes exactly the rows [getCitiesSlice] would return for the same filter (any offset). */
    @Query(
        "DELETE FROM cities " +
            "WHERE (:cityName IS NULL OR cityName LIKE '%' || :cityName || '%') " +
            "AND (:provinceCode IS NULL OR LTRIM(provinceCode, '0') = LTRIM(:provinceCode, '0'))"
    )
    suspend fun clearCitiesMatching(cityName: String?, provinceCode: String?)

    /**
     * A fresh first page from the network replaces the whole cached list for that filter —
     * atomically, so observers never see it half-empty. Other filters' rows are untouched.
     */
    @Transaction
    suspend fun replaceCitiesMatching(cityName: String?, provinceCode: String?, cities: List<CityEntity>) {
        clearCitiesMatching(cityName, provinceCode)
        upsertCities(cities)
    }

    @Query("DELETE FROM cities WHERE provinceCode = :provinceCode")
    suspend fun clearCitiesByProvinceCode(provinceCode: String)

    @Transaction
    suspend fun replaceCitiesForProvince(provinceCode: String, cities: List<CityEntity>) {
        clearCitiesByProvinceCode(provinceCode)
        upsertCities(cities)
    }

    @Upsert
    suspend fun upsertProvince(province: ProvinceEntity)

    @Upsert
    suspend fun upsertProvinces(provinces: List<ProvinceEntity>)

    @Query("SELECT * FROM provinces WHERE provinceCode = :provinceCode LIMIT 1")
    fun getProvince(provinceCode: String): Flow<ProvinceEntity?>

    @Query("SELECT * FROM provinces ORDER BY provinceName ASC")
    fun getAllProvinces(): Flow<List<ProvinceEntity>>

    @Query("DELETE FROM provinces")
    suspend fun clearProvinces()

    @Transaction
    suspend fun replaceAllProvinces(provinces: List<ProvinceEntity>) {
        clearProvinces()
        upsertProvinces(provinces)
    }
}
