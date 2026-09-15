package com.tamin.taminhamrah.deeplink

/** Where a link came from. Decides how much a web link is trusted. */
enum class DeepLinkSource {
    /** Model-generated content: web links are limited to [DeepLinkParser.TRUSTED_HOST]. */
    AGENT,

    /** Content the app itself curates, such as story call-to-actions. */
    APP_CONTENT,

    /** Delivered by the operating system (Android intent, iOS URL open). */
    SYSTEM,
}

/** A link after parsing, before any feature flag has been consulted. */
sealed interface ParsedDeepLink {
    data class Feature(val key: DeepLinkKey, val args: Map<String, String>) : ParsedDeepLink
    data class Web(val url: String) : ParsedDeepLink
    data class Prompt(val text: String) : ParsedDeepLink
    data object Invalid : ParsedDeepLink
}

/**
 * Turns every link shape the app accepts into a [ParsedDeepLink]:
 *
 * | Shape                                   | Result            |
 * |-----------------------------------------|-------------------|
 * | `@key` / `@key?a=b`                     | [ParsedDeepLink.Feature] |
 * | `mytamin://feature/key?a=b`             | [ParsedDeepLink.Feature] |
 * | `tamin://feature/FLAG_NAME` (stories)   | [ParsedDeepLink.Feature] |
 * | `agent://nav/key?a=b`                   | [ParsedDeepLink.Feature] |
 * | `agent://prompt?text=...`               | [ParsedDeepLink.Prompt]  |
 * | `https://...`                           | [ParsedDeepLink.Web] (host limited for [DeepLinkSource.AGENT]) |
 *
 * Anything else, including a key missing from [DeepLinkKey], is [ParsedDeepLink.Invalid].
 * Parsing never throws.
 */
object DeepLinkParser {

    const val TRUSTED_HOST = "tamin.ir"

    private const val SCHEME_SEPARATOR = "://"
    private const val HOST_FEATURE = "feature"
    private const val HOST_NAV = "nav"
    private const val HOST_PROMPT = "prompt"
    private const val PROMPT_TEXT_PARAM = "text"
    private val FEATURE_SCHEMES = setOf("mytamin", "tamin")

    fun parse(raw: String?, source: DeepLinkSource): ParsedDeepLink {
        val link = raw?.trim().orEmpty()
        if (link.isEmpty()) return ParsedDeepLink.Invalid

        if (link.startsWith("@")) return feature(link.substring(1))

        val schemeEnd = link.indexOf(SCHEME_SEPARATOR)
        if (schemeEnd <= 0) return ParsedDeepLink.Invalid
        val scheme = link.substring(0, schemeEnd).lowercase()
        val rest = link.substring(schemeEnd + SCHEME_SEPARATOR.length)
        val host = rest.substringBefore('/').substringBefore('?').lowercase()
        val afterHost = rest.substring(host.length)

        return when {
            scheme in FEATURE_SCHEMES && host == HOST_FEATURE -> feature(afterHost.removePrefix("/"))
            scheme == "agent" && host == HOST_NAV -> feature(afterHost.removePrefix("/"))
            scheme == "agent" && host == HOST_PROMPT -> prompt(afterHost)
            scheme == "https" || scheme == "http" -> web(link, scheme, host, source)
            else -> ParsedDeepLink.Invalid
        }
    }

    private fun feature(pathAndQuery: String): ParsedDeepLink {
        val keyPart = pathAndQuery.substringBefore('?').trimEnd('/')
        val key = DeepLinkKey.fromKey(keyPart) ?: return ParsedDeepLink.Invalid
        return ParsedDeepLink.Feature(key, parseQuery(pathAndQuery.substringAfter('?', "")))
    }

    private fun prompt(afterHost: String): ParsedDeepLink {
        val text = parseQuery(afterHost.substringAfter('?', ""))[PROMPT_TEXT_PARAM]
            ?.takeIf { it.isNotBlank() }
            ?: return ParsedDeepLink.Invalid
        return ParsedDeepLink.Prompt(text)
    }

    private fun web(link: String, scheme: String, host: String, source: DeepLinkSource): ParsedDeepLink {
        if (host.isEmpty()) return ParsedDeepLink.Invalid
        if (source == DeepLinkSource.AGENT) {
            val trusted = scheme == "https" && (host == TRUSTED_HOST || host.endsWith(".$TRUSTED_HOST"))
            if (!trusted) return ParsedDeepLink.Invalid
        }
        return ParsedDeepLink.Web(link)
    }

    private fun parseQuery(query: String): Map<String, String> {
        if (query.isBlank()) return emptyMap()
        return query.split('&')
            .filter { it.isNotEmpty() }
            .associate { pair ->
                percentDecode(pair.substringBefore('=')) to percentDecode(pair.substringAfter('=', ""))
            }
            .filterKeys { it.isNotEmpty() }
    }

    /** UTF-8 percent decoding; malformed escapes are kept literally rather than failing. */
    internal fun percentDecode(value: String): String {
        if ('%' !in value && '+' !in value) return value
        val bytes = ArrayList<Byte>(value.length)
        var i = 0
        while (i < value.length) {
            val c = value[i]
            val hex = if (c == '%' && i + 2 <= value.lastIndex) {
                value.substring(i + 1, i + 3).toIntOrNull(16)
            } else {
                null
            }
            when {
                c == '+' -> {
                    bytes.add(' '.code.toByte())
                    i++
                }
                hex != null -> {
                    bytes.add(hex.toByte())
                    i += 3
                }
                else -> {
                    // Copy the literal run up to the next escape in one go, so surrogate pairs
                    // are encoded together instead of one half at a time.
                    val end = (i + 1 until value.length)
                        .firstOrNull { value[it] == '%' || value[it] == '+' } ?: value.length
                    value.substring(i, end).encodeToByteArray().forEach(bytes::add)
                    i = end
                }
            }
        }
        return bytes.toByteArray().decodeToString()
    }
}
