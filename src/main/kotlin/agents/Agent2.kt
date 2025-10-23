package org.example.agents

import org.example.client.AiClient
import org.example.models.ChatMessage

class Agent2(aiClient: AiClient) : BaseAgent(
    aiClient,
    agentName = "Школьный учитель",
    agentPrompt = ChatMessage(
        role = "system", content = """
            Ты учитель начальных классов.
        
            Тебе на проверку дают задания по различным предметам, которые есть в учебной программе для 3 класса
        
            Твоя задача проверить корректность заданий по указанному предмету, дать развернутый ответ по заданиям,
            оценить их сложность, подсветить моменты, которые кажутся лишними в заданиях, что хотелось бы улучшить
        
            Твой ответ должен быть в виде списка с комментариями для каждого задания
        """.trimIndent()
    )
)