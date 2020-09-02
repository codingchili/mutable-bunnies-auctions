package com.codingchili.bunneh.api

import com.codingchili.core.protocol.ResponseStatus

class AuthenticationResponse: ClientAuthentication() {
    var status: ResponseStatus? = null
    var message: String? = null
}

/*
fun main(args: Array<String>) {
    val mapper = ObjectMapper()
    val response = mapper.readValue("{\n" +
            "      \"status\" : \"MISSING\",\n" +
            "      \"message\" : \"requested account is missing.\",\n" +
            "      \"target\" : \"client.authentication.node\",\n" +
            "      \"route\" : \"authenticate\"\n" +
            "    }", AuthenticationResponse::class.java)
    System.out.println("status = ${response.status} msg = ${response.message}")
}*/
