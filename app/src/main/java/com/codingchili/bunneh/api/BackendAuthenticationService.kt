package com.codingchili.bunneh.api

import com.codingchili.bunneh.model.single
import com.codingchili.core.protocol.ResponseStatus
import com.codingchili.core.security.Account
import com.codingchili.core.security.Token
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.rxjava3.core.Single
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.CompletableFuture

class BackendAuthenticationService : AuthenticationService {
    private var authentication: AuthenticationResponse? = null
    private val json: MediaType = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()
    private val mapper = ObjectMapper()

    init {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    override fun authenticate(username: String, password: String): Single<AuthenticationResponse> {
        val account = Account(username, password)
        val body = mapper.writeValueAsString(AuthenticationRequest(account)).toRequestBody(json)

        val request = Request.Builder()
            .url("${Connector.protocol}${Connector.server}:${Connector.api_port}/")
            .post(body)
            .build()

        val future = CompletableFuture<AuthenticationResponse>()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                future.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        authentication = mapper.readValue(
                            response.body?.string(),
                            AuthenticationResponse::class.java
                        )
                        if (ResponseStatus.ACCEPTED == authentication!!.status) {
                            future.complete(authentication)
                        } else {
                            future.completeExceptionally(Exception(authentication!!.message))
                        }
                    } else {
                        future.completeExceptionally(Exception("Authentication failure."))
                    }
                }
            }
        })
        return single(future)
    }

    override fun token(): Token? {
        return authentication?.token
    }

    override fun logout() {
        authentication = null
    }

    override fun user(): String? {
        return authentication?.account?.username
    }

}