package com.example.common.infra

import com.example.StartGuessTheSecretAppServer
import org.http4k.client.JavaHttpClient
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.testing.Approver
import org.http4k.testing.JsonApprovalTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(JsonApprovalTest::class)
class OpenApiSpecTests {

    private val appServer = StartGuessTheSecretAppServer(TestEnvironment())
    private val client = ClientFilters
        .SetBaseUriFrom(Uri.of("http://localhost:${appServer.port()}"))
        .then(JavaHttpClient())

    @Test
    fun `provides the open api specs`(approver: Approver) {
        approver.assertApproved(client(Request(GET, "/docs/openapi.json")))
    }
}