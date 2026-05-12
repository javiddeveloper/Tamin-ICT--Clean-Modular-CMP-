package com.tamin.taminhamrah.plugin

import com.tamin.taminhamrah.plugin.capability.WorkflowCapability

/**
 * Specialized plugin interface for workflow engine integrations
 * (e.g. ComfyUI, ExternalServer).
 */
interface WorkflowEnginePlugin : Plugin {
    val capabilities: Set<WorkflowCapability>
    val isConnected: Boolean
    suspend fun connect(): Result<Unit>
    suspend fun disconnect()
    suspend fun getStatus(): WorkflowEngineStatus
}

data class WorkflowEngineStatus(
    val isConnected: Boolean,
    val serverName: String,
    val serverUrl: String,
    val capabilities: Set<WorkflowCapability>,
)
