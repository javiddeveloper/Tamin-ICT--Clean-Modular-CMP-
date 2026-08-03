package com.tamin.taminhamrah.data.mapper.health

import com.tamin.taminhamrah.model.health.*
import com.tamin.taminhamrah.tools.ProblemDTO

fun PatientGeneralDTO.toDomain() = PatientGeneralDN(
    ptientID = ptientID,
    patientName = patientName,
    patientFamily = patientFamily,
    patientNatCode = patientNatCode,
    patientAge = patientAge,
    patientGender = patientGender,
    patientBirthDate = patientBirthDate,
    patientMobile = patientMobile,
    patientAddress = patientAddress,
    patientFather = patientFather,
    emergencyAddress = emergencyAddress,
    emergencyArea = emergencyArea,
    emergencyCity = emergencyCity,
    emergencyCityCode = emergencyCityCode,
    emergencyEmail = emergencyEmail,
    emergencyFamily = emergencyFamily,
    emergencyMobile = emergencyMobile,
    emergencyName = emergencyName,
    emergencyProvince = emergencyProvince,
    emergencyProvinceCode = emergencyProvinceCode,
    emergencyRelation = emergencyRelation,
    emergencyRelationshipCode = emergencyRelationshipCode,
    lastUpdateDate = lastUpdateDate,
    lastVisitDate = lastVisitDate,
    patientArea = patientArea,
    patientBMI = patientBMI,
    patientBloodGroup = patientBloodGroup,
    patientBloodGroupCode = patientBloodGroupCode,
    patientCitizenship = patientCitizenship,
    patientCity = patientCity,
    patientCityCode = patientCityCode,
    patientEmail = patientEmail,
    patientGenderCode = patientGenderCode,
    patientHeight = patientHeight,
    patientInsurance = patientInsurance,
    patientInsuranceCode = patientInsuranceCode,
    patientJob = patientJob,
    patientMarriage = patientMarriage,
    patientMarriageCode = patientMarriageCode,
    patientNationality = patientNationality,
    patientProvince = patientProvince,
    patientProvinceCode = patientProvinceCode,
    patientWeight = patientWeight
)

fun PatientSelfDeclarativeDTO.toDomain() = PatientSelfDeclarativeDN(
    alcoholDesc = alcoholDesc,
    alcoholUsage = alcoholUsage,
    alcoholUsageTitle = alcoholUsageTitle,
    exerciseDesc = exerciseDesc,
    exerciseFreq = exerciseFreq,
    exerciseFreqTitle = exerciseFreqTitle,
    lastUpdateDate = lastUpdateDate,
    objectID = objectID,
    smokingDesc = smokingDesc,
    smokingStatus = smokingStatus,
    smokingStatusTitle = smokingStatusTitle,
    substanceDesc = substanceDesc,
    substanceUsage = substanceUsage,
    substanceUsageTitle = substanceUsageTitle
)

fun DrugItemAllergiesDTO.toDomain() = DrugItemAllergiesDN(
    allergyComments = allergyComments,
    drugId = drugId,
    drugName = drugName
)

fun PatientHospitalizationsDTO.toDomain() = PatientHospitalizationsDN(
    admId = admId,
    admSource = admSource,
    admType = admType,
    comments = comments,
    docID = docID,
    docSpeciality = docSpeciality,
    doctorName = doctorName,
    finalDiagCode = finalDiagCode,
    finalDiagDesc = finalDiagDesc,
    firstDiagCode = firstDiagCode,
    firstDiagDesc = firstDiagDesc,
    healthcareProvider = healthcareProvider,
    hospitalizedDays = hospitalizedDays,
    hospitalizedEndDate = hospitalizedEndDate,
    hospitalizedStartDate = hospitalizedStartDate,
    outcomeDesc = outcomeDesc,
    referDocId = referDocId,
    referDocName = referDocName,
    referDocSpeciality = referDocSpeciality,
    referHealthcareProvider = referHealthcareProvider
)

fun PatientVisitDTO.toDomain() = PatientVisitDN(
    comments = comments,
    diagCode = diagCode,
    diagDesc = diagDesc,
    docID = docID,
    docSpeciality = docSpeciality,
    docSpecialityCode = docSpecialityCode,
    doctorName = doctorName,
    healthcareProvider = healthcareProvider,
    serviceName = serviceName,
    serviceProvideType = serviceProvideType,
    serviceResult = serviceResult,
    sourceSystem = sourceSystem,
    visitDate = visitDate,
    visitType = visitType
)

fun PatientLabDTO.toDomain() = PatientLabDN(
    deliveredQty = deliveredQty,
    diagCode = diagCode,
    diagDesc = diagDesc,
    docSpeciality = docSpeciality,
    docSpecialityCode = docSpecialityCode,
    doctorName = doctorName,
    examName = examName,
    healthcareProvider = healthcareProvider,
    itemComments = itemComments,
    objectId = objectId,
    prescribedQty = prescribedQty,
    resultDesc = resultDesc,
    resultValue = resultValue,
    serviceProvideType = serviceProvideType,
    sourceSystem = sourceSystem,
    visitDate = visitDate,
    visitType = visitType
)

fun PatientImagingDTO.toDomain() = PatientImagingDN(
    deliverStatus = deliverStatus,
    diagCode = diagCode,
    diagDesc = diagDesc,
    docSpeciality = docSpeciality,
    doctorName = doctorName,
    healthcareProvider = healthcareProvider,
    imagingName = imagingName,
    itemComments = itemComments,
    modality = modality,
    objectId = objectId,
    resultDesc = resultDesc,
    visitDate = visitDate,
    visitType = visitType
)

// --- Location ---

fun ProvinceItemDTO.toDomain() = ProvinceItemDN(id = id, name = name)

fun ProvinceCitiesItemDTO.toDomain() = ProvinceCityItemDN(id = id, name = name)

// --- Lookup ---

fun BloodGroupDTO.toDomain() = BloodGroupDN(key = key, value = value)

fun MaritalStatusDTO.toDomain() = MaritalStatusDN(key = key, value = value)

fun SmokingStatusDTO.toDomain() = SmokingStatusDN(key = key, value = value)

fun ActFrequencyDTO.toDomain() = ActFrequencyDN(key = key, value = value)

// --- Illnesses ---

fun IllnessItemDTO.toDomain() = IllnessItemDN(illnessID = illnessID, illnessDesc = illnessDesc)

fun SelfDeclarableIllnessGroupDTO.toDomain() = SelfDeclarableIllnessGroupDN(
    groupId = groupId,
    groupTitle = groupTitle,
    forFamily = forFamily,
    illnessList = illnessList?.map { it.toDomain() }
)

// --- Drug ---

fun DrugItemDTO.toDomain() = DrugItemDN(drugID = drugID, drugName = drugName)

// --- Mutation responses ---

fun UpdatePatientDTO.toDomain() = UpdatePatientDN(
    ptientID = ptientID,
    patientName = patientName,
    patientFamily = patientFamily,
    patientNatCode = patientNatCode,
    patientAge = patientAge,
    patientGender = patientGender,
    patientBirthDate = patientBirthDate,
    patientMobile = patientMobile,
    patientAddress = patientAddress,
    patientFather = patientFather,
    emergencyAddress = emergencyAddress,
    emergencyArea = emergencyArea,
    emergencyCity = emergencyCity,
    emergencyCityCode = emergencyCityCode,
    emergencyEmail = emergencyEmail,
    emergencyFamily = emergencyFamily,
    emergencyMobile = emergencyMobile,
    emergencyName = emergencyName,
    emergencyProvince = emergencyProvince,
    emergencyProvinceCode = emergencyProvinceCode,
    emergencyRelation = emergencyRelation,
    emergencyRelationshipCode = emergencyRelationshipCode,
    lastUpdateDate = lastUpdateDate,
    lastVisitDate = lastVisitDate,
    patientArea = patientArea,
    patientBMI = patientBMI,
    patientBloodGroup = patientBloodGroup,
    patientBloodGroupCode = patientBloodGroupCode,
    patientCitizenship = patientCitizenship,
    patientCity = patientCity,
    patientCityCode = patientCityCode,
    patientEmail = patientEmail,
    patientGenderCode = patientGenderCode,
    patientHeight = patientHeight,
    patientInsurance = patientInsurance,
    patientInsuranceCode = patientInsuranceCode,
    patientJob = patientJob,
    patientMarriage = patientMarriage,
    patientMarriageCode = patientMarriageCode,
    patientNationality = patientNationality,
    patientProvince = patientProvince,
    patientProvinceCode = patientProvinceCode,
    patientWeight = patientWeight
)

fun AddSelfDeclarativeDTO.toDomain() = AddSelfDeclarativeDN(
    objectID = objectID,
    smokingStatus = smokingStatus,
    smokingStatusTitle = smokingStatusTitle,
    smokingDesc = smokingDesc,
    alcoholUsage = alcoholUsage,
    alcoholUsageTitle = alcoholUsageTitle,
    alcoholDesc = alcoholDesc,
    substanceUsage = substanceUsage,
    substanceUsageTitle = substanceUsageTitle,
    substanceDesc = substanceDesc,
    exerciseFreq = exerciseFreq,
    exerciseFreqTitle = exerciseFreqTitle,
    exerciseDesc = exerciseDesc,
    lastUpdateDate = lastUpdateDate
)

fun UpdateSelfDeclarativeDTO.toDomain() = UpdateSelfDeclarativeDN(
    objectID = objectID,
    smokingStatus = smokingStatus,
    smokingStatusTitle = smokingStatusTitle,
    smokingDesc = smokingDesc,
    alcoholUsage = alcoholUsage,
    alcoholUsageTitle = alcoholUsageTitle,
    alcoholDesc = alcoholDesc,
    substanceUsage = substanceUsage,
    substanceUsageTitle = substanceUsageTitle,
    substanceDesc = substanceDesc,
    exerciseFreq = exerciseFreq,
    exerciseFreqTitle = exerciseFreqTitle,
    exerciseDesc = exerciseDesc,
    lastUpdateDate = lastUpdateDate
)

// --- Business problems (BaseDTO.problems envelope) ---

fun ProblemDTO.toDomain() = HealthProblemDN(
    code = errorCode,
    message = errorMsg?.takeIf { it.isNotBlank() } ?: "خطای نامشخص"
)

// --- Domain request → DTO request converters ---

fun UpdatePatientRequest.toDTO() = UpdatePatientRequestDTO(
    patientID = patientID,
    patientNatCode = patientNatCode,
    patientMobile = patientMobile,
    patientEmail = patientEmail,
    patientAddress = patientAddress,
    patientArea = patientArea,
    patientCityID = patientCityID,
    patientBloodGroup = patientBloodGroup,
    patientMarriage = patientMarriage,
    patientJob = patientJob,
    patientHeight = patientHeight,
    patientWeight = patientWeight,
    patientCitizenship = patientCitizenship,
    patientNationality = patientNationality,
    patientInsurance = patientInsurance,
    emergencyName = emergencyName,
    emergencyFamily = emergencyFamily,
    emergencyMobile = emergencyMobile,
    emergencyEmail = emergencyEmail,
    emergencyRelation = emergencyRelation,
    emergencyAddress = emergencyAddress,
    emergencyArea = emergencyArea,
    emergencyCityID = emergencyCityID
)

fun AddSelfDeclarativeRequest.toDTO() = AddSelfDeclarativeRequestDTO(
    natCode = natCode,
    patientID = patientID,
    smoking = smoking,
    alcoholUse = alcoholUse,
    substanceUse = substanceUse,
    exerciseFrequency = exerciseFrequency,
)

fun UpdateSelfDeclarativeRequest.toDTO() = UpdateSelfDeclarativeRequestDTO(
    patientID = patientID,
    objectID = objectID,
    smoking = smoking,
    alcoholUse = alcoholUse,
    substanceUse = substanceUse,
    exerciseFrequency = exerciseFrequency,
)

fun SyncIllnessSelfDeclarativesRequest.toDTO() = SyncIllnessesSelfDecRequestDTO(
    natCode = natCode,
    patientID = patientID,
    illnessSelfDeclareList = illnessSelfDeclareList?.map {
        IllnessSelfDeclareDTO(illnessID = it.illnessID, relation = it.relation, illnessComments = it.illnessComments)
    }
)

fun SyncDrugAllergiesRequest.toDTO() = SyncDrugAllergiesRequestDTO(
    natCode = natCode,
    patientID = patientID,
    drugAllergyList = drugAllergyList?.map {
        DrugAllergyDTO(drugId = it.drugId, allergyComments = it.allergyComments)
    }
)

