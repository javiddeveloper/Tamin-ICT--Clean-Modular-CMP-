package com.tamin.taminhamrah.model.subdominant

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DependentRelationDNTest {

    @Test
    fun `codes resolve like the native RelationEnum`() {
        assertEquals(DependentRelationDN.SON, DependentRelationDN.of("104", null))
        assertEquals(DependentRelationDN.DAUGHTER, DependentRelationDN.of("102", "02"))
        assertEquals(DependentRelationDN.MOTHER, DependentRelationDN.of("106", "02"))
        assertEquals(DependentRelationDN.FATHER, DependentRelationDN.of("110", "01"))
        assertEquals(DependentRelationDN.PARENT, DependentRelationDN.of("106", null))
        assertEquals(DependentRelationDN.ADOPTED_CHILD, DependentRelationDN.of("133", null))
        assertEquals(DependentRelationDN.BROTHER, DependentRelationDN.of("124", "01"))
        assertEquals(DependentRelationDN.SURVIVOR, DependentRelationDN.of("124", null))
        assertEquals(DependentRelationDN.SPOUSE, DependentRelationDN.of("108", "02"))
        assertEquals(DependentRelationDN.DAUGHTER, DependentRelationDN.of("112", "02"))
        assertEquals(DependentRelationDN.CHILD, DependentRelationDN.of("117", null))
        assertNull(DependentRelationDN.of("999", "01"))
        assertNull(DependentRelationDN.of(null, "01"))
    }

    @Test
    fun `a filter without gender accepts every relation the code can mean`() {
        assertEquals(
            setOf(DependentRelationDN.FATHER, DependentRelationDN.MOTHER, DependentRelationDN.PARENT),
            DependentRelationDN.acceptedBy("106", null),
        )
        assertEquals(setOf(DependentRelationDN.MOTHER), DependentRelationDN.acceptedBy("106", "02"))
        assertEquals(
            setOf(DependentRelationDN.SON, DependentRelationDN.DAUGHTER, DependentRelationDN.CHILD),
            DependentRelationDN.acceptedBy("111", null),
        )
        assertTrue(DependentRelationDN.acceptedBy("000", null).isEmpty())
    }

    @Test
    fun `children are sons, daughters, adopted and unspecified children`() {
        assertEquals(
            setOf(DependentRelationDN.SON, DependentRelationDN.DAUGHTER, DependentRelationDN.CHILD, DependentRelationDN.ADOPTED_CHILD),
            DependentRelationDN.entries.filter { it.isChild }.toSet(),
        )
    }
}
