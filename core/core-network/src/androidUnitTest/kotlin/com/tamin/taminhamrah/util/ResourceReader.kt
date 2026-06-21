package com.tamin.taminhamrah.util

actual fun readResourceFile(path: String): String {
    val classLoader = ApiTestUtils::class.java.classLoader
    return classLoader?.getResourceAsStream(path)?.bufferedReader()?.use { it.readText() }
        ?: throw IllegalArgumentException("Resource not found: $path")
}
