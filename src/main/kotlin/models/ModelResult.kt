package org.example.models

data class ModelResult(
    val modelName: String,
    val response: String,
    val responseTimeMs: Long,
    val inputTokens: Int,
    val outputTokens: Int,
    val totalTokens: Int,
    val estimatedCost: Double?,
    val error: String? = null
)