package com.tamin.taminhamrah.plugin

import com.tamin.taminhamrah.plugin.model.PluginManifest
import com.tamin.taminhamrah.plugin.model.PluginState

/**
 * Core plugin interface that all plugin types must implement.
 */
interface Plugin {
    val manifest: PluginManifest
    val state: PluginState

    suspend fun initialize()
    suspend fun activate()
    suspend fun deactivate()
    suspend fun destroy()
}
