package com.tamin.taminhamrah.feature.treatment.fake

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDN
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDetailDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPriceDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Configurable fake [TreatmentRepository] for the dashboard ViewModel tests.
 *
 * Defaults emit realistic [TreatmentTestData] so success paths work out of the box;
 * set [shouldThrowError] to drive failure paths. The error is thrown inside the emitted
 * flow so the ViewModel's `catch` handles it exactly as in production.
 */
class FakeTreatmentRepository : TreatmentRepository {

    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake Treatment Repository Error")

    var deservedResult: List<DeservedTreatmentDN> = listOf(TreatmentTestData.deserved())
    var dependantResult: List<DependantUserUnderEighteenDN> = listOf(TreatmentTestData.dependant())
    var prescriptionListResult: List<ElectronicPrescriptionDN> = emptyList()
    var prescriptionDetailResult: List<ElectronicPrescriptionDetailDN> = emptyList()
    var prescriptionPriceResult: List<ElectronicPrescriptionPriceDN> = emptyList()
    var prescriptionPdfResult: PdfDownloadDN = TreatmentTestData.pdf()
    var testResultPdfResult: PdfDownloadDN = TreatmentTestData.pdf()

    private fun <T> result(value: T): Flow<T> = flow {
        if (shouldThrowError) throw error
        emit(value)
    }

    override suspend fun getDeservedTreatment(nationalCode: String): Flow<List<DeservedTreatmentDN>> =
        result(deservedResult)

    override suspend fun getElectronicPrescriptionList(
        requestTypeId: String, nationalCode: String, dependantUserNationalCode: String,
        startDate: String, endDate: String, filters: List<ApiFilterDN>
    ): Flow<List<ElectronicPrescriptionDN>> = result(prescriptionListResult)

    override suspend fun getElectronicPrescriptionDetail(
        noteHeadID: String, nationalCode: String, childNationalCode: String,
        flagSata: String, type: String, filters: List<ApiFilterDN>
    ): Flow<List<ElectronicPrescriptionDetailDN>> = result(prescriptionDetailResult)

    override suspend fun getElectronicPrescriptionPrice(
        noteHeadID: String, nationalCode: String, filters: List<ApiFilterDN>
    ): Flow<List<ElectronicPrescriptionPriceDN>> = result(prescriptionPriceResult)

    override suspend fun getDependantUnderEighteen(
        nationalCode: String,
        filters: List<ApiFilterDN>
    ): Flow<List<DependantUserUnderEighteenDN>> = result(dependantResult)

    override suspend fun getPrescriptionPdfFile(prescriptionID: String): Flow<PdfDownloadDN> =
        result(prescriptionPdfResult)

    override suspend fun downloadTestResultPdf(
        patientID: String?, noteHeadEprescID: String?, currentUserNationalCode: String?
    ): Flow<PdfDownloadDN> = result(testResultPdfResult)
}
