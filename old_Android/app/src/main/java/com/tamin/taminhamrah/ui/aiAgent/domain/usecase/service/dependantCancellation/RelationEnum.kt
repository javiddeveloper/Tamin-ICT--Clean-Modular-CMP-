package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation

import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.RelationEnum.BROTHER
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.RelationEnum.CHILD
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.RelationEnum.DAUGHTER
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.RelationEnum.EMPTY
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.RelationEnum.FATHER
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.RelationEnum.GOD_CHILD
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.RelationEnum.MOTHER
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.RelationEnum.PARENT
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.RelationEnum.REMAINED
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.RelationEnum.SISTER
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.RelationEnum.SON
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.RelationEnum.WIFE

enum class RelationEnum(val title: String) {
    FATHER("پدر"),
    MOTHER("مادر"),
    DAUGHTER("دختر"),
    PARENT("والدین"),
    SON("پسر"),
    SISTER("خواهر"),
    BROTHER("برادر"),
    GOD_CHILD("فرزند خوانده"),
    CHILD("فرزند"),
    REMAINED("بازمانده"),
    WIFE("همسر"),
    EMPTY("");

    companion object {
        fun getRelation(relationCode: String, genderCode: String) : RelationEnum {

            return when (relationCode) {
                "101", "104" -> SON
                "102", "105" -> DAUGHTER
                "106", "110" -> {
                    when (genderCode) {
                        "02" -> MOTHER
                        "01" -> FATHER
                        else -> PARENT
                    }
                }

                "123", "118", "133" -> GOD_CHILD
                "124" -> {
                    when (genderCode) {
                        "02" -> SISTER
                        "01" -> BROTHER
                        else -> REMAINED
                    }
                }

                "100", "103", "107", "108", "109" -> WIFE
                "111", "112", "117" -> {
                    when (genderCode) {
                        "01" -> SON
                        "02" -> DAUGHTER
                        else -> CHILD
                    }
                }

                else -> EMPTY
            }
        }
    }

}


