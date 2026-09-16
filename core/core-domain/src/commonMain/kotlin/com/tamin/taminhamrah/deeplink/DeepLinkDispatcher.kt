package com.tamin.taminhamrah.deeplink

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * A link waiting to be handled, with where it came from. [onOpened] runs only if the link actually
 * opens something — a blocked or invalid link leaves the sender's screen untouched.
 */
data class PendingDeepLink(
    val uri: String,
    val source: DeepLinkSource,
    val onOpened: () -> Unit = {},
)

/**
 * App-wide inbox for deep links.
 *
 * Anything that receives a link — the Android activity, the iOS app delegate, a story, the
 * assistant — only calls [submit]. The navigation host is the one consumer: it collects [links]
 * once it can actually navigate (logged in, graph built), so a link that arrives on a cold start
 * waits here instead of being dropped. Each link is delivered once.
 */
class DeepLinkDispatcher {

    private val channel = Channel<PendingDeepLink>(capacity = Channel.BUFFERED)

    val links: Flow<PendingDeepLink> = channel.receiveAsFlow()

    fun submit(uri: String, source: DeepLinkSource, onOpened: () -> Unit = {}) {
        if (uri.isBlank()) return
        channel.trySend(PendingDeepLink(uri, source, onOpened))
    }
}
