package com.tamin.taminhamrah.dataSource.personal

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.apiService.personal.PersonalApiService
import com.tamin.taminhamrah.apiService.personal.createPersonalApiService
import com.tamin.taminhamrah.model.personal.InsuredDocDTO
import com.tamin.taminhamrah.model.personal.NewInsuredSummaryDTO
import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.DisabilityDependentDTO
import com.tamin.taminhamrah.model.personal.girlSurvivor.ConfirmGirlSurvivorRequestDTO
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.DependencyTypeRequest
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoRequest
import com.tamin.taminhamrah.model.personal.submitFinalSurvivorPension.SubmitFinalSurvivorPensionRequest
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentDTO
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.FakeIOException
import com.tamin.taminhamrah.tools.ProblemDTO
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilderImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.util.PersonalTestData
import io.ktor.client.statement.HttpStatement
import io.ktor.http.ContentType
import io.ktor.utils.io.readRemaining
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlinx.coroutines.test.runTest
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

private val testJson = Json { ignoreUnknownKeys = true }

class PersonalRemoteDataSourceImplTest : BaseApiTest() {

    private lateinit var fakeApiService: FakePersonalApiService
    private lateinit var dataSource: PersonalRemoteDataSourceImpl

    @BeforeTest
    fun setup() {
        fakeApiService = FakePersonalApiService()
        dataSource = PersonalRemoteDataSourceImpl(
            personalApiService = fakeApiService,
            queryBuilder = ApiQueryBuilderImpl(),
            errorParser = ErrorParserImpl(),
        )
    }

    @Test
    fun getPersonalInfo_success_returnsGirlSurvivorPersonal() = runTest {
        val personal = testJson.decodeFromString(
            PersonalInfoDTO.serializer(),
            PersonalTestData.girlSurvivorPersonalSuccess,
        )
        fakeApiService.personalInfoResult = BaseDTO(
            status = 200,
            family = "SUCCESSFUL",
            reason = "OK",
            data = personal,
        )

        val result = dataSource.getPersonalInfo()

        assertNotNull(result)
        assertEquals("زهرا", result.personal?.firstName)
        assertEquals("0012345678", result.personal?.nationalId)
        assertEquals("02", result.personal?.gender?.genderCode)
        assertEquals("09121234567", result.mobileNumber)
        assertEquals("تهران، خیابان آزادی، پلاک ۱۲", result.personal?.contacts?.firstOrNull()?.address)
    }

    @Test
    fun getPersonalInfo_onNetworkError_throwsParsedTaminApiException() = runTest {
        fakeApiService.shouldThrowException = FakeIOException()

        val exception = assertFailsWith<TaminApiException> {
            dataSource.getPersonalInfo()
        }

        assertEquals("خطای اتصال", exception.title)
    }

    @Test
    fun checkGirlSurvivorConditions_barePersian500Body_throwsParsedBusinessMessage() = runTest {
        val message = "اطلاعاتی از حکم مستمری یا فوت فرد مورد نظر شما یافت نشد."
        val ktorfit = createMockKtorfit(
            content = message,
            status = io.ktor.http.HttpStatusCode.InternalServerError,
        )
        val liveDataSource = PersonalRemoteDataSourceImpl(
            personalApiService = ktorfit.createPersonalApiService(),
            queryBuilder = ApiQueryBuilderImpl(),
            errorParser = ErrorParserImpl(),
        )

        val exception = assertFailsWith<TaminApiException> {
            liveDataSource.checkGirlSurvivorConditions("6360110032", "")
        }

        assertEquals("خطا", exception.title)
        assertEquals(message, exception.subtitle)
    }

    @Test
    fun checkGirlSurvivorConditions_success_returnsReasonMessage() = runTest {
        fakeApiService.conditionResult = BaseDTO(
            status = 200,
            family = "SUCCESSFUL",
            reason = "OK",
            data = null,
        )

        val result = dataSource.checkGirlSurvivorConditions("0012345678", "1234567890")

        assertEquals("OK", result)
    }

    @Test
    fun checkGirlSurvivorConditions_ineligible_throwsBusinessError() = runTest {
        fakeApiService.conditionResult = BaseDTO(
            status = 400,
            family = "CLIENT_ERROR",
            reason = "شرایط احراز نشده است",
            data = null,
            hasError = true,
            problems = listOf(
                ProblemDTO(
                    errorCode = 9001,
                    errorMsg = "فرد مشمول تعهدنامه فرزندان دختر نیست",
                )
            ),
        )

        val exception = assertFailsWith<TaminApiException> {
            dataSource.checkGirlSurvivorConditions("0012345678", "1234567890")
        }

        assertEquals("خطا", exception.title)
        assertEquals(exception.subtitle?.contains("مشمول"), true)
    }

    @Test
    fun checkGirlSurvivorConditions_onNetworkError_throwsParsedTaminApiException() = runTest {
        fakeApiService.shouldThrowException = FakeIOException()

        val exception = assertFailsWith<TaminApiException> {
            dataSource.checkGirlSurvivorConditions("001", "002")
        }

        assertEquals("خطای اتصال", exception.title)
    }

    @Test
    fun confirmGirlSurvivor_success_returnsMessageFromData() = runTest {
        fakeApiService.confirmResult = BaseDTO(
            status = 200,
            family = "SUCCESSFUL",
            reason = "OK",
            data = JsonPrimitive("درخواست با موفقیت ثبت شد"),
        )

        val result = dataSource.confirmGirlSurvivor(sampleConfirmRequest())

        assertEquals("درخواست با موفقیت ثبت شد", result)
    }

    @Test
    fun confirmGirlSurvivor_nullData_returnsReason() = runTest {
        fakeApiService.confirmResult = BaseDTO(
            status = 200,
            family = "SUCCESSFUL",
            reason = "OK",
            data = null,
        )

        val result = dataSource.confirmGirlSurvivor(sampleConfirmRequest(pensionId = "1234567890"))

        assertEquals("OK", result)
    }

    @Test
    fun confirmGirlSurvivor_onNetworkError_throwsParsedTaminApiException() = runTest {
        fakeApiService.shouldThrowException = FakeIOException()

        val exception = assertFailsWith<TaminApiException> {
            dataSource.confirmGirlSurvivor(sampleConfirmRequest())
        }

        assertEquals("خطای اتصال", exception.title)
    }

    @Test
    fun getGirlSurvivorReport_success_readsPdfBytes() = runTest {
        val ktorfit = createMockKtorfit(
            content = PersonalTestData.girlSurvivorReportPdfBytes,
            contentType = ContentType.Application.Pdf,
        )
        val liveDataSource = PersonalRemoteDataSourceImpl(
            personalApiService = ktorfit.createPersonalApiService(),
            queryBuilder = ApiQueryBuilderImpl(),
            errorParser = ErrorParserImpl(),
        )

        val result = liveDataSource.getGirlSurvivorReport(
            address = "تهران",
            tel = "02166778899",
            postalCode = "1234567890",
            fatherName = "علی",
            birthDate = 631152000000,
            insuranceId = "0071234567",
            parentCode = "0012345678",
            pensionerId = "",
        )

        val channel = assertNotNull(result.pdf?.pdf)
        val bytes = channel.readRemaining().readByteArray()
        assertEquals(PersonalTestData.girlSurvivorReportPdfBytes.toList(), bytes.toList())
    }

    @Test
    fun getGirlSurvivorReport_onNetworkError_throwsParsedTaminApiException() = runTest {
        fakeApiService.shouldThrowException = FakeIOException()

        val exception = assertFailsWith<TaminApiException> {
            dataSource.getGirlSurvivorReport(
                address = "a",
                tel = "0",
                postalCode = "1",
                fatherName = null,
                birthDate = null,
                insuranceId = null,
                parentCode = "001",
                pensionerId = "002",
            )
        }

        assertEquals("خطای اتصال", exception.title)
    }

    private fun sampleConfirmRequest(
        nationalCode: String? = "9988776655",
        pensionId: String? = null,
    ) = ConfirmGirlSurvivorRequestDTO(
        address = "تهران، خیابان آزادی، پلاک ۱۲",
        age = "33",
        birthDate = 631152000000,
        childInsuranceId = "0071234567",
        childNationalId = "0012345678",
        dependencyType = DependencyTypeRequest(code = "04"),
        firstName = "زهرا",
        gender = "02",
        idNumber = "456789",
        insuranceNumber = "0071234567",
        lastName = "محمدی",
        mobileNumber = "09121234567",
        nationalCode = nationalCode,
        pensionId = pensionId,
        phoneNumber = "02166778899",
        status = "0",
    )
}

private class FakePersonalApiService : PersonalApiService {
    var personalInfoResult: BaseDTO<PersonalInfoDTO> =
        BaseDTO(status = 200, family = "SUCCESSFUL", reason = "OK", data = null)
    var conditionResult: BaseDTO<JsonElement?> =
        BaseDTO(status = 200, family = "SUCCESSFUL", reason = "OK", data = null)
    var confirmResult: BaseDTO<JsonElement?> =
        BaseDTO(status = 200, family = "SUCCESSFUL", reason = "OK", data = null)
    var shouldThrowException: Exception? = null

    override suspend fun getPersonalInfo(): BaseDTO<PersonalInfoDTO> {
        shouldThrowException?.let { throw it }
        return personalInfoResult
    }

    override suspend fun getDeceasedInfo(nationalId: String): BaseDTO<DeceasedInfoDTO> {
        shouldThrowException?.let { throw it }
        error("Not stubbed")
    }

    override suspend fun getAge(birthDate: Long): BaseDTO<AgeDTO> {
        shouldThrowException?.let { throw it }
        error("Not stubbed")
    }

    override suspend fun getDisabilityDependentInfo(
        parameters: Map<String, String>
    ): BaseDTO<ListData<DisabilityDependentDTO>> {
        shouldThrowException?.let { throw it }
        error("Not stubbed")
    }

    override suspend fun getSurvivorList(id: String): BaseDTO<ListData<SurvivorDependentDTO>> {
        shouldThrowException?.let { throw it }
        error("Not stubbed")
    }

    override suspend fun checkGirlSurvivorConditions(
        nationalCode: String,
        relation: String,
        pensionerId: String,
    ): BaseDTO<JsonElement?> {
        shouldThrowException?.let { throw it }
        return conditionResult
    }

    override suspend fun confirmSurvivorsList(
        parameters: Map<String, String>
    ): BaseDTO<ListData<ConfirmSurvivorDTO>> {
        shouldThrowException?.let { throw it }
        error("Not stubbed")
    }

    override suspend fun submitFinalSurvivorPension(
        requestId: Int,
        body: SubmitFinalSurvivorPensionRequest
    ): BaseDTO<JsonElement?> {
        shouldThrowException?.let { throw it }
        error("Not stubbed")
    }

    override suspend fun saveSurvivorInfo(body: SaveSurvivorInfoRequest): BaseDTO<JsonElement?> {
        shouldThrowException?.let { throw it }
        error("Not stubbed")
    }

    override suspend fun getFinalSurvivorPensionPDF(): HttpStatement {
        shouldThrowException?.let { throw it }
        error("Not stubbed")
    }

    override suspend fun getGirlSurvivorReport(
        address: String,
        tel: String,
        postalCode: String,
        fatherName: String?,
        birthDate: Long?,
        insuranceId: String?,
        parentCode: String,
        pensionerId: String,
    ): HttpStatement {
        shouldThrowException?.let { throw it }
        error("Not stubbed")
    }

    override suspend fun confirmGirlSurvivor(
        body: ConfirmGirlSurvivorRequestDTO
    ): BaseDTO<JsonElement?> {
        shouldThrowException?.let { throw it }
        return confirmResult
    }

    override suspend fun putInsuredRegistrationDocList(
        personalId: String,
        body: List<InsuredDocDTO>
    ): BaseDTO<String?> {
        shouldThrowException?.let { throw it }
        error("Not stubbed")
    }

    override suspend fun getRequestSummary(requestId: String): BaseDTO<NewInsuredSummaryDTO> {
        shouldThrowException?.let { throw it }
        error("Not stubbed")
    }

    override suspend fun getInsuredRegistrationDocList(
        parameters: Map<String, String>
    ): BaseDTO<ListData<InsuredDocDTO>> {
        shouldThrowException?.let { throw it }
        error("Not stubbed")
    }
}
