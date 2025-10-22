package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ResponseMessage(
    val role: String,
    val content: String,
)

@Serializable
data class Choice(
    val index: Int,
    val message: ResponseMessage,
    val finish_reason: String? = null
)

@Serializable
data class PromptTokensDetails(
    val cached_tokens: Int? = null
)

@Serializable
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int,
    val prompt_tokens_details: PromptTokensDetails? = null
)

@Serializable
data class ZAIResponse(
    val id: String? = null,
    val `object`: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<Choice>? = null,
    val request_id: String? = null,
    val usage: Usage? = null,
    val error: String? = null
)