package com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.extentions.ifCreateValueIsBlank
import com.tamin.taminhamrah.utils.extentions.isNumericString

class DependentInfoResponse : ListDataModel<DependentDataModel>()

data class DependentDataModel(
    @SerializedName("relationWithTamin")
    val dependentInfo: DependentInfoModel = DependentInfoModel(),
) {
    fun getDependentInfo(): List<KeyValueModel> {
        val info = dependentInfo.identityInfo
        val stringResId = dependentInfo.familyRelationShip.getRelationResId(info.gender.genderCode)
        val expireDate = info.subDominant?.dateOfExpire

        return mutableListOf(
            KeyValueModel(
                _keyStringResId = R.string.full_name,
                _isValueBold = true,
                _value = if (info.firstName.plus(info.lastName)
                        .isBlank()
                ) "_" else "${info.firstName} ${info.lastName}"
            ),
            KeyValueModel(
                _keyStringResId = R.string.relative,
                _textColor = EnumTextColor.GREEN,
                _valueStringResId = stringResId
            ),

            KeyValueModel(
                _keyStringResId = R.string.label_national_code,
                _value = info.nationalId ?: ""
            ),

            KeyValueModel(
                _keyStringResId = R.string.birthdate,
                _value = if (info.dateOfBirth?.isNumericString() == true)
                    ConvertDate.convertTimestampToPersianDate(info.dateOfBirth) else ""
            ),

            KeyValueModel(
                _keyStringResId = R.string.identity_number,
                _value = info.idCardNumber ?: ""
            ),

            KeyValueModel(
                _keyStringResId = R.string.termination_date,
                _value = if (expireDate?.isNumericString() == true)
                    ConvertDate.convertTimestampToPersianDate(expireDate) else ""
            )
        )
    }

    fun createKeyValue(): List<com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel> {
        val info = dependentInfo.identityInfo
        val stringRes = dependentInfo.familyRelationShip.getRelationRes(info.gender.genderCode)
        val expireDate = info.subDominant?.dateOfExpire

        return listOfNotNull(
            com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel(
                _key = "نام و نام خانوادگی",
                _value = if (info.firstName.plus(info.lastName)
                        .isBlank()
                ) "---" else "${info.firstName} ${info.lastName}"
            ),
            com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel(
                _key = "نسبت",
                _value = stringRes.ifCreateValueIsBlank()
            ),
            info.nationalId?.let {
                com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel(
                    _key = "کد ملی",
                    _value = it.ifCreateValueIsBlank()
                )
            },
            info.dateOfBirth?.let {
                com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel(
                    _key = "تاریخ تولد",
                    _value = if (it.isNumericString()) ConvertDate.convertTimestampToPersianDate(
                        info.dateOfBirth
                    ) else "-"
                )
            },
            info.idCardNumber?.let {
                com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel(
                    _key = "شماره شناسنامه",
                    _value = it.ifCreateValueIsBlank()
                )
            },
            expireDate?.let {
                com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel(
                    _key = "تاریخ خاتمه ارتباط",
                    _value = if (it.isNumericString()) ConvertDate.convertTimestampToPersianDate(
                        expireDate.ifCreateValueIsBlank()
                    ) else "-"
                )
            }
        )
    }

}

data class DependentInfoModel(
    @SerializedName("personal")
    val identityInfo: DependentIdentityInfo = DependentIdentityInfo(),
    @SerializedName("relationWithTamin")
    val familyRelationShip: FamilyRelationShip = FamilyRelationShip(),
)

data class DependentIdentityInfo(
    val dateOfBirth: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val idCardNumber: String? = null,
    val idCardSerial1: String? = null,
    val idCardSerial2: String? = null,
    val nationalId: String? = null,
    val gender: Gender = Gender(),
    val subDominant: InsuranceInfo? = null,
)

data class InsuranceInfo(
    val dateOfExpire: String?,
)

data class FamilyRelationShip(
    @SerializedName("baseTendency")
    val relationDetail: RelationDetail = RelationDetail(),
) {
    fun getRelationResId(genderCode: String?) = when (relationDetail.relationCode) {
        "124" -> {
            R.string.survivor
        }

        "106", "110" -> {
            when (genderCode) {
                "01" -> R.string.father
                "02" -> R.string.mother
                else -> R.string.parents
            }
        }

        "101", "104" -> {
            R.string.son
        }

        "102", "105" -> {
            R.string.daughter
        }

        "111", "112", "117" -> {
            when (genderCode) {
                "01" -> R.string.son
                "02" -> R.string.daughter
                else -> R.string.child
            }
        }

        "133", "118", "123" -> {
            R.string.step_child
        }

        "100", "103", "107", "108", "109" -> {
            R.string.spouse
        }

        else -> {
            0
        }
    }

    fun getRelationRes(genderCode: String?) = when (relationDetail.relationCode) {
        "124" -> {
            "بازمانده"
        }

        "106", "110" -> {
            when (genderCode) {
                "01" -> "پدر"
                "02" -> "مادر"
                else -> "والدین"
            }
        }

        "101", "104" -> {
            "فرزند پسر"
        }

        "102", "105" -> {
            "فرزند دختر"
        }

        "111", "112", "117" -> {
            when (genderCode) {
                "01" -> "فرزند پسر"
                "02" -> "فرزند دختر"
                else -> "فرزند"
            }
        }

        "133", "118", "123" -> {
            "فرزند خوانده"
        }

        "100", "103", "107", "108", "109" -> {
            "همسر"
        }

        else -> {
            ""
        }
    }

}


data class RelationDetail(
    @SerializedName("tendencyCode")
    val relationCode: String = "",
)

data class Gender(
    val genderCode: String? = null,
)



