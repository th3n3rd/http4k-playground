package com.example.guessing.common.infra

import com.example.guessing.StartGuessTheSecretAppServer
import io.kotest.matchers.should
import org.http4k.client.JavaHttpClient
import org.http4k.core.Method.GET
import org.http4k.core.Method.OPTIONS
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.kotest.haveHeader
import org.http4k.kotest.haveStatus
import org.http4k.testing.Approver
import org.http4k.testing.JsonApprovalTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(JsonApprovalTest::class)
class OpenApiSpecTests {

    private val appServer = StartGuessTheSecretAppServer(TestEnvironment())
    private val client = SetBaseUriFrom(Uri.of("http://localhost:${appServer.port()}"))
        .then(JavaHttpClient())

    @Test
    fun `provides the open api specs`(approver: Approver) {
        approver.assertApproved(client(Request(GET, "/docs/openapi.json")))
    }

    @Test
    fun `cors are allowed from localhost only`() {
        corsShouldBeAllowedFrom("http://localhost")
        corsShouldBeAllowedFrom("http://localhost:9001")
        corsShouldBeAllowedFrom("https://localhost")
        corsShouldBeAllowedFrom("https://localhost:9001")
        corsShouldNotBeAllowedFrom("http://somwhere-else")
        corsShouldNotBeAllowedFrom("https://somwhere-else")
    }

    private fun corsShouldNotBeAllowedFrom(origin: String) {
        val response = client(Request(OPTIONS, "/docs/openapi.json")
            .header("Origin", origin))
        response should haveStatus(OK)
        response should haveHeader("access-control-allow-origin", "null")
    }

    private fun corsShouldBeAllowedFrom(origin: String) {
        val response = client(Request(OPTIONS, "/docs/openapi.json")
            .header("Origin", origin))
        response should haveStatus(OK)
        response should haveHeader("access-control-allow-origin", origin)
        response should haveHeader("access-control-allow-methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD")
        response should haveHeader("access-control-allow-credentials")
    }

}