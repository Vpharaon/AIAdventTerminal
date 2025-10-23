package org.example.agents

import org.example.client.AiClient
import org.example.models.ChatMessage
import org.example.models.ChatRequest
import org.example.models.TaskInput
import org.example.models.TaskResult

open class BaseAgent(
    private val aiClient: AiClient,
    override val agentName: String,
    override val agentPrompt: ChatMessage
): Agent {
    override suspend fun execute(taskInput: TaskInput): TaskResult {
        println("=".repeat(80))
        println("$agentName начал работу")

        val chatRequest = ChatRequest(
            messages = listOf(
                agentPrompt,
                ChatMessage(
                    role = "user", content = taskInput.message
                )
            )
        )

        val response = aiClient.sendChatRequest(chatRequest)

        println("$agentName закончил работу")
        println("")

        return TaskResult(
            result = response.choices?.firstOrNull()?.message?.content
        )
    }
}