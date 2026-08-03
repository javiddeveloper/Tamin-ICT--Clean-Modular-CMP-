package com.tamin.taminhamrah.ui.image

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import io.ktor.client.HttpClient
import okio.FileSystem

/** Scans are large; a quarter of the app's heap keeps a screenful warm without crowding it out. */
private const val MEMORY_CACHE_FRACTION = 0.25

/**
 * Capped deliberately: scanned documents are big and this cache lives in temporary storage, so an
 * uncapped one grows until the platform evicts it wholesale.
 */
private const val DISK_CACHE_FRACTION = 0.02

/**
 * Coil, fetching through the app's authenticated Ktor client.
 *
 * Document thumbnails sit behind the same auth as the API, so they are fetched with the very
 * client that already carries the token and refreshes it. A second HTTP stack with a copied header
 * would drift the moment the refresh rules change.
 */
fun taminImageLoader(
    context: PlatformContext,
    httpClient: HttpClient? = null,
): ImageLoader = ImageLoader.Builder(context)
    .apply {
        if (httpClient != null) {
            components {
                add(KtorNetworkFetcherFactory(httpClient = { httpClient }))
            }
        }
    }
    .memoryCache {
        MemoryCache.Builder()
            .maxSizePercent(context, MEMORY_CACHE_FRACTION)
            .build()
    }
    .diskCache {
        DiskCache.Builder()
            .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "tamin_image_cache")
            .maxSizePercent(DISK_CACHE_FRACTION)
            .build()
    }
    .crossfade(true)
    .build()
