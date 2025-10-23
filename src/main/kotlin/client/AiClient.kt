package org.example.client

import org.example.models.ChatRequest
import org.example.models.ChatResponse

interface AiClient {
    suspend fun sendChatRequest(chatRequest: ChatRequest): ChatResponse
}