package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class TaskResult(
    val result: String?
)