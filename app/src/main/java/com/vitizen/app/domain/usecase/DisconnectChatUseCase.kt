package com.vitizen.app.domain.usecase

import com.vitizen.app.domain.repository.IChatRepository
import javax.inject.Inject

class DisconnectChatUseCase @Inject constructor(
    private val chatRepository: IChatRepository
) {
    operator fun invoke() {
        chatRepository.disconnect()
    }
}
