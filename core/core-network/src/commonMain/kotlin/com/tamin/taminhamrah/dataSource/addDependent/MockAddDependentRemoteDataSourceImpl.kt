package com.tamin.taminhamrah.dataSource.addDependent

import com.tamin.taminhamrah.model.addDependent.BranchDto
import com.tamin.taminhamrah.model.addDependent.DependentInfoDto
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDto
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipProxyDto
import com.tamin.taminhamrah.model.addDependent.GeneralResponseDto
import com.tamin.taminhamrah.model.addDependent.RegistryDataDto
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDto
import com.tamin.taminhamrah.model.addDependent.UploadImageResponseDto
import com.tamin.taminhamrah.model.request.ApiFilterDN
import kotlinx.coroutines.delay


class MockAddDependentRemoteDataSourceImpl : AddDependentRemoteDataSource {

    override suspend fun getDependentInfo(): List<DependentInfoDto> {
        delay(600)
        return listOf(
            DependentInfoDto(
                id = "1",
                fullName = "مریم حسینی",
                relationshipDesc = "همسر",
                nationalId = "0012345678",
                isInsuranceActive = true
            ),
            DependentInfoDto(
                id = "2",
                fullName = "امیرحسین رضایی",
                relationshipDesc = "فرزند پسر",
                nationalId = "0087654321",
                isInsuranceActive = true
            )
        )
    }

    override suspend fun getActiveBranches(): List<BranchDto> {
        delay(400)
        return listOf(
            BranchDto(branchCode = "1001", branchName = "شعبه ۱ تهران - شمیران"),
            BranchDto(branchCode = "1002", branchName = "شعبه ۲ تهران - آزادی"),
            BranchDto(branchCode = "1003", branchName = "شعبه ۳ تهران - انقلاب")
        )
    }

    override suspend fun getFamilyRelationships(filter: List<ApiFilterDN>): List<FamilyRelationshipDto> {
        delay(400)
        return listOf(
            FamilyRelationshipDto(id = 1, relationCode = "01", relationDesc = "همسر"),
            FamilyRelationshipDto(id = 2, relationCode = "02", relationDesc = "فرزند پسر"),
            FamilyRelationshipDto(id = 3, relationCode = "03", relationDesc = "فرزند دختر")
        )
    }

    override suspend fun getFamilyRelationshipsFromProxy(): List<FamilyRelationshipProxyDto> {
        return emptyList()
    }

    override suspend fun inquiryRegistry(
        dependentNationalId: String,
        birthDateTimeStamp: String,
        dependencyCode: String
    ): RegistryDataDto {
        delay(800)
        return when {
            dependencyCode == "01" || dependentNationalId.endsWith("01") -> {
                RegistryDataDto(
                    firstName = "مریم",
                    lastName = "حسینی",
                    fatherName = "محمد",
                    gender = "2",
                    birthDate = "1370/05/15",
                    nationalId = dependentNationalId.ifBlank { "0012345678" },
                    age = 34,
                    registryConfirmState = "CONFIRMED"
                )
            }
            dependencyCode == "02" || dependentNationalId.endsWith("02") -> {
                RegistryDataDto(
                    firstName = "امیرحسین",
                    lastName = "رضایی",
                    fatherName = "رضا",
                    gender = "1",
                    birthDate = "1383/02/10", // Age 21 for son education verification flow
                    nationalId = dependentNationalId.ifBlank { "0087654321" },
                    age = 21,
                    registryConfirmState = "CONFIRMED"
                )
            }
            dependencyCode == "03" || dependentNationalId.endsWith("03") -> {
                RegistryDataDto(
                    firstName = "فاطمه",
                    lastName = "رضایی",
                    fatherName = "رضا",
                    gender = "2",
                    birthDate = "1385/08/20", // Age 19 for daughter commitment flow
                    nationalId = dependentNationalId.ifBlank { "0099887766" },
                    age = 19,
                    registryConfirmState = "CONFIRMED"
                )
            }
            else -> {
                RegistryDataDto(
                    firstName = "زهرا",
                    lastName = "احمدی",
                    fatherName = "علی",
                    gender = "2",
                    birthDate = "1372/11/12",
                    nationalId = dependentNationalId.ifBlank { "0055443322" },
                    age = 32,
                    registryConfirmState = "CONFIRMED"
                )
            }
        }
    }

    override suspend fun inquiryEducationCode(
        nationalId: String,
        educationCode: String
    ): String {
        delay(700)
        return "دانشگاه تهران - دانشکده فنی و مهندسی (کد استعلام $educationCode تایید گردید)"
    }

    override suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ): UploadImageResponseDto {
        delay(900)
        val mockGuid = "MOCK-DOC-GUID-${(10000..99999).random()}"
        return UploadImageResponseDto(
            isSuccess = true,
            message = "فایل $fileName با موفقیت بارگذاری شد",
            guid = mockGuid
        )
    }

    override suspend fun addNewDependent(request: RequestAddDependentDto): GeneralResponseDto {
        delay(1000)
        return GeneralResponseDto(
            isSuccess = true,
            message = "درخواست افزودن فرد تبعی با موفقیت در سیستم سازمان تامین اجتماعی ثبت شد.",
            code = 200
        )
    }
}
