package space.sunqian.common.net.http;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.Kit;
import space.sunqian.common.io.IOKit;
import space.sunqian.common.net.NetException;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

enum HttpClientEngineServiceImplByJ11 implements HttpClientEngineService {
    INST;

    @Override
    public @Nonnull HttpClientEngine newEngine(int bufSize) throws IllegalArgumentException {
        return new EngineImpl();
    }

    private static final class EngineImpl implements HttpClientEngine {

        private final @Nonnull HttpClient client;

        EngineImpl() throws IllegalArgumentException {
            this.client = HttpClient.newHttpClient();
        }

        @Override
        public @Nonnull HttpResp request(@Nonnull HttpReq req, @Nullable Proxy proxy) throws NetException {
            return Kit.uncheck(() -> request0(req, proxy), NetException::new);
        }

        private @Nonnull HttpClient getClient(@Nullable Proxy proxy) {
            if (proxy == null || Objects.equals(proxy, Proxy.NO_PROXY)) {
                return HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            }
            return HttpClient.newBuilder()
                .proxy(new ProxySelector() {
                    @Override
                    public List<Proxy> select(URI uri) {
                        return Collections.singletonList(proxy);
                    }

                    @Override
                    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                        throw new NetException("Failed to proxy: " + uri + "[proxied by " + sa + "]", ioe);
                    }
                })
                .build();
        }

        @SuppressWarnings("resource")
        private @Nonnull HttpResp request0(@Nonnull HttpReq req, @Nullable Proxy proxy) throws Exception {
            HttpClient httpClient = getClient(proxy);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(req.url().toURI())
                .headers(
                    req.headers().entrySet().stream().flatMap(entry -> {
                            List<String> values = entry.getValue();
                            String[] kvs = new String[values.size() * 2];
                            int i = 0;
                            for (String value : values) {
                                kvs[i++] = entry.getKey();
                                kvs[i++] = value;
                            }
                            return Arrays.stream(kvs);
                        }
                    ).toArray(String[]::new)
                )
                .method(req.method(), req.body() == null ?
                    HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofInputStream(req::body))
                .timeout(req.timeout())
                .build();
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            return new HttpResp() {

                @Override
                public @Nonnull String protocolVersion() {
                    return findString(response.version(), 0);
                }

                @Override
                public @Nonnull String statusCode() {
                    return Integer.toString(response.statusCode());
                }

                @Override
                public @Nonnull String statusText() {
                    return findString(response.statusCode(), 4);
                }

                @Override
                public @Nonnull Map<String, List<String>> headers() {
                    return response.headers().map();
                }

                @Override
                public @Nonnull InputStream body() {
                    return Kit.nonnull(response.body(), IOKit.emptyInputStream());
                }

                @Override
                public @Nullable String contentType() {
                    return response.headers().firstValue("Content-Type").orElse(null);
                }
            };
        }

        private static final @Nonnull Object @Nonnull [] TABLE = {
            HttpClient.Version.HTTP_1_1, "HTTP/1.1",
            HttpClient.Version.HTTP_2, "HTTP/2",
            200, "OK",
            201, "Created",
            204, "No Content",
            301, "Moved Permanently",
            302, "Found",
            304, "Not Modified",
            400, "Bad Request",
            401, "Unauthorized",
            403, "Forbidden",
            404, "Not Found",
            405, "Method Not Allowed",
            500, "Internal Server Error",
            502, "Bad Gateway",
            503, "Service Unavailable",
            504, "Gateway Timeout"
        };

        private String findString(@Nonnull Object key, int start) {
            for (int i = start; i < TABLE.length; i++) {
                if (Objects.equals(TABLE[i], key)) {
                    return TABLE[i + 1].toString();
                }
            }
            return "Unknown";
        }
    }
}
