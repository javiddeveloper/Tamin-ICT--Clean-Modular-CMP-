package com.tamin.taminhamrah.data.remote.models.profile

import com.tamin.taminhamrah.data.entity.ProfileModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.myDatePicker.utils.MyPersianDate
import timber.log.Timber


data class ProfileResponse(

    var data: ProfileResponseModel? = null

) : BaseResponseNew() {

    inner class ProfileResponseModel(
        var profileStatus: String = "",
        var entityId: String? = null,
        var login: String? = null,
        var organizationKey: Int? = null,
        var firstName: String? = null,
        var lastName: String? = null,
        var middleName: Any? = null,
        var displayName: String? = null,
        var commonName: Any? = null,
        var startDate: Any? = null,
        var endDate: Any? = null,
        var provisioningDate: Any? = null,
        var deprovisioningDate: Any? = null,
        var provisionedDate: Any? = null,
        var deprovisionedDate: Any? = null,
        var policyUpdateEnabled: Any? = null,
        var accountStatus: String? = null,
        var userDisabled: Any? = null,
        var email: String? = null,
        var userType: String? = null,
        var employeeType: String? = null,
        var managerKey: Any? = null,
        var password: String? = null,
        var confitmPassword: Any? = null,
        var passwordWarned: Any? = null,
        var passwordExpired: Any? = null,
        var loginAttemptsCounter: String? = null,
        var passwordReAttemptsCounter: String? = null,
        var changePasswordAtNextLogin: String? = null,
        var passwordMinimumAge: Any? = null,
        var timezone: Any? = null,
        var locale: Any? = null,
        var country: Any? = null,
        var description: Any? = null,
        var employeeNumber: Any? = null,
        var generationQualifier: Any? = null,
        var ldapOrganization: Any? = null,
        var ldapOrganizationUnit: Any? = null,
        var numberFormat: Any? = null,
        var dateFormat: Any? = null,
        var timeFormat: Any? = null,
        var language: Any? = null,
        var territory: Any? = null,
        var nationalCode: String? = null,
        var mobile: String? = null,
        var gender: String? = null,
        var birthDate: Long? = null,
        var automaticallyDeleteDate: Any? = null,
        var accountLockedDate: Any? = null,
        var passwordCantChange: Any? = null,
        var passwordMustChange: String? = null,
        var passwordNeverExpires: Any? = null,
        var creationDate: Any? = null,
        var creationDateFarsi: Any? = null,
        var passwordExpireDate: Long? = null,
        var passwordExpireDateFarsi: Any? = null,
        var passwordWarnDate: Any? = null,
        var manuallyLocked: Any? = null,
        var passwordGenerated: Any? = null,
        var ldapGUID: Any? = null,
        var ldapDN: Any? = null,
        var userDetail: UserDetail? = null,
        var roles: List<Role>? = null,
        var organization: Organization? = null,
        var department: Any? = null,
        var fax: Any? = null,
        var hireDate: Any? = null,
        var homePhone: Any? = null,
        var localityName: Any? = null,
        var homePostalAddress: Any? = null,
        var postalAddress: Any? = null,
        var postalCode: Any? = null,
        var poBox: Any? = null,
        var state: Any? = null,
        var street: Any? = null,
        var telePhoneNumber: Any? = null,
        var title: Any? = null,
        var initials: Any? = null,
        var pager: Any? = null,
        var city: Any? = null,
        private val additionalProperties: Map<String, Any> = HashMap()

    ) {
        var fullName: String = ""
            get() {
                return "$firstName $lastName"
            }
        var id: String = ""
            get() {
                return "$entityId"
            }

        init {
            Timber.tag("loginRepository")
                .i("ProfileResponseModel:init:  firstName=$firstName lastName=$lastName")
            //      fullName = ""
            //    id = ""

        }


    }

    inner class UserDetail {
        var id: Int? = null
        var oimUserId: String? = null
        var geoUnit: GeoUnit? = null
        var userDetailWidgets: Any? = null
        var userDetailDevices: Any? = null
        private val additionalProperties: MutableMap<String, Any> = HashMap()
        fun getAdditionalProperties(): Map<String, Any> {
            return additionalProperties
        }

        fun setAdditionalProperty(name: String, value: Any) {
            additionalProperties[name] = value
        }
    }

    inner class GeoUnit {
        var id: Int? = null
        var code: String? = null
        var title: String? = null
        var description: String? = null
        var type: Type? = null
        var parent: Parent__1? = null
        var isDefault: Any? = null
        private val additionalProperties: MutableMap<String, Any> = HashMap()
        fun getAdditionalProperties(): Map<String, Any> {
            return additionalProperties
        }

        fun setAdditionalProperty(name: String, value: Any) {
            additionalProperties[name] = value
        }
    }

    inner class Organization {
        var entityId: String? = null
        var children: Any? = null
        var create: Any? = null
        var createdBy: Any? = null
        var update: Any? = null
        var updateBy: Any? = null
        var organizationStatus: String? = null
        var organizationName: String? = null
        var organizationCustomerType: String? = null
        var parent: Any? = null
        var code: String? = null
        var organizationDetail: Any? = null
        private val additionalProperties: MutableMap<String, Any> = HashMap()
        fun getAdditionalProperties(): Map<String, Any> {
            return additionalProperties
        }

        fun setAdditionalProperty(name: String, value: Any) {
            additionalProperties[name] = value
        }
    }

    inner class Parent {
        var id: Int? = null
        var code: String? = null
        var title: String? = null
        var parent: Any? = null
        private val additionalProperties: MutableMap<String, Any> = HashMap()
        fun getAdditionalProperties(): Map<String, Any> {
            return additionalProperties
        }

        fun setAdditionalProperty(name: String, value: Any) {
            additionalProperties[name] = value
        }
    }

    inner class Parent__1 {
        var id: Int? = null
        var code: String? = null
        var title: String? = null
        var description: String? = null
        var type: Type__1? = null
        var parent: Any? = null
        var isDefault: Any? = null
        private val additionalProperties: MutableMap<String, Any> = HashMap()
        fun getAdditionalProperties(): Map<String, Any> {
            return additionalProperties
        }

        fun setAdditionalProperty(name: String, value: Any) {
            additionalProperties[name] = value
        }
    }

    class Role {
        var id: String? = null
        var roleName: String? = null
        var roleDisplayName: String? = null
        var roleUniqueName: String? = null
        var roleDescription: String? = null
        var email: Any? = null
        var create: Any? = null
        var createdBy: Any? = null
        var update: Any? = null
        var updateBy: Any? = null
        var ownerKey: Any? = null
        var roleCategory: String? = null
        private val additionalProperties: MutableMap<String, Any> = HashMap()
        fun getAdditionalProperties(): Map<String, Any> {
            return additionalProperties
        }

        fun setAdditionalProperty(name: String, value: Any) {
            additionalProperties[name] = value
        }
    }

    inner class Type {
        var id: Int? = null
        var code: String? = null
        var title: String? = null
        var parent: Parent? = null
        private val additionalProperties: MutableMap<String, Any> = HashMap()
        fun getAdditionalProperties(): Map<String, Any> {
            return additionalProperties
        }

        fun setAdditionalProperty(name: String, value: Any) {
            additionalProperties[name] = value
        }
    }

    inner class Type__1 {
        var id: Int? = null
        var code: String? = null
        var title: String? = null
        var parent: Any? = null
        private val additionalProperties: MutableMap<String, Any> = HashMap()
        fun getAdditionalProperties(): Map<String, Any> {
            return additionalProperties
        }

        fun setAdditionalProperty(name: String, value: Any) {
            additionalProperties[name] = value
        }
    }
}


fun ProfileResponse.asDomainModel(): ProfileModel {
    return ProfileModel(
        id = this.data?.entityId,
        fullName = (if (this.data?.firstName.isNullOrBlank() || this.data?.lastName.isNullOrBlank()) null else this.data?.firstName + " " + this.data?.lastName),
        email = this.data?.email ?: "",
        phonenumber = this.data?.mobile ?: "",
        nationalCode = this.data?.nationalCode ?: ""
    )
}

fun List<ProfileResponse>.asDomainModel(): List<ProfileModel> {
    return map {
        it.asDomainModel()
    }
}

