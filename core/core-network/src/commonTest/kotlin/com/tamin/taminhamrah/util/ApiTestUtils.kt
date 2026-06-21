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

    val changeMobileSuccess = """
        {
            "traceId": "test-trace-id-123",
            "data": {
                "hash": "test-hash-456"
            }
        }
    """.trimIndent()

    val verifyMobileSuccess = "\"OTP_VERIFIED_SUCCESSFULLY\""
}

object UserRequestTestData {
    val userRequestsSuccess = """
        {
            "total": 1,
            "list": [
                {
                    "id": 478176975,
                    "operation": null,
                    "createdBy": "0946168113",
                    "creationTime": 1780398668987,
                    "lastModifiedBy": null,
                    "lastModificationTime": 1780398668983,
                    "refCode": "1073555545",
                    "userName": "0946168113",
                    "status": {
                        "operation": null,
                        "requestCode": "2903",
                        "requestDesc": "انعقاد قرارداد"
                    },
                    "title": "انعقاد قرارداد بيمه اختياري",
                    "comment": "قرارداد ايجاد شد",
                    "template": null,
                    "requestType": {
                        "operation": null,
                        "createdBy": null,
                        "creationTime": null,
                        "lastModifiedBy": null,
                        "lastModificationTime": null,
                        "id": 35,
                        "title": "قرارداد بيمه اختياري",
                        "description": "قرارداد بيمه اختياري"
                    },
                    "deliverCode": null,
                    "refrenceid": "478176974",
                    "requestDetails": null,
                    "requestChid": null,
                    "fullName": null,
                    "createByName": "حميد چيداز"
                }
            ]
        }
    """.trimIndent()

    val requestTypesSuccess = """
        {
            "total": 64,
            "list": [
                {
                    "operation": null,
                    "createdBy": "PNJ_14040622",
                    "creationTime": 1760260280000,
                    "lastModifiedBy": null,
                    "lastModificationTime": null,
                    "id": 67,
                    "title": "END_KAFALAT",
                    "description": "خاتمه کفالت"
                }
            ]
        }
    """.trimIndent()
}

object PersonalInboxTestData {
    val inboxItemsSuccess = """
        {
            "total": "1",
            "list": [
                {
                    "id": 12345,
                    "nationalCode": "0946168113",
                    "mobileNumber": "09123456789",
                    "email": null,
                    "read": "0",
                    "data": null,
                    "sentDate": 1780398668987,
                    "receiveDate": 1780398668987,
                    "seenDate": null,
                    "seen": false,
                    "hasImage": false,
                    "hasText": true,
                    "hasPDF": false,
                    "updateable": true,
                    "status": "1",
                    "refrenceId": null,
                    "pdf": null,
                    "type": {
                        "typeDesc": "سیستم تامین",
                        "typeCode": "01"
                    },
                    "subType": {
                        "typeDesc": "اعلامیه",
                        "typeCode": "02"
                    },
                    "permission": {
                        "password": 1234,
                        "dateFrom": null,
                        "dateTo": 1780398668987,
                        "id": 1
                    }
                }
            ]
        }
    """.trimIndent()

    val inboxSizeSuccess = """
        {
            "usage": "0.53",
            "total": "10"
        }
    """.trimIndent()
}

object ContractsTestData {
    val contractsListSuccess = """
        {
            "total": 1,
            "list": [
                {
                    "adultLetterDate": null,
                    "adultLetterNumber": null,
                    "age": null,
                    "branchCode": null,
                    "brchCodeNew": null,
                    "cancelDate": null,
                    "cancelUID": null,
                    "canceldesc": null,
                    "cityCode": null,
                    "cntDrmn": "1",
                    "cntFreeJobCode": null,
                    "cntIncPayDate3t4": null,
                    "cntMedicalFlag": null,
                    "comment": null,
                    "commissionStatus": null,
                    "confirmDate": null,
                    "confirmUID": null,
                    "contractDate": 1780398668987,
                    "contractNumber": 478176974,
                    "contractStatus": null,
                    "contractStatusObject": {
                        "selfIsuContStatDesc": "فعال بعلت تنظیم قرارداد",
                        "selfIsuContStatDode": 1
                    },
                    "creatDate": null,
                    "createDate": null,
                    "createUID": null,
                    "eligibilityStatus": null,
                    "freeJob": {
                        "discrioption": "تاسیساتی",
                        "endDate": null,
                        "fixRank": null,
                        "id": null,
                        "iscoCode": null,
                        "jobCode": null,
                        "startDate": null,
                        "status": null
                    },
                    "guid": null,
                    "guidName": null,
                    "history": null,
                    "insuranceId": null,
                    "isStudent": null,
                    "medicalExemptionStatus": null,
                    "militaryServiceLicense": null,
                    "mobileNumber": null,
                    "natinoalCode": null,
                    "physicalStatus": null,
                    "premiumRate": {
                        "govermentPercent": null,
                        "insurDpercent": "27",
                        "payrespitelOne": null,
                        "payrespitelTwo": null,
                        "selfIsuTypeCode": null,
                        "spcLowDayWage": null,
                        "spcrateCode": null,
                        "spcrateDescription": "بیمه اختیاری ۲۷ درصد",
                        "status": null,
                        "statusStDate": null,
                        "treatmentPercap": null
                    },
                    "premiumRateCode": null,
                    "premiumType": {
                        "insuranceDescription": "اختیاری",
                        "insuranceKind": "اختیاری",
                        "insuranceTypeCode": "02",
                        "status": "1",
                        "statusDate": 1780398668987
                    },
                    "premiumTypeCode": null,
                    "provinceCode": null,
                    "provinceName": null,
                    "refCode": null,
                    "salary": 362592593,
                    "startDate": null,
                    "statusDate": null,
                    "wage": null
                }
            ]
        }
    """.trimIndent()
}
