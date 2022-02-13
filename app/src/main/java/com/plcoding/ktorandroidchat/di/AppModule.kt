package com.plcoding.ktorandroidchat.di

import com.plcoding.ktorandroidchat.data.MessageServiceImpl
import com.plcoding.ktorandroidchat.data.interf.MessageService
import com.plcoding.ktorandroidchat.data.remote.chatSocketServiceImpl
import com.plcoding.ktorandroidchat.data.remote.service.ChatSocketService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient{
        return HttpClient(CIO){
            install(Logging)
            install(WebSockets)
            install(JsonFeature){
                serializer = KotlinxSerializer()
            }
        }
    }


    @Provides
    @Singleton
    fun provideMessageService(client: HttpClient) : MessageService{
        return MessageServiceImpl(client = client)
    }

    @Provides
    @Singleton
    fun chatSocketService(client: HttpClient) : ChatSocketService{
        return chatSocketServiceImpl(client = client)
    }

}