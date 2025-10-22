package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class ZAIRequest(
    val model: String,
    val messages: List<Message>,
    val max_tokens: Int = 4096,
    val temperature: Double = 1.0,
    val stream: Boolean = false
)