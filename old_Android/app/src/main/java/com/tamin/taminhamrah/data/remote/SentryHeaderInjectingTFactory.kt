package com.tamin.taminhamrah.data.remote

import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.di.interceptor.TokenHolder
import io.sentry.Hint
import io.sentry.ITransportFactory
import io.sentry.RequestDetails
import io.sentry.SentryEnvelope
import io.sentry.SentryOptions
import io.sentry.transport.AsyncHttpTransport
import io.sentry.transport.CurrentDateProvider.getInstance
import io.sentry.transport.ITransport
import io.sentry.transport.RateLimiter

@Suppress("UnstableApiUsage")
class HeaderInjectingTransportFactory(
    private val customHeaderName: String,
    private val pref: PreferenceManager
) : ITransportFactory {
    override fun create(options: SentryOptions, requestDetails: RequestDetails): ITransport {
        options.connectionTimeoutMillis = 10_000
        options.readTimeoutMillis = 10_000

        val dateProvider = getInstance()
        val rateLimiter = RateLimiter(dateProvider, options)
        val transportGate = options.transportGate

        return LazyTokenTransport(
            AsyncHttpTransport(options, rateLimiter, transportGate, requestDetails),
            requestDetails,
            customHeaderName,
            pref
        )
    }
}

@Suppress("UnstableApiUsage")
class LazyTokenTransport(
    private val delegate: ITransport,
    private val requestDetails: RequestDetails,
    private val headerName: String,
    private val pref: PreferenceManager
) : ITransport by delegate {

    override fun send(envelope: SentryEnvelope, hint: Hint) {
        val token = TokenHolder.getAccessToken(pref)
        if (token.isNullOrEmpty()) {
            return // if the token is not available, the submission will be canceled
        }
        requestDetails.headers[headerName] = token
        delegate.send(envelope, hint)
    }

    override fun send(envelope: SentryEnvelope) {
        val token = TokenHolder.getAccessToken(pref)
        if (token.isNullOrEmpty()) return
        requestDetails.headers[headerName] = token
        delegate.send(envelope)
    }

    override fun isHealthy(): Boolean {
        return delegate.isHealthy
    }
}