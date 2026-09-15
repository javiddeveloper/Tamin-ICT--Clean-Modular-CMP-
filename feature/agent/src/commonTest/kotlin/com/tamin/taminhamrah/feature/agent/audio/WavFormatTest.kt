package com.tamin.taminhamrah.feature.agent.audio

import kotlin.test.Test
import kotlin.test.assertEquals

class WavFormatTest {

    private fun ByteArray.int32(at: Int) = (0..3).sumOf { (this[at + it].toInt() and 0xFF) shl (8 * it) }
    private fun ByteArray.int16(at: Int) = (0..1).sumOf { (this[at + it].toInt() and 0xFF) shl (8 * it) }
    private fun ByteArray.ascii(at: Int) = (0..3).map { this[at + it].toInt().toChar() }.joinToString("")

    @Test
    fun `the header describes 16 kHz mono 16-bit PCM and the data that follows`() {
        val header = WavFormat.header(dataSize = 32_000)

        assertEquals(44, header.size)
        assertEquals("RIFF", header.ascii(0))
        assertEquals(36 + 32_000, header.int32(4))
        assertEquals("WAVE", header.ascii(8))
        assertEquals("fmt ", header.ascii(12))
        assertEquals(16, header.int32(16))
        assertEquals(1, header.int16(20))
        assertEquals(1, header.int16(22))
        assertEquals(16_000, header.int32(24))
        assertEquals(32_000, header.int32(28))
        assertEquals(2, header.int16(32))
        assertEquals(16, header.int16(34))
        assertEquals("data", header.ascii(36))
        assertEquals(32_000, header.int32(40))
    }
}
