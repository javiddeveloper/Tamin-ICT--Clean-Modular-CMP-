package com.tamin.taminhamrah.data.local.services

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tamin.taminhamrah.data.local.services.entity.AppliedServiceEntity

@Dao
interface ServiceDao {

    @Query("SELECT * FROM AppliedServicesTbl where visitCount > 0 and type=:userType order by visitCount desc limit 5")
    fun getAppliedService(userType: Int): LiveData<List<AppliedServiceEntity>>

    @Query("SELECT COUNT(*) FROM AppliedServicesTbl where  type=:userType")
   suspend fun getAllServiceSize(userType: Int): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllServices(list: List<AppliedServiceEntity>): LongArray

    @Query("UPDATE  AppliedServicesTbl set lastVisitTime =:time,visitCount=(select visitCount from AppliedServicesTbl where id=:serviceId)+1  where id=:serviceId  ")
    suspend fun updateAppliedService(serviceId: Int, time: Long): Int

}
