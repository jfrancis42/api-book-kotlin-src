package ch21

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OAuthClientTest {
    private lateinit var server: MockWebServer
    private lateinit var client: OAuthClient

    @BeforeTest
    fun setup() {
        server = MockWebServer()
        server.start()
        client = OAuthClient(
            clientId = "test-client-id",
            oauthBaseUrl = server.url("").toString()
                .trimEnd('/')
        )
    }

    @AfterTest
    fun teardown() {
        client.close()
        server.shutdown()
    }

    @Test
    fun `requestDeviceCode returns device code`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .addHeader(
                        "Content-Type",
                        "application/json"
                    )
                    .setBody(
                        """{
                          "device_code":"dev123",
                          "user_code":"ABCD-1234",
                          "verification_uri":
                            "https://github.com/login/device",
                          "expires_in":900,
                          "interval":5
                        }"""
                    )
            )

            val result = client.requestDeviceCode()

            assertTrue(result.isSuccess)
            assertEquals(
                "ABCD-1234",
                result.getOrThrow().userCode
            )
        }

    @Test
    fun `pollForToken returns Success on token`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .addHeader(
                        "Content-Type",
                        "application/json"
                    )
                    .setBody(
                        """{
                          "access_token":"gho_abc123",
                          "token_type":"bearer",
                          "scope":"repo"
                        }"""
                    )
            )

            val result = client.pollForToken("dev123")

            assertTrue(result is DevicePollResult.Success)
            assertEquals(
                "gho_abc123",
                (result as DevicePollResult.Success)
                    .token.accessToken
            )
        }

    @Test
    fun `pollForToken returns Pending`() = runTest {
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody(
                    """{
                      "error":"authorization_pending",
                      "error_description":"Still waiting"
                    }"""
                )
        )

        val result = client.pollForToken("dev123")

        assertTrue(result is DevicePollResult.Pending)
    }

    @Test
    fun `pollForToken returns Failed on expired`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .addHeader(
                        "Content-Type",
                        "application/json"
                    )
                    .setBody(
                        """{
                          "error":"expired_token",
                          "error_description":"Expired"
                        }"""
                    )
            )

            val result = client.pollForToken("dev123")

            assertTrue(result is DevicePollResult.Failed)
        }

    @Test
    fun `pollForToken returns Failed on denied`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .addHeader(
                        "Content-Type",
                        "application/json"
                    )
                    .setBody(
                        """{
                          "error":"access_denied",
                          "error_description":"Denied"
                        }"""
                    )
            )

            val result = client.pollForToken("dev123")

            assertTrue(result is DevicePollResult.Failed)
            assertEquals(
                "User denied access",
                (result as DevicePollResult.Failed).reason
            )
        }
}
