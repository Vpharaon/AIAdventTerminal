package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class TaskInput(
    val message: String
)