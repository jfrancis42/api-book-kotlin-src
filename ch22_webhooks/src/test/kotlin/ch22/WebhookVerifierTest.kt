package ch22

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WebhookVerifierTest {
    private val secret = "webhook-secret"
    private val payload = """{"action":"push"}""".toByteArray()

    private fun computeSig(
        payload: ByteArray,
        secret: String
    ): String {
        val mac = javax.crypto.Mac.getInstance("HmacSHA256")
        mac.init(
            javax.crypto.spec.SecretKeySpec(
                secret.toByteArray(Charsets.UTF_8),
                "HmacSHA256"
            )
        )
        return "sha256=" + mac.doFinal(payload)
            .joinToString("") { "%02x".format(it) }
    }

    @Test
    fun `valid signature returns true`() {
        val sig = computeSig(payload, secret)
        assertTrue(
            WebhookVerifier.verifySignature(
                payload, sig, secret
            )
        )
    }

    @Test
    fun `wrong secret returns false`() {
        val sig = computeSig(payload, "wrong-secret")
        assertFalse(
            WebhookVerifier.verifySignature(
                payload, sig, secret
            )
        )
    }

    @Test
    fun `tampered payload returns false`() {
        val sig = computeSig(payload, secret)
        val tampered = """{"action":"delete"}""".toByteArray()
        assertFalse(
            WebhookVerifier.verifySignature(
                tampered, sig, secret
            )
        )
    }

    @Test
    fun `empty payload with valid sig succeeds`() {
        val empty = ByteArray(0)
        val sig = computeSig(empty, secret)
        assertTrue(
            WebhookVerifier.verifySignature(
                empty, sig, secret
            )
        )
    }
}
