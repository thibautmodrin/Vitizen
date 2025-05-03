package com.vitizen.app.domain.usecase

import com.vitizen.app.domain.repository.IChatRepository
import org.json.JSONObject
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(
    private val chatRepository: IChatRepository
) {
    operator fun invoke(message: String) {
        if (message.isBlank()) return
        val formatted = JSONObject().apply {
            put("message", message.trim())
        }.toString()
        chatRepository.sendMessage(formatted)
    }
}
