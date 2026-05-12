package com.tamin.taminhamrah.plugin

import com.tamin.taminhamrah.plugin.model.PluginType
import kotlinx.coroutines.flow.Flow

/**
 * Registry for managing plugin lifecycle and discovery.
 */
interface PluginRegistry {
    fun register(plugin: Plugin): Result<Unit>
    fun unregister(pluginId: String): Result<Unit>
    fun getPlugin(pluginId: String): Plugin?
    fun getPluginsByType(type: PluginType): List<Plugin>
    fun observePlugins(): Flow<List<Plugin>>
    fun observePluginsByType(type: PluginType): Flow<List<Plugin>>
}
