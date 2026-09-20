package com.tamin.taminhamrah.data.repository.constructionInsurance

import com.tamin.taminhamrah.data.local.dao.ConstructionFileDao
import com.tamin.taminhamrah.data.local.entity.ConstructionFileEntity
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.dataSource.constructionInsurance.ConstructionInsuranceRemoteDataSource
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.constructionInsurance.BuildingRequestSummaryDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.WorkshopIdInfoDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class ConstructionInsuranceRepositoryImpl(
    private val remoteDataSource: ConstructionInsuranceRemoteDataSource,
    private val constructionFileDao: ConstructionFileDao,
) : ConstructionInsuranceRepository {

    override fun getConstructionFiles(
        search: ConstructionFileSearchParamsDN?
    ): Flow<List<ConstructionFileDN>> {
        return flow {
            val matchingLocalFiles = constructionFileDao.getConstructionFiles().first()
                .filter { it.matches(search) }
            emit(matchingLocalFiles.map { it.toDomain() })

            try {
                val query = buildQuery(search)
                val response = remoteDataSource.getConstructionFiles(query)
                val remoteFiles = response.list.orEmpty()
                constructionFileDao.replaceAll(remoteFiles.map { it.toEntity() })
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (matchingLocalFiles.isEmpty()) {
                    throw e
                }
            }

            emitAll(
                constructionFileDao.getConstructionFiles().map { entities ->
                    entities.filter { it.matches(search) }.map { it.toDomain() }
                }
            )
        }.distinctUntilChanged()
    }

    /**
     * Same EQ-per-non-blank-field semantics as [buildQuery], applied to the local cache so a
     * search for one file never surfaces another file's cached row while the network call is
     * in flight or has failed.
     */
    private fun ConstructionFileEntity.matches(search: ConstructionFileSearchParamsDN?): Boolean {
        if (search == null) return true
        val fileNo = search.fileNo
        if (!fileNo.isNullOrBlank() && fileNumber.toString() != fileNo) return false
        val reqNo = search.reqNo
        if (!reqNo.isNullOrBlank() && requestNumber?.toString() != reqNo) return false
        val workshopId = search.workshopId
        if (!workshopId.isNullOrBlank() && this.workshopId != workshopId) return false
        val branchCode = search.branchCode
        if (!branchCode.isNullOrBlank() && brhCode != branchCode) return false
        return true
    }

    override fun getConstructionFilesPage(query: ApiQueryParamDN): Flow<PageDN<ConstructionFileDN>> = flow {
        val response = remoteDataSource.getConstructionFiles(query)
        emit(
            PageDN(
                items = response.list.orEmpty().map { it.toDomain() },
                total = response.total,
            )
        )
    }

    override fun getBeneficiariesWorkshopPage(query: ApiQueryParamDN): Flow<PageDN<BeneficiaryConstructionDN>> = flow {
        val response = remoteDataSource.getBeneficiariesWorkshop(query)
        emit(
            PageDN(
                items = response.list.orEmpty().map { it.toDomain() },
                total = response.total,
            )
        )
    }

    override fun getPaymentSheetConstructionInfo(
        debitNumber: String
    ): Flow<List<PaymentSheetConstructionFileDN>> = flow {
        val result = remoteDataSource.getPaymentSheetConstructionInfo(debitNumber)
        emit(result.list.orEmpty().map { it.toDomain() })
    }

    override fun getCertificatePaymentSheetPdf(
        debitNumber: String,
        branchCode: String,
    ): Flow<PdfDownloadDN> = flow {
        emit(remoteDataSource.getCertificatePaymentSheetPdf(debitNumber, branchCode).toDomain())
    }

    override fun issuancePaymentSheet(debitNumber: String): Flow<String> = flow {
        emit(remoteDataSource.issuancePaymentSheet(debitNumber))
    }

    override fun getInstallmentLetterListPage(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): Flow<PageDN<InstallmentLetterDN>> = flow {
        val response = remoteDataSource.getInstallmentLetterList(workshopId, branchId, query)
        emit(
            PageDN(
                items = response.list.orEmpty().map { it.toDomain() },
                total = response.total,
            )
        )
    }

    private fun buildQuery(search: ConstructionFileSearchParamsDN?): ApiQueryParamDN {
        if (search == null) {
            return ApiQueryParamDN()
        }
        val filters = mutableListOf<ApiFilterDN>()
        val fileNo = search.fileNo
        if (!fileNo.isNullOrBlank()) {
            filters.add(
                ApiFilterDN(
                    property = FilterProperty.FILE_NO,
                    value = fileNo,
                    operator = FilterOperator.EQ
                )
            )
        }
        val reqNo = search.reqNo
        if (!reqNo.isNullOrBlank()) {
            filters.add(
                ApiFilterDN(
                    property = FilterProperty.REQ_NO,
                    value = reqNo,
                    operator = FilterOperator.EQ
                )
            )
        }
        val workshopId = search.workshopId
        if (!workshopId.isNullOrBlank()) {
            filters.add(
                ApiFilterDN(
                    property = FilterProperty.WORKSHOP_ID,
                    value = workshopId,
                    operator = FilterOperator.EQ
                )
            )
        }
        val branchCode = search.branchCode
        if (!branchCode.isNullOrBlank()) {
            filters.add(
                ApiFilterDN(
                    property = FilterProperty.WORKSHOP_BRANCH_CODE,
                    value = branchCode,
                    operator = FilterOperator.EQ
                )
            )
        }
        return ApiQueryParamDN(filters = filters)
    }
}
