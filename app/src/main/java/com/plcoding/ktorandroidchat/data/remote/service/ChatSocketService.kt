package com.plcoding.ktorandroidchat.data.remote.service

import com.plcoding.ktorandroidchat.domain.model.Message
import com.plcoding.ktorandroidchat.util.Resource
import kotlinx.coroutines.flow.Flow


interface ChatSocketService {

    suspend fun initSession(
        username: String,
    ): Resource<Unit>


    suspend fun sendMessage(message: String)


    fun observeMessages(): Flow<Message>

    suspend fun closeSession()

    companion object{
        const val BASE_URL = "ws://192.168.1.3:8080"
    }

    sealed class Endpoints(val url: String){
        object chatSocketRoute: Endpoints("$BASE_URL/chat-socket")
    }

}