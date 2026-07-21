package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.weddingPresent

import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams

/**
 * TEMPORARY MOCK for testing the wedding-present chat flow without a backend.
 *
 * HOW TO REMOVE (fast & easy):
 *  1) Delete this file.
 *  2) Delete the lines tagged with "// MOCK" in:
 *        - WeddingPresentGetUseCase.kt
 *        - WeddingPresentValidateUseCase.kt
 *        - WeddingPresentCalculateUseCase.kt
 *        - WeddingPresentSubmitUseCase.kt
 *
 * To disable without deleting anything, just set ENABLED = false.
 */
object WeddingPresentMock {

    const val ENABLED = false

    const val SIMULATE_VALIDATE_ERROR = false
    const val SIMULATE_CALC_ERROR = false
    const val SIMULATE_SUBMIT_ERROR = false

    private const val TITLE = "درخواست هدیه ازدواج"
    private const val VALIDATE_MESSAGE = "اعتبارسنجی تاریخ عقد با موفقیت انجام شد (تست)"
    private const val SUCCESS_MESSAGE = "درخواست هدیه ازدواج شما با موفقیت ثبت شد (تست)"

    /** Step 1: getNoPresenceLoadData — fake user info. */
    fun mockGet(params: ServiceParams): ServiceResult {
        val payload = params.payload.mergePayload(fakeUserInfo())
        return ServiceResult.Success(
            listOf(
                ServiceResponse(
                    action = params.serviceName,
                    title = TITLE,
                    data = ServiceData.GenerativeForm(
                        schema = buildWeddingPresentSchema(step = 1, showCancelButton = true),
                        payload = payload.mapValues { it.value?.toString() }
                    )
                )
            )
        )
    }

    /** Step 1 → Step 2: validateMariageNoPresence. */
    fun mockValidate(params: ServiceParams): ServiceResult {
        val payload = params.payload ?: emptyMap()
        if (SIMULATE_VALIDATE_ERROR) {
            return ServiceResult.Success(
                listOf(
                    ServiceResponse(
                        action = params.serviceName,
                        title = TITLE,
                        data = ServiceData.GenerativeForm(
                            schema = buildWeddingPresentSchema(
                                step = 1,
                                showCancelButton = true,
                                errorMessage = "تاریخ عقد در سامانه ثبت احوال تایید نشد (تست)"
                            ),
                            payload = payload.mapValues { it.value?.toString() }
                        )
                    )
                )
            )
        }

        val weddingDateTimestamp = payload["weddingDateTimestamp"]?.toString().orEmpty()
        val resultPayload = payload.mergePayload(
            mapOf(
                "validateMessage" to VALIDATE_MESSAGE,
                "calcDateTimestamp" to weddingDateTimestamp,
                "calcDateJalali" to payload["weddingDateJalali"],
            )
        )
        return ServiceResult.Success(
            listOf(
                ServiceResponse(
                    action = params.serviceName,
                    title = TITLE,
                    data = ServiceData.GenerativeForm(
                        schema = buildWeddingPresentSchema(
                            step = 2,
                            showCancelButton = true,
                            message = VALIDATE_MESSAGE
                        ),
                        payload = resultPayload.mapValues { it.value?.toString() }
                    )
                )
            )
        )
    }

    /** Step 3: calcMarriage. */
    fun mockCalculate(params: ServiceParams): ServiceResult {
        val payload = params.payload ?: emptyMap()
        if (SIMULATE_CALC_ERROR) {
            return ServiceResult.Success(
                listOf(
                    ServiceResponse(
                        action = params.serviceName,
                        title = TITLE,
                        data = ServiceData.GenerativeForm(
                            schema = buildWeddingPresentSchema(
                                step = 3,
                                showCancelButton = true,
                                errorMessage = "امکان محاسبه مبلغ وجود ندارد (تست)"
                            ),
                            payload = payload.mapValues { it.value?.toString() }
                        )
                    )
                )
            )
        }

        val resultPayload = payload.mergePayload(
            mapOf(
                "amountPayable" to "120,000,000",
                "totalSalary" to "240,000,000",
            )
        )
        return ServiceResult.Success(
            listOf(
                ServiceResponse(
                    action = params.serviceName,
                    title = TITLE,
                    data = ServiceData.GenerativeForm(
                        schema = buildWeddingPresentSchema(step = 3, showCancelButton = true),
                        payload = resultPayload.mapValues { it.value?.toString() }
                    )
                )
            )
        )
    }

    /** Step 4: saveShorttremMariage — final success. */
    fun mockSubmit(params: ServiceParams): ServiceResult {
        val payload = params.payload ?: emptyMap()
        if (SIMULATE_SUBMIT_ERROR) {
            return ServiceResult.Success(
                listOf(
                    ServiceResponse(
                        action = params.serviceName,
                        title = TITLE,
                        data = ServiceData.GenerativeForm(
                            schema = buildWeddingPresentSchema(
                                step = 4,
                                showCancelButton = false,
                                errorMessage = "ثبت نهایی درخواست با خطا مواجه شد (تست)"
                            ),
                            payload = payload.mapValues { it.value?.toString() }
                        )
                    )
                )
            )
        }

        return ServiceResult.Success(
            listOf(
                ServiceResponse(
                    action = params.serviceName,
                    title = TITLE,
                    data = ServiceData.GenerativeForm(
                        schema = buildWeddingPresentSchema(
                            step = 4,
                            showCancelButton = false,
                            message = SUCCESS_MESSAGE
                        ),
                        payload = payload.mapValues { it.value?.toString() }
                    )
                )
            )
        )
    }

    private fun fakeUserInfo(): Map<String, Any?> = mapOf(
        "risuid" to "1234567890",
        "insuranceFirstName" to "علی",
        "insuranceLastName" to "محمدی",
        "nationalCode" to "0011223344",
        "insuranceTypeDesc" to "اجباری",
        "insuranceStatusDesc" to "فعال",
        "bankAccount" to "IR123456789012345678901234",
        "bankName" to "بانک رفاه",
        "branchName" to "شعبه مرکزی تهران",
        "mobilNumber" to "09120000000",
        "requestHelpType" to "1",
        "serviceDateTimeStamp" to "0",
        "branchCode" to "1000",
    )
}
