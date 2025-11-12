package space.sunqian.common.net.http;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.Fs;
import space.sunqian.common.io.IOKit;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

enum HttpCallerServiceImplByJ11 implements HttpCallerService {
    INST;

    @Override
    public @Nonnull HttpCaller newCaller(int bufSize, @Nonnull Proxy proxy) throws HttpNetException {
        return new CallerImpl(proxy);
    }

    private static final class CallerImpl implements HttpCaller {

        private final @Nonnull HttpClient httpClient;

        CallerImpl(@Nonnull Proxy proxy) throws HttpNetException {
            this.httpClient = Fs.uncheck(() ->
                    HttpClient.newBuilder().proxy(ProxySelector.of((InetSocketAddress) proxy.address())).build(),
                HttpNetException::new);
        }

        @Override
        public @Nonnull HttpResp request(@Nonnull HttpReq req) throws HttpNetException {
            return Fs.uncheck(() -> request0(req), HttpNetException::new);
        }

        @SuppressWarnings("resource")
        private @Nonnull HttpResp request0(@Nonnull HttpReq req) throws Exception {
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
                    return Fs.nonnull(response.body(), IOKit.emptyInputStream());
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
