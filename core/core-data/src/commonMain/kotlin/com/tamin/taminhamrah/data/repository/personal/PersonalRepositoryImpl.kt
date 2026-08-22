package com.tamin.taminhamrah.data.repository.personal

import com.tamin.taminhamrah.data.local.dao.PersonalDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toDTO
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.dataSource.personal.PersonalRemoteDataSource
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.DocumentFileDTO
import com.tamin.taminhamrah.model.personal.GirlSurvivorConditionDN
import com.tamin.taminhamrah.model.personal.InsuredDocDN
import com.tamin.taminhamrah.model.personal.InsuredDocDTO
import com.tamin.taminhamrah.model.personal.NewInsuredSummaryDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDN
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDN
import com.tamin.taminhamrah.model.personal.girlSurvivor.ConfirmGirlSurvivorDN
import com.tamin.taminhamrah.model.personal.girlSurvivor.GirlSurvivorReportParamsDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class PersonalRepositoryImpl(
    private val personalRemoteDataSource: PersonalRemoteDataSource,
    private val personalDao: PersonalDao
) : PersonalRepository {
    override fun getPersonalInfo(refreshRemote: Boolean): Flow<PersonalInfoDN?> = flow {
        val localInfo = personalDao.getPersonalInfo().first()
        if (!refreshRemote) {
            emit(localInfo?.toDomain())
        }

        try {
            val response = personalRemoteDataSource.getPersonalInfo()
            val remoteInfo = response?.toDomain()

            if (remoteInfo != null) {
                personalDao.upsertPersonalInfo(remoteInfo.toEntity())
            }
        } catch (e: Exception) {
            if (localInfo == null) {
                throw e
            }
        }

        emitAll(personalDao.getPersonalInfo().map { it?.toDomain() })
    }.distinctUntilChanged()

    override fun getDeceasedInfo(nationalId: String): Flow<DeceasedInfoDN> = flow {
        emit(personalRemoteDataSource.getDeceasedInfo(nationalId).toDomain())
    }

    override fun getAge(birthDate: Long): Flow<AgeDN> = flow {
        emit(personalRemoteDataSource.getAge(birthDate).toDomain())
    }


    override fun getDisabilityDependentInfo(filters: List<ApiFilterDN>): Flow<List<DisabilityDependentDN>> =
        flow {

            val response = personalRemoteDataSource.getDisabilityDependentInfo(
                ApiQueryParamDN(filters = filters)
            )
            emit(response.map { it.toDomain() })

        }

    override fun checkGirlSurvivorConditions(
        nationalCode: String,
        pensionerId: String
    ): Flow<GirlSurvivorConditionDN> = flow {
        emit(
            personalRemoteDataSource.checkGirlSurvivorConditions(
                nationalCode,
                pensionerId
            ).toDomain()
        )
    }

    override fun submitFinalSurvivorPension(
        requestId: Int,
        body: SubmitFinalSurvivorPensionDN
    ): Flow<String?> = flow {
        emit(personalRemoteDataSource.submitFinalSurvivorPension(requestId, body.toDTO()))
    }

    override fun getConfirmSurvivorsList(filters: List<ApiFilterDN>): Flow<List<ConfirmSurvivorDN>> =
        flow {

            val response =
                personalRemoteDataSource.confirmSurvivorsList(ApiQueryParamDN(filters = filters))
            emit(response.map { it.toDomain() })

    }

    override fun getFinalSurvivorPensionPDF(): Flow<PdfDownloadDN> = flow {
        emit(personalRemoteDataSource.getFinalSurvivorPensionPDF().toDomain())
    }

    override fun getGirlSurvivorReport(params: GirlSurvivorReportParamsDN): Flow<PdfDownloadDN> = flow {
        emit(
            personalRemoteDataSource.getGirlSurvivorReport(
                address = params.address,
                tel = params.tel,
                postalCode = params.postalCode,
                fatherName = params.fatherName,
                birthDate = params.birthDate,
                insuranceId = params.insuranceId,
                parentCode = params.parentCode,
                pensionerId = params.pensionerId,
            ).toDomain()
        )
    }

    override fun confirmGirlSurvivor(body: ConfirmGirlSurvivorDN): Flow<String?> = flow {
        emit(personalRemoteDataSource.confirmGirlSurvivor(body.toDTO()))
    }

    override fun saveSurvivorInfo(body: SaveSurvivorInfoDN): Flow<String?> = flow {
        emit(personalRemoteDataSource.saveSurvivorInfo(body.toDTO()))
    }

    override fun putInsuredRegistrationDocList(
        personalId: String,
        docs: List<InsuredDocDN>
    ): Flow<String?> = flow {
        val dtos = docs.map { it.toDTO() }
        emit(personalRemoteDataSource.putInsuredRegistrationDocList(personalId, dtos))
    }

    override fun getRequestSummary(requestId: String): Flow<NewInsuredSummaryDN?> = flow {
        val response = personalRemoteDataSource.getRequestSummary(requestId)
        emit(response?.toDomain())
    }
}
