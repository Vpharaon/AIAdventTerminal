package org.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val id: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<Choice>? = null,
    val usage: Usage? = null
)

@Serializable
data class Choice(
    val index: Int? = null,
    val message: ChatMessage? = null,
    val finishReason: String? = null
)

@Serializable
data class Usage(
    val promptTokens: Int? = null,
    val completionTokens: Int? = null,
    val totalTokens: Int? = null
)

@Serializable
data class Message(
    val content: String? = null,
    @SerialName("reasoning_content")
    val reasoningContent: String? = null,
    val role: String? = null
)