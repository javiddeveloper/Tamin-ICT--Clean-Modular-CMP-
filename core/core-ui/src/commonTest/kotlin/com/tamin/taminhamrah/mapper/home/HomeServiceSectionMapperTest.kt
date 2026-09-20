package com.tamin.taminhamrah.mapper.home

import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.model.home.HomeServiceSection
import com.tamin.taminhamrah.model.home.SectionPlacement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HomeServiceSectionMapperTest {

    private fun service(
        flag: FeatureFlag,
        status: MenuServiceStatusDN? = MenuServiceStatusDN.ACTIVE,
    ) = MainServiceDN(id = flag.id, name = flag.name, status = status)

    @Test
    fun `servicesOf returns members in the section's declared order, not the menu order`() {
        val section = HomeServiceSection.AID
        // menu deliberately shuffled relative to section.members
        val menu = section.members.reversed().map { service(it) }

        val resolved = menu.servicesOf(section).mapNotNull { it.id }

        assertEquals(section.members.map { it.id }, resolved)
    }

    @Test
    fun `servicesOf skips members the menu does not carry`() {
        val section = HomeServiceSection.AID
        val present = section.members.first()
        val menu = listOf(service(present))

        val resolved = menu.servicesOf(section)

        assertEquals(listOf(present.id), resolved.map { it.id })
    }

    @Test
    fun `servicesOf drops COMPLETELY_DISABLED but keeps TEMPORARY_DISABLED and DISABLED`() {
        val section = HomeServiceSection.EMPLOYER
        val (a, b, c) = section.members
        val menu = listOf(
            service(a, MenuServiceStatusDN.COMPLETELY_DISABLED),
            service(b, MenuServiceStatusDN.TEMPORARY_DISABLED),
            service(c, MenuServiceStatusDN.DISABLED),
        )

        val resolved = menu.servicesOf(section).map { it.id }

        assertEquals(listOf(b.id, c.id), resolved)
    }

    @Test
    fun `a service that belongs to two sections resolves in both`() {
        // عناوین شغلی is declared in both HISTORY and FEATURED.
        val flag = FeatureFlag.VIEW_TITLE_JOB
        assertTrue(flag in HomeServiceSection.HISTORY.members)
        assertTrue(flag in HomeServiceSection.FEATURED.members)

        val menu = listOf(service(flag))

        assertEquals(listOf(flag.id), menu.servicesOf(HomeServiceSection.HISTORY).map { it.id })
        assertEquals(listOf(flag.id), menu.servicesOf(HomeServiceSection.FEATURED).map { it.id })
    }

    @Test
    fun `featuredServices resolves the FEATURED section in its declared order`() {
        val menu = HomeServiceSection.FEATURED.members.reversed().map { service(it) }

        val resolved = menu.featuredServices().mapNotNull { it.id }

        assertEquals(HomeServiceSection.FEATURED.members.map { it.id }, resolved)
    }

    @Test
    fun `quickAccess and featured partition every section with no overlap`() {
        val quick = HomeServiceSection.quickAccess().toSet()
        val featured = setOf(HomeServiceSection.featured())

        assertEquals(HomeServiceSection.entries.toSet(), quick + featured)
        assertTrue((quick intersect featured).isEmpty())
        assertTrue(quick.all { it.placement == SectionPlacement.QUICK_ACCESS })
    }

    @Test
    fun `every member id maps back to a FeatureFlag`() {
        HomeServiceSection.entries.forEach { section ->
            section.members.forEach { flag ->
                assertEquals(flag, FeatureFlag.fromId(flag.id), "unmapped id in $section")
            }
        }
    }
}
