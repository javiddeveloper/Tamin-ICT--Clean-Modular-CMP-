package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.CityEntity
import com.tamin.taminhamrah.data.local.entity.ProvinceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityProvinceDao {
    @Upsert
    suspend fun upsertCity(city: CityEntity)

    @Query("SELECT * FROM cities WHERE cityCode = :cityCode LIMIT 1")
    fun getCity(cityCode: String): Flow<CityEntity?>

    @Upsert
    suspend fun upsertProvince(province: ProvinceEntity)

    @Query("SELECT * FROM provinces WHERE provinceCode = :provinceCode LIMIT 1")
    fun getProvince(provinceCode: String): Flow<ProvinceEntity?>

    @Query("SELECT * FROM provinces ORDER BY provinceName ASC")
    fun getAllProvinces(): Flow<List<ProvinceEntity>>

    @Query("SELECT * FROM cities WHERE provinceCode = :provinceCode ORDER BY cityName ASC")
    fun getCitiesByProvinceCode(provinceCode: String): Flow<List<CityEntity>>
}
