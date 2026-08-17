package com.tamin.taminhamrah.feature.treatment.fake

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDN
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDN
import com.tamin.taminhamrah.model.treatment.TreatmentCostDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDetailDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPriceDN
import com.tamin.taminhamrah.model.treatment.MedicalConfirmationDN
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
    var labResultPdfResult: PdfDownloadDN = TreatmentTestData.pdf()
    var treatmentCostsResult: List<TreatmentCostDN> = emptyList()
    var treatmentCostsPdfResult: PdfDownloadDN = TreatmentTestData.pdf()
    var sendToInboxResult: String = "SUCCESS"
    var medicalConfirmationsResult: List<MedicalConfirmationDN> = emptyList()

    private fun <T> result(value: T): Flow<T> = flow {
        if (shouldThrowError) throw error
        emit(value)
    }

    override suspend fun getDeservedTreatment(nationalCode: String): Flow<List<DeservedTreatmentDN>> =
        result(deservedResult)

    override suspend fun getElectronicPrescriptionList(
        requestTypeId: String, nationalCode: String, patientNationalCode: String,
        startDate: String, endDate: String
    ): Flow<List<ElectronicPrescriptionDN>> = result(prescriptionListResult)

    override suspend fun getElectronicPrescriptionDetail(
        noteHeadID: String, nationalCode: String, patientNationalCode: String,
        flagSata: String, type: String
    ): Flow<List<ElectronicPrescriptionDetailDN>> = result(prescriptionDetailResult)

    override suspend fun getElectronicPrescriptionPrice(
        noteHeadID: String, nationalCode: String
    ): Flow<List<ElectronicPrescriptionPriceDN>> = result(prescriptionPriceResult)

    override suspend fun getDependantUnderEighteen(
        nationalCode: String
    ): Flow<List<DependantUserUnderEighteenDN>> = result(dependantResult)

    override suspend fun getPrescriptionPdfFile(prescriptionID: String): Flow<PdfDownloadDN> =
        result(prescriptionPdfResult)

    override suspend fun downloadLabResultPdf(
        patientID: String?, noteHeadEprescID: String?, currentUserNationalCode: String?
    ): Flow<PdfDownloadDN> = result(labResultPdfResult)

    override suspend fun getTreatmentCosts(): Flow<List<TreatmentCostDN>> =
        result(treatmentCostsResult)

    override suspend fun getTreatmentCostsPDF(repId: String): Flow<PdfDownloadDN> =
        result(treatmentCostsPdfResult)

    override suspend fun sendToInboxTreatmentCosts(repId: String): Flow<String> =
        result(sendToInboxResult)

    override suspend fun getMedicalConfirmations(): Flow<List<MedicalConfirmationDN>> =
        result(medicalConfirmationsResult)

    override suspend fun getMedicalConfirmationPdf(repId: String): Flow<PdfDownloadDN> =
        result(treatmentCostsPdfResult)

    override suspend fun sendToInboxMedicalConfirmation(repId: String): Flow<String> =
        result(sendToInboxResult)
}

