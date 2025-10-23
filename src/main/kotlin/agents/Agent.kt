package org.example.agents

import org.example.models.ChatMessage
import org.example.models.TaskInput
import org.example.models.TaskResult

interface Agent {

    val agentName: String
    val agentPrompt: ChatMessage

    suspend fun execute(taskInput: TaskInput): TaskResult
}