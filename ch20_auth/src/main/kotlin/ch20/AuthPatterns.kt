package ch20

import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermissions
import java.util.Base64

fun basicAuthHeader(
    username: String,
    password: String
): String {
    val credentials = "$username:$password"
    val encoded = Base64.getEncoder()
        .encodeToString(
            credentials.toByteArray(Charsets.UTF_8)
        )
    return "Basic $encoded"
}

fun maskApiKey(url: String): String {
    return url.replace(
        Regex("""([?&]api_key=)[^&]+"""),
        "$1***"
    )
}

fun loadEnvFile(path: String): Map<String, String> {
    val file = File(path)
    if (!file.exists()) return emptyMap()
    return file.readLines()
        .filter { it.isNotBlank() && !it.startsWith("#") }
        .mapNotNull { line ->
            val idx = line.indexOf('=')
            if (idx < 0) return@mapNotNull null
            val key = line.substring(0, idx).trim()
            val value = line.substring(idx + 1).trim()
                .removeSurrounding("\"")
                .removeSurrounding("'")
            key to value
        }
        .toMap()
}

fun writeSecureConfigFile(
    path: String,
    content: String
) {
    val file = File(path)
    file.writeText(content)
    try {
        Files.setPosixFilePermissions(
            file.toPath(),
            PosixFilePermissions.fromString("rw-------")
        )
    } catch (_: UnsupportedOperationException) {
        // Non-POSIX filesystem (Windows)
    }
}
