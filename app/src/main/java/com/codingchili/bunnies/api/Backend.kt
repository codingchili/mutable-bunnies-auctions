package com.codingchili.bunnies.api

import android.util.Log
import com.codingchili.bunnies.api.protocol.ServerResponse
import com.codingchili.core.protocol.ResponseStatus
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

/**
 * Handles connection with the backend. This is done over REST for now, later the auction
 * service may be switched to using websockets instead. This is to support server push
 * for notifications and observing changes to auctions. Currently this requires polling.
 */
class Backend {
    companion object {
        private val json: MediaType = "application/json; charset=utf-8".toMediaType()
        private val client = OkHttpClient()
        private val mapper = ObjectMapper()

        init {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            mapper.registerModule(KotlinModule())
        }

        /**
         * Performs a REST request to the server set in the connector.
         *
         * @param body the request body to serialize as json and send to the server.
         * @param target the type to which the response is deserialized and returned.
         * @return a future that either completes with an instance of the deserialized
         * response or an error should one occur during the request. The future will
         * also complete exceptionally if the service responds with an error code.
         * Anything else than HTTP 200/OK and ResponseStatus.ACCEPTED.
         */
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
                            val reply = mapper.readValue(response.body?.string(), target) as T
                            Log.w("foo", mapper.writeValueAsString(reply))

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