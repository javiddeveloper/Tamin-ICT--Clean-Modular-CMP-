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

    @Query("DELETE FROM cities WHERE provinceCode = :provinceCode")
    suspend fun clearCitiesByProvinceCode(provinceCode: String)

    @Transaction
    suspend fun replaceCitiesForProvince(provinceCode: String, cities: List<CityEntity>) {
        clearCitiesByProvinceCode(provinceCode)
        upsertCities(cities)
    }

    @Upsert
    suspend fun upsertProvince(province: ProvinceEntity)

    @Query("SELECT * FROM provinces WHERE provinceCode = :provinceCode LIMIT 1")
    fun getProvince(provinceCode: String): Flow<ProvinceEntity?>
}
