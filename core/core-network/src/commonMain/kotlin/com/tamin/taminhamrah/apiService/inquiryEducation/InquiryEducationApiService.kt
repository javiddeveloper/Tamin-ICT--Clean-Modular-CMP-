package com.tamin.taminhamrah.apiService.inquiryEducation

import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsListDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface InquiryEducationApiService {

    @GET("subdominants/getDataForEducation")
    suspend fun getDataForEducation(): BaseDTO<EducationDependentsListDTO>

    @GET("subdominants/extendEducation/{code}/{educationCode}")
    suspend fun inquiryEducationCertificate(
        @Path("code") code: String,
        @Path("educationCode") educationCode: String,
    ): BaseDTO<String?>
}
