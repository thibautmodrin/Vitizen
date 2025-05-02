package com.vitizen.app.data.remote.dto

data class ChatRequest(
    val input: Map<String, String>,
    val stream: Boolean = false,
    val fn_index: Int = 0
) 