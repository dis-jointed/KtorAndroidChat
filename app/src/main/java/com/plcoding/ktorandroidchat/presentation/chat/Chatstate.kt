package com.plcoding.ktorandroidchat.presentation.chat

import com.plcoding.ktorandroidchat.domain.model.Message

data class Chatstate(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false
)