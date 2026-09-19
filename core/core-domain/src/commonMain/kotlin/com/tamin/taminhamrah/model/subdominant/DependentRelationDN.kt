package com.tamin.taminhamrah.model.subdominant

import com.tamin.taminhamrah.model.common.GenderCodeDN

/**
 * How a dependent is related to the insured, resolved from the relation code (`tendencyCode`)
 * and, where the code alone is ambiguous, the dependent's gender. Mirrors the native `RelationEnum`.
 */
enum class DependentRelationDN {
    SON, DAUGHTER, CHILD, ADOPTED_CHILD, FATHER, MOTHER, PARENT, BROTHER, SISTER, SURVIVOR, SPOUSE;

    val isChild: Boolean get() = this in CHILDREN

    companion object {
        private val CHILDREN = setOf(SON, DAUGHTER, CHILD, ADOPTED_CHILD)

        private val SONS = setOf("101", "104")
        private val DAUGHTERS = setOf("102", "105")
        private val PARENTS = setOf("106", "110")
        private val ADOPTED = setOf("123", "118", "133")
        private val SIBLINGS = setOf("124")
        private val SPOUSES = setOf("100", "103", "107", "108", "109")
        private val CHILDREN_BY_GENDER = setOf("111", "112", "117")

        fun of(tendencyCode: String?, genderCode: String?): DependentRelationDN? {
            val code = tendencyCode?.trim() ?: return null
            val gender = GenderCodeDN.fromCode(genderCode)
            return when (code) {
                in SONS -> SON
                in DAUGHTERS -> DAUGHTER
                in PARENTS -> when (gender) {
                    GenderCodeDN.MALE -> FATHER
                    GenderCodeDN.FEMALE -> MOTHER
                    null -> PARENT
                }
                in ADOPTED -> ADOPTED_CHILD
                in SIBLINGS -> when (gender) {
                    GenderCodeDN.MALE -> BROTHER
                    GenderCodeDN.FEMALE -> SISTER
                    null -> SURVIVOR
                }
                in SPOUSES -> SPOUSE
                in CHILDREN_BY_GENDER -> when (gender) {
                    GenderCodeDN.MALE -> SON
                    GenderCodeDN.FEMALE -> DAUGHTER
                    null -> CHILD
                }
                else -> null
            }
        }

        /**
         * The relations a filter from the assistant accepts. A filter without a gender widens to
         * every relation the code could mean; an unknown code accepts nothing, which the caller
         * treats as "no filter", as the native service did.
         */
        fun acceptedBy(tendencyCode: String?, genderCode: String?): Set<DependentRelationDN> {
            val relation = of(tendencyCode, genderCode) ?: return emptySet()
            if (GenderCodeDN.fromCode(genderCode) != null) return setOf(relation)
            return when (relation) {
                PARENT -> setOf(FATHER, MOTHER, PARENT)
                CHILD -> setOf(SON, DAUGHTER, CHILD)
                else -> setOf(relation)
            }
        }
    }
}
