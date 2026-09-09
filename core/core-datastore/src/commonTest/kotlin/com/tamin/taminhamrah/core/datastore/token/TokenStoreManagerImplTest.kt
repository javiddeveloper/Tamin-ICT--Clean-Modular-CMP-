package com.tamin.taminhamrah.core.datastore.token

import com.russhwolf.settings.MapSettings
import com.tamin.taminhamrah.model.auth.TokenSlot
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Covers the gate this feature's own commit message says used to be missing: in a release build
 * (`isDebug = false`), whatever a previous debug install left in shared storage must never make
 * the app read or switch to a debug slot — [TokenStoreManagerImpl.getActiveSlot] and
 * [TokenStoreManagerImpl.getToken] must always resolve to [TokenSlot.USER].
 */
class TokenStoreManagerImplTest {

    @Test
    fun `release build ignores a debug slot left in storage by a previous debug install`() {
        val settings = MapSettings()
        // Written directly against the underlying storage, as a previous debug install would have
        // left it - not through setActiveSlot, which itself refuses to run outside a debug build.
        settings.putString("ACTIVE_TOKEN_SLOT", TokenSlot.BACK_TO_BACK.name)
        settings.putString("TOKEN", "user-token")
        settings.putString("TOKEN_${TokenSlot.BACK_TO_BACK.name}", "back-to-back-token")

        val manager = TokenStoreManagerImpl(settings, isDebug = false)

        assertEquals(TokenSlot.USER, manager.getActiveSlot())
        assertEquals("user-token", manager.getToken())
    }

    @Test
    fun `debug build resolves the stored active slot`() {
        val settings = MapSettings()
        settings.putString("ACTIVE_TOKEN_SLOT", TokenSlot.BACK_TO_BACK.name)
        settings.putString("TOKEN", "user-token")
        settings.putString("TOKEN_${TokenSlot.BACK_TO_BACK.name}", "back-to-back-token")

        val manager = TokenStoreManagerImpl(settings, isDebug = true)

        assertEquals(TokenSlot.BACK_TO_BACK, manager.getActiveSlot())
        assertEquals("back-to-back-token", manager.getToken())
    }

    @Test
    fun `setActiveSlot is a no-op outside a debug build`() = runTest {
        val settings = MapSettings()
        val manager = TokenStoreManagerImpl(settings, isDebug = false)

        manager.setActiveSlot(TokenSlot.BACK_TO_BACK)

        assertEquals(TokenSlot.USER, manager.getActiveSlot())
    }

    @Test
    fun `a real login always resolves the active slot back to USER even if a debug slot was active`() = runTest {
        val settings = MapSettings()
        val manager = TokenStoreManagerImpl(settings, isDebug = true)
        manager.saveToken(TokenSlot.BACK_TO_BACK, "back-to-back-token")
        manager.setActiveSlot(TokenSlot.BACK_TO_BACK)
        assertEquals(TokenSlot.BACK_TO_BACK, manager.getActiveSlot())

        manager.saveToken("real-user-token")

        assertEquals(TokenSlot.USER, manager.getActiveSlot())
        assertEquals("real-user-token", manager.getToken())
    }

    @Test
    fun `logging out always resolves the active slot back to USER even if a debug slot was active`() = runTest {
        val settings = MapSettings()
        val manager = TokenStoreManagerImpl(settings, isDebug = true)
        manager.saveToken(TokenSlot.BACK_TO_BACK, "back-to-back-token")
        manager.setActiveSlot(TokenSlot.BACK_TO_BACK)

        manager.saveToken(null)

        assertEquals(TokenSlot.USER, manager.getActiveSlot())
    }
}
