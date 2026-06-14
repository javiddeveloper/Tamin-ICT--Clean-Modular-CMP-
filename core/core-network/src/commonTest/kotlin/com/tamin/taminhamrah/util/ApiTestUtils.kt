package com.tamin.taminhamrah.util

object ApiTestUtils {
    fun createJsonResponse(
        dataJson: String,
        status: Int = 200,
        family: String = "SUCCESSFUL",
        reason: String = "OK"
    ): String = """
        {
            "status": $status,
            "family": "$family",
            "reason": "$reason",
            "traceId": "mock-trace-id",
            "data": $dataJson
        }
    """.trimIndent()
}

object UserTestData {
    val identityInfoSuccess = """
        {
            "nationalId": "0080000800",
            "firstName": "Javid",
            "lastName": "Sattar",
            "idCardNumber": "0080000800",
            "idCardSerial1": "A91",
            "idCardSerial2": "314981",
            "fatherName": "TESTFATHERNAME",
            "dateOfBirth": 730326600000,
            "countryId": "0001",
            "cityOfBirthId": "1105",
            "cityOfIssueId": "1105",
            "gender": "01",
            "ssn": "2194813688"
        }
    """.trimIndent()
}
