package com.tamin.taminhamrah.model.constructionInsurance

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import kotlin.test.Test
import kotlin.test.assertEquals

class ConstructionFileSearchParamsDNMapperTest {

    @Test
    fun `null search maps to a query with no filters`() {
        val query = null.toApiQueryParam()

        assertEquals(emptyList(), query.filters)
    }

    @Test
    fun `blank fields are omitted as filters`() {
        val search = ConstructionFileSearchParamsDN(fileNo = "", reqNo = null, workshopId = "  ", branchCode = null)

        val query = search.toApiQueryParam()

        assertEquals(emptyList(), query.filters)
    }

    @Test
    fun `non-blank fields map to EQ filters keyed by property`() {
        val search = ConstructionFileSearchParamsDN(
            fileNo = "1234",
            reqNo = "5678",
            workshopId = "9012",
            branchCode = "6400",
        )

        val query = search.toApiQueryParam()

        assertEquals(
            listOf(
                ApiFilterDN(FilterProperty.FILE_NO, "1234", FilterOperator.EQ),
                ApiFilterDN(FilterProperty.REQ_NO, "5678", FilterOperator.EQ),
                ApiFilterDN(FilterProperty.WORKSHOP_ID, "9012", FilterOperator.EQ),
                ApiFilterDN(FilterProperty.WORKSHOP_BRANCH_CODE, "6400", FilterOperator.EQ),
            ),
            query.filters,
        )
    }
}
