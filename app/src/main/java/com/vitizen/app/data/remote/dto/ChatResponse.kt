package com.vitizen.app.data.remote.dto

data class ChatResponse(
    val output: Output,
    val metadata: Map<String, Any>
)

data class Output(
    val answer: String
) 