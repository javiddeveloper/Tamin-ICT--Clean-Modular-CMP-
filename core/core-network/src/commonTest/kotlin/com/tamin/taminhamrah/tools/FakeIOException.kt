package com.tamin.taminhamrah.tools

/**
 * A transport failure, for fakes that stand in for a call that never reached the server.
 *
 * The real thing has a different type on every platform — `java.io.IOException` on Android,
 * Darwin's own on iOS — and none of them exist in `commonTest`. The data sources' template maps
 * it through its last catch (`Exception` → `NO_CONNECTION_ERROR`); naming the double after the
 * failure keeps the tests saying which case they mean.
 */
class FakeIOException(message: String = "network") : Exception(message)
