package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ResponseMessage(
    val role: String,
    val content: String
)

@Serializable
data class Choice(
    val index: Int,
    val message: ResponseMessage,
    val finish_reason: String? = null
)

@Serializable
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

@Serializable
data class HuggingFaceResponse(
    val id: String? = null,
    val `object`: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<Choice>? = null,
    val system_fingerprint: String? = null,
    val usage: Usage? = null,
    val error: String? = null
)