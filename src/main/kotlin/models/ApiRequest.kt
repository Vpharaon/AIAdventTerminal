package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class HuggingFaceRequest(
    val messages: List<Message>,
    val model: String,
    val stream: Boolean = false
)