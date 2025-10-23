package org.example.agents

import org.example.client.AiClient
import org.example.models.ChatMessage

class Agent1(aiClient: AiClient) : BaseAgent(
    aiClient,
    agentName = "Составитель заданий",
    agentPrompt = ChatMessage(
        role = "system", content = """
            Ты составитель примеров. Твоя задача подготовить 10 заданий по предмету, который тебе скажут для 3-го класса.
            ты не должен их проверять. 
            Твоим ответом должны быть список из 10 заданий по указанному предмету
        """.trimIndent()
    )
)