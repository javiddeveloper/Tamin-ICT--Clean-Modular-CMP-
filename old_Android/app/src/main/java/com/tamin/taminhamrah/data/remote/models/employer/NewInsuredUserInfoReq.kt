package com.tamin.taminhamrah.data.remote.models.employer

import android.net.Uri
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.workshop.RelationWithTamin2

data class NewInsuredUserInfoReq(
    var personal: PersonalInfo = PersonalInfo(),
    var relationWithTamin: RelationWithTamin2 = RelationWithTamin2()
) {
    @Transient
     var tempImageType: String? = null
    @Transient
     var tempImageName: String? = null
    @Transient
    var tempImageUri: Uri? = null
    @Transient
    var imageFileList: ArrayList<UploadedImageModel>? = null
    @Transient
    var selectedCityOfBirth: String? = null
    @Transient
    var selectedCityOfIssue: String? = null
    @Transient
    var selectedjob: String? = null
    @Transient
    var dateOfBirthTimeStamp:Long?=null
    @Transient
    var localDateOfStartJobTimeStamp:Long?=null

}

data class PersonalInfo(
    var nation: String? = "01",
    var countryId: String? = "0001",
    var cityOfBirthId: String? = null,
    var cityOfIssueId: String? = null,
    var dateOfBirth: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var nationalId: String? = null,
    var id:Long?=null
) {

}
