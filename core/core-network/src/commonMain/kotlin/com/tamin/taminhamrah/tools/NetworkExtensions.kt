package com.tamin.taminhamrah.tools

import com.tamin.taminhamrah.tools.errorHandling.errorFromHttpBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel

/**
 * Reads raw bytes from an active [HttpStatement] into an in-memory [ByteReadChannel].
 * `HttpStatement.body<ByteReadChannel>()` returns a channel that is already finalized —
 * draining it later yields nothing — so the bytes are pulled inside `execute` and re-wrapped.
 * Useful anywhere in the app when streaming binary payloads (PDFs, images, files, documents).
 */
suspend fun HttpStatement.readByteChannel(): ByteReadChannel = ByteReadChannel(readBytesOrThrow())

/**
 * Convenience alias for [readByteChannel] when handling PDF document streams.
 */
suspend fun HttpStatement.readPdfChannel(): ByteReadChannel = readByteChannel()

/**
 * The payload of a successful reply, or the failure its body describes. Without the status check a
 * failed download handed the server's error JSON to the viewer as if it were the file — the old
 * app's `getPdfResult` checked `isSuccessful` first.
 */
suspend fun HttpStatement.readBytesOrThrow(): ByteArray = execute { it.bytesOrThrow() }

private suspend fun HttpResponse.bytesOrThrow(): ByteArray =
    if (status.isSuccess()) readRawBytes() else throw errorFromHttpBody(status.value, bodyAsText())
