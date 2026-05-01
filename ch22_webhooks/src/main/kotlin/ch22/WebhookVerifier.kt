package ch22

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object WebhookVerifier {
    fun verifySignature(
        payload: ByteArray,
        signature: String,
        secret: String
    ): Boolean {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(
            SecretKeySpec(
                secret.toByteArray(Charsets.UTF_8),
                "HmacSHA256"
            )
        )
        val computed = mac.doFinal(payload)
        val expected = "sha256=" +
            computed.joinToString("") {
                "%02x".format(it)
            }
        return MessageDigest.isEqual(
            expected.toByteArray(Charsets.UTF_8),
            signature.toByteArray(Charsets.UTF_8)
        )
    }
}
