package com.example.uithread.data.network

import com.example.uithread.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response

}