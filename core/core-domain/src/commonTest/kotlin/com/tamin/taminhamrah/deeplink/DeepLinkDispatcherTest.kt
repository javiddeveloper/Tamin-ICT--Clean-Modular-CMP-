package com.tamin.taminhamrah.deeplink

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DeepLinkDispatcherTest {

    @Test
    fun `links submitted before anyone listens wait and arrive in order`() = runTest {
        val dispatcher = DeepLinkDispatcher()
        dispatcher.submit("@wedding_present", DeepLinkSource.SYSTEM)
        dispatcher.submit("@agent", DeepLinkSource.APP_CONTENT)

        assertEquals(
            listOf("@wedding_present" to DeepLinkSource.SYSTEM, "@agent" to DeepLinkSource.APP_CONTENT),
            dispatcher.links.take(2).toList().map { it.uri to it.source },
        )
    }

    @Test
    fun `the sender's callback travels with the link`() = runTest {
        val dispatcher = DeepLinkDispatcher()
        var opened = false
        dispatcher.submit("@agent", DeepLinkSource.APP_CONTENT) { opened = true }

        dispatcher.links.first().onOpened()

        assertEquals(true, opened)
    }

    @Test
    fun `each link is delivered once and blanks are ignored`() = runTest {
        val dispatcher = DeepLinkDispatcher()
        dispatcher.submit(" ", DeepLinkSource.SYSTEM)
        dispatcher.submit("@laws", DeepLinkSource.SYSTEM)
        assertEquals("@laws", dispatcher.links.first().uri)

        dispatcher.submit("@agent", DeepLinkSource.SYSTEM)
        assertEquals("@agent", dispatcher.links.first().uri)
    }
}
