package com.tamin.taminhamrah.core.datastore

import com.russhwolf.settings.MapSettings
import com.tamin.taminhamrah.model.BaseUrlKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * Covers the persistence contract of dev_opt_base_url_* overrides: resetting to default must
 * actually drop the stored value (not just stop reporting it), and a saved override must
 * always end in a trailing slash so it can be concatenated with a relative path or handed to
 * Ktorfit (which requires @Url base paths to end with "/").
 */
class DeveloperOptionsRepositoryImplTest {

    @Test
    fun `setOverride then clearOverride restores the compiled-in default`() {
        val settings = MapSettings()
        val repository = DeveloperOptionsRepositoryImpl(settings, isDebug = true)

        repository.setOverride(BaseUrlKey.MAIN, "https://custom.example.com/api/")
        assertEquals("https://custom.example.com/api/", repository.getEffectiveBaseUrl(BaseUrlKey.MAIN))

        repository.clearOverride(BaseUrlKey.MAIN)

        assertEquals(BaseUrlKey.MAIN.defaultValue, repository.getEffectiveBaseUrl(BaseUrlKey.MAIN))
        assertFalse(settings.hasKey("dev_opt_base_url_MAIN"))
    }

    @Test
    fun `clearOverride also removes the value from a freshly loaded repository`() {
        val settings = MapSettings()
        DeveloperOptionsRepositoryImpl(settings, isDebug = true).setOverride(BaseUrlKey.ACCOUNT, "https://custom.example.com/auth/")

        val repository = DeveloperOptionsRepositoryImpl(settings, isDebug = true)
        repository.clearOverride(BaseUrlKey.ACCOUNT)

        assertEquals(BaseUrlKey.ACCOUNT.defaultValue, repository.getEffectiveBaseUrl(BaseUrlKey.ACCOUNT))
    }

    @Test
    fun `setOverride appends a trailing slash when the input is missing one`() {
        val settings = MapSettings()
        val repository = DeveloperOptionsRepositoryImpl(settings, isDebug = true)

        repository.setOverride(BaseUrlKey.AI, "http://172.16.15.54:9001")

        assertEquals("http://172.16.15.54:9001/", repository.getEffectiveBaseUrl(BaseUrlKey.AI))
    }

    @Test
    fun `setOverride trims whitespace and keeps an existing trailing slash intact`() {
        val settings = MapSettings()
        val repository = DeveloperOptionsRepositoryImpl(settings, isDebug = true)

        repository.setOverride(BaseUrlKey.HEALTH_PROFILE, "  http://172.16.14.115:5700/api/  ")

        assertEquals("http://172.16.14.115:5700/api/", repository.getEffectiveBaseUrl(BaseUrlKey.HEALTH_PROFILE))
    }

    @Test
    fun `in release builds overrides are ignored and compiled-in default is always returned`() {
        val settings = MapSettings()
        val repository = DeveloperOptionsRepositoryImpl(settings, isDebug = false)

        repository.setOverride(BaseUrlKey.MAIN, "https://custom.example.com/api/")

        assertEquals(BaseUrlKey.MAIN.defaultValue, repository.getEffectiveBaseUrl(BaseUrlKey.MAIN))
    }

    @Test
    fun `datastoreModule resolves DeveloperOptionsRepository from Koin`() {
        val koinApp = org.koin.dsl.koinApplication {
            modules(
                com.tamin.taminhamrah.core.datastore.di.datastoreModule,
                org.koin.dsl.module {
                    single<com.russhwolf.settings.Settings> { MapSettings() }
                },
            )
        }
        val repository = koinApp.koin.getOrNull<com.tamin.taminhamrah.repository.DeveloperOptionsRepository>()
        kotlin.test.assertNotNull(repository)
        koinApp.close()
    }
}
