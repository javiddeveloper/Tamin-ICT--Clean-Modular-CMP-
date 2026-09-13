package com.tamin.taminhamrah.tools

/**
 * A transport failure, as [isConnectivityFailure] recognises one.
 *
 * That check matches on the exception's **simple name**, because the real thing has a different
 * type on every platform — `java.io.IOException` on Android, Darwin's own on iOS — and none of
 * them exist in `commonTest`. A double that carries the name is therefore the accurate stand-in,
 * not a trick: it is recognized by exactly the rule production code uses.
 *
 * Fakes here used to throw `IllegalStateException("network")` for the same purpose. That passed
 * only while *every* unrecognized failure was reported as `NO_CONNECTION_ERROR`, so those tests
 * proved nothing about connection mapping — they would have passed against a data source that
 * called every error a connection problem, which is precisely the bug that behavior caused.
 */
class FakeIOException(message: String = "network") : Exception(message)
