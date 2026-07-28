package com.tamin.taminhamrah.tools

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.client.statement.readRawBytes
import io.ktor.utils.io.ByteReadChannel

/**
 * Reads raw bytes from an active [HttpStatement] into an in-memory [ByteReadChannel].
 * `HttpStatement.body<ByteReadChannel>()` returns a channel that is already finalized —
 * draining it later yields nothing — so the bytes are pulled inside `execute` and re-wrapped.
 * Useful anywhere in the app when streaming binary payloads (PDFs, images, files, documents).
 */
suspend fun HttpStatement.readByteChannel(): ByteReadChannel =
    ByteReadChannel(execute { it.readRawBytes() })

/**
 * Convenience alias for [readByteChannel] when handling PDF document streams.
 */
suspend fun HttpStatement.readPdfChannel(): ByteReadChannel = readByteChannel()

