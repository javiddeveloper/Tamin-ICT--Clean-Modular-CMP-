package com.tamin.taminhamrah.data.repository.common

import com.tamin.taminhamrah.dataSource.commonSource.MenuLocalDataSource
import com.tamin.taminhamrah.data.mapper.toDomainOrNull
import com.tamin.taminhamrah.model.common.FeatureDN
import com.tamin.taminhamrah.repository.common.FeatureFlagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * EM-2381 feature-flag repository.
 *
 * Serves the tidied offline menu ([MenuLocalDataSource]) parsed through the real
 * [com.tamin.taminhamrah.model.common.MainServiceDto] → domain mapper. When the backend exposes
 * the extended feature-flag fields, add the remote call here and fall back to offline on failure:
 *
 * ```
 * override fun getFeatures(forceRefresh: Boolean) = flow {
 *     val dtos = runCatching { commonRemoteDataSource.getMainMenu(versionCode, forceRefresh) }
 *         .getOrElse { menuLocalDataSource.getOfflineMenu() }
 *     emit(dtos.mapNotNull { it.toDomainOrNull() })
 * }
 * ```
 */
class FeatureFlagRepositoryImpl(
    private val menuLocalDataSource: MenuLocalDataSource,
) : FeatureFlagRepository {

    override fun getFeatures(forceRefresh: Boolean): Flow<List<FeatureDN>> = flow {
        // TODO(EM-2381): try the remote menu first when the backend supports the extended fields,
        //  falling back to the offline source on failure.
        emit(menuLocalDataSource.getOfflineMenu().mapNotNull { it.toDomainOrNull() })
    }
}
