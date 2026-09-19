package com.tamin.taminhamrah.feature.agent.audio

/**
 * The one voice format the assistant's upload accepts: WAV, 16 kHz, mono, 16-bit PCM — what the
 * native app recorded with WaveRecorder. The server's firewall rejects any other file (an `.m4a`
 * upload came back as an HTML "Access Denied" 403), so both platforms record exactly this.
 */
object WavFormat {
    const val SAMPLE_RATE = 16_000
    const val CHANNELS = 1
    const val BITS_PER_SAMPLE = 16
    const val HEADER_SIZE = 44
    const val EXTENSION = "wav"
    const val MIME_TYPE = "audio/wav"

    /** The 44-byte RIFF header for [dataSize] bytes of PCM that follow it. */
    fun header(dataSize: Int): ByteArray {
        val byteRate = SAMPLE_RATE * CHANNELS * BITS_PER_SAMPLE / 8
        val blockAlign = CHANNELS * BITS_PER_SAMPLE / 8
        val out = ByteArray(HEADER_SIZE)
        var offset = 0
        fun ascii(text: String) = text.forEach { out[offset++] = it.code.toByte() }
        fun int32(value: Int) = repeat(4) { out[offset++] = (value shr (8 * it)).toByte() }
        fun int16(value: Int) = repeat(2) { out[offset++] = (value shr (8 * it)).toByte() }

        ascii("RIFF"); int32(36 + dataSize); ascii("WAVE")
        ascii("fmt "); int32(16); int16(1); int16(CHANNELS); int32(SAMPLE_RATE); int32(byteRate)
        int16(blockAlign); int16(BITS_PER_SAMPLE)
        ascii("data"); int32(dataSize)
        return out
    }
}
