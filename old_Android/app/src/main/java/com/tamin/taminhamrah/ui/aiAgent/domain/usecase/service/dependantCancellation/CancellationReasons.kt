package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation

import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.DependentDataModel

enum class CancellationReasons(val reason: String, val title: String) {
    DEATH(reason = "dead", title = "فوت"),
    DIVORCE(reason = "divorce", title = "طلاق"),
    JOB_MALE(reason = "job_male", title = "اشتغال فرزند پسر"),
    JOB_FEMALE(reason = "job_female", title = "اشتغال فرزند دختر");

    companion object {
        fun getReasonsForWifeOrHusband(): List<CancellationReasons> {
            return listOf(DEATH, DIVORCE)
        }
        fun getReasonsForChild(): List<CancellationReasons> {
            return listOf(DEATH, DIVORCE, JOB_MALE, JOB_FEMALE)
        }
        fun getReasonsForSON(): List<CancellationReasons> {
            return listOf(DEATH, DIVORCE, JOB_MALE)
        }
        fun getReasonsForDAUGHTER(): List<CancellationReasons> {
            return listOf(DEATH, DIVORCE, JOB_FEMALE)
        }
        fun getReasonForParent(): List<CancellationReasons> {
            return listOf(DEATH)
        }
        fun getReasonForSibilings(): List<CancellationReasons> {
            return listOf(DEATH)
        }
        fun getAllReasons(): List<CancellationReasons> {
            return listOf(DEATH, DIVORCE, JOB_MALE, JOB_FEMALE)
        }

    }





}
