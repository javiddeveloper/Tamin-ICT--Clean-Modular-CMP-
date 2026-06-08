/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.dataSource.userSource

import com.tamin.core.network.model.user.IdentityInfoDto
import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.user.TaminRelationResponse

interface UserRemoteDataSource {
    suspend fun getIdentityInfo(): IdentityInfoDto
    suspend fun getUserProfileImage(): String
    suspend fun fetchTaminRelation(): TaminRelationResponse
    suspend fun sendImageRequest(branchCode: String, filter: List<ApiFilterDN>): String
}
