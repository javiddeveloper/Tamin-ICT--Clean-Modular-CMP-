package com.tamin.taminhamrah.util

actual fun readResourceFile(path: String): String {
    val normalizedPath = path.removePrefix("/")
    val classLoaders = listOfNotNull(
        Thread.currentThread().contextClassLoader,
        ApiTestUtils::class.java.classLoader,
        ClassLoader.getSystemClassLoader(),
    ).distinct()

    for (classLoader in classLoaders) {
        classLoader.getResourceAsStream(normalizedPath)?.bufferedReader()?.use { return it.readText() }
    }

    throw IllegalArgumentException("Resource not found: $path")
}
