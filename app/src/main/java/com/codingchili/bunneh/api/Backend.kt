package com.codingchili.bunneh.api

import com.codingchili.bunneh.api.protocol.ServerResponse
import com.codingchili.core.protocol.ResponseStatus
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

class Backend {
    companion object {
        private val json: MediaType = "application/json; charset=utf-8".toMediaType()
        private val client = OkHttpClient() // should be singleton.
        private val mapper = ObjectMapper()

        init {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }

        fun <T : ServerResponse> request(body: Any, target: Class<T>): Future<T> {
            val request = buildRequest(body);
            val future = CompletableFuture<T>()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    future.completeExceptionally(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (response.isSuccessful) {
                            val reply =
                                mapper.readValue(response.body?.string(), target) as T

                            if (ResponseStatus.ACCEPTED == reply.status) {
                                future.complete(reply)
                            } else {
                                future.completeExceptionally(Exception(reply.message))
                            }
                        } else {
                            future.completeExceptionally(Exception("Call to server failed."))
                        }
                    }
                }
            })
            return future
        }

        private fun buildRequest(message: Any): Request {
            val body = mapper.writeValueAsString(message).toRequestBody(json)
            return Request.Builder()
                .url(getUrl())
                .post(body)
                .build()
        }

        private fun getUrl(): String {
            return "${Connector.protocol}${Connector.server}:${Connector.api_port}/"
        }
    }
}