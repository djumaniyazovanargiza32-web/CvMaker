package com.nargiza.cvmaker.backend

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.options
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ApplicationTest {

    @Test
    fun testRootEndpoint() = testApplication {
        application {
            module()
        }

        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("AI CV Maker Backend is online.", response.bodyAsText())
    }

    @Test
    fun testHealthEndpoint() = testApplication {
        application {
            module()
        }

        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"status\": \"OK\"") || body.contains("\"status\":\"OK\""))
        assertTrue(body.contains("groqConfigured"))
        assertTrue(body.contains("model"))
    }

    @Test
    fun testCorsHeadersOnOptions() = testApplication {
        application {
            module()
        }

        val response = client.options("/api/v1/ai/summary") {
            header(HttpHeaders.Origin, "http://localhost:3000")
            header(HttpHeaders.AccessControlRequestMethod, "POST")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertNotNull(response.headers[HttpHeaders.AccessControlAllowOrigin])
    }

    @Test
    fun testSummaryEndpointRejectsEmptyBody() = testApplication {
        application {
            module()
        }

        val response = client.post("/api/v1/ai/summary") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Job title is required"))
    }

    @Test
    fun testExperienceEndpointRejectsEmptyBody() = testApplication {
        application {
            module()
        }

        val response = client.post("/api/v1/ai/experience") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Position and company are required"))
    }

    @Test
    fun testAchievementsEndpointRejectsEmptyBody() = testApplication {
        application {
            module()
        }

        val response = client.post("/api/v1/ai/achievements") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Responsibilities are required"))
    }

    @Test
    fun testJobAnalysisEndpointRejectsEmptyBody() = testApplication {
        application {
            module()
        }

        val response = client.post("/api/v1/ai/job-analysis") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Job description is required"))
    }
}
