package org.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val model: String = "glm-4.5-flash",
    val messages: List<ChatMessage>,
    @SerialName("max_tokens")
    val maxTokens: Int = 4096,
    val temperature: Double = 0.0
)