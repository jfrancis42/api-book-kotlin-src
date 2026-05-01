package ch05;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.Objects;

public class GitHubClientJava implements AutoCloseable {

    private static final String DEFAULT_BASE_URL =
        "https://api.github.com";

    private final String baseUrl;
    private final OkHttpClient http;

    public GitHubClientJava() {
        this(DEFAULT_BASE_URL);
    }

    public GitHubClientJava(String baseUrl) {
        this.baseUrl = baseUrl;
        this.http = new OkHttpClient.Builder().build();
    }

    public String getZen() throws IOException {
        Request request = new Request.Builder()
            .url(baseUrl + "/zen")
            .header(
                "Accept",
                "application/vnd.github.v3+json"
            )
            .header(
                "User-Agent",
                "suspend-disbelief-book/1.0"
            )
            .build();

        try (Response response = http.newCall(request)
                .execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Empty response body");
            }
            return body.string();
        }
    }

    public String getUser(String username)
            throws IOException {
        Request request = new Request.Builder()
            .url(baseUrl + "/users/" + username)
            .header(
                "Accept",
                "application/vnd.github.v3+json"
            )
            .header(
                "User-Agent",
                "suspend-disbelief-book/1.0"
            )
            .build();

        try (Response response = http.newCall(request)
                .execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Empty response body");
            }
            return body.string();
        }
    }

    public String getRepo(String owner, String repo)
            throws IOException {
        Request request = new Request.Builder()
            .url(baseUrl + "/repos/" + owner + "/" + repo)
            .header(
                "Accept",
                "application/vnd.github.v3+json"
            )
            .header(
                "User-Agent",
                "suspend-disbelief-book/1.0"
            )
            .build();

        try (Response response = http.newCall(request)
                .execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Empty response body");
            }
            return body.string();
        }
    }

    @Override
    public void close() {
        http.dispatcher().executorService().shutdown();
        http.connectionPool().evictAll();
        http.cache();
    }
}
