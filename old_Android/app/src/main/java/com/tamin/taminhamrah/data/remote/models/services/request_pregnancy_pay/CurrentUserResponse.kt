package com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class CurrentUserResponse(

    var data: CurrentUserModel? = null

) : BaseResponseNew()

    data class CurrentUserModel(
        val accountStatus: String?,
        val birthDate: Long?,
        val email: String?,
        val entityId: String?,
        val firstName: String?,
        val gender: String?,
        val lastName: String?,
        val login: String?,
        val mobile: String?,
        val nationalCode: String?,
        val organization: Organization?,
        val organizationKey: Int?,
        val roles: List<Role?>?,
        val status: String?,
        val userDetail: UserDetail?
    ) {
        data class Organization(
            val children: Any?,
            val code: String?,
            val entityId: String?,
            val organizationCustomerType: String?,
            val organizationDetail: Any?,
            val organizationName: String?,
            val organizationStatus: String?,
            val parent: Any?
        )

        data class Role(
            val email: Any?,
            val id: String?,
            val ownerKey: Any?,
            val roleCategory: String?,
            val roleDescription: String?,
            val roleDisplayName: String?,
            val roleName: String?,
            val roleUniqueName: String?
        )

        data class UserDetail(
            val geoUnit: GeoUnit?,
            val id: Int?,
            val oimUserId: String?,
            val userDetailDevices: Any?,
            val userDetailWidgets: Any?
        ) {
            data class GeoUnit(
                val code: String?,
                val description: String?,
                val id: Int?,
                val isDefault: Any?,
                val parent: Parent?,
                val title: String?,
                val type: Type?
            ) {
                data class Parent(
                    val code: String?,
                    val description: String?,
                    val id: Int?,
                    val isDefault: Any?,
                    val parent: Any?,
                    val title: String?,
                    val type: Type?
                ) {
                    data class Type(
                        val code: String?,
                        val id: Int?,
                        val parent: Any?,
                        val title: String?
                    )
                }

                data class Type(
                    val code: String?,
                    val id: Int?,
                    val parent: Parent?,
                    val title: String?
                ) {
                    data class Parent(
                        val code: String?,
                        val id: Int?,
                        val parent: Any?,
                        val title: String?
                    )
                }
            }
        }
    }
