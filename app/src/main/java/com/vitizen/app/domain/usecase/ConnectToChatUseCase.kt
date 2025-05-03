package com.vitizen.app.domain.usecase

import com.vitizen.app.domain.repository.IChatRepository
import com.vitizen.app.data.remote.websocket.WebSocketMessage
import javax.inject.Inject

class ConnectToChatUseCase @Inject constructor(
    private val chatRepository: IChatRepository
) {
    operator fun invoke(
        onMessage: (WebSocketMessage) -> Unit,
        onError: (String) -> Unit
    ) {
        chatRepository.connect(onMessage, onError)
    }
}
