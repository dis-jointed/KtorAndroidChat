package com.plcoding.ktorandroidchat.presentation.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.ktorandroidchat.data.interf.MessageService
import com.plcoding.ktorandroidchat.data.remote.service.ChatSocketService
import com.plcoding.ktorandroidchat.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageService: MessageService,
    private val chatSocket: ChatSocketService,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private  val _state = mutableStateOf(Chatstate())
    val state: State<Chatstate> = _state

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()


    fun connectToChat() {
        getAllMessages()
        savedStateHandle.get<String>("username")?.let {
            username ->
            viewModelScope.launch {
                val result = chatSocket.initSession(username = username)

                when(result) {
                    is Resource.Success ->{

                        chatSocket.observeMessages()
                            .onEach {
                                message ->
                                val newList = state.value.messages.toMutableList().apply {
                                    add(0, message)
                                }
                                _state.value = state.value.copy(
                                    messages = newList
                                )

                            }.launchIn(viewModelScope)

                    }

                    is Resource.Error -> {
                        _toastEvent.emit(result.message ?: "Unknown error")
                    }
                }

            }


        }
    }

    fun onMessageChange(message: String){
        _messageText.value = message
    }

    fun disconnect(){
        viewModelScope.launch {
            chatSocket.closeSession()
        }
    }

    fun getAllMessages(){
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)

            val result = messageService.getAllMessages()
            _state.value = state.value.copy(
                messages = result,
                isLoading = false
            )
        }
    }

    fun sendMessage(){
        viewModelScope.launch {

            if (messageText.value.isNotBlank()) {
                chatSocket.sendMessage(messageText.value)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

}