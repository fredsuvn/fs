package xyz.sunqian.common.net.http;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.io.IOKit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.time.Duration;

final class HttpClientImpl implements GekHttpClient {

    private final @Nullable Duration connectTimeout;
    private final @Nullable Duration readTimeout;
    private final int chunkSize;
    private final @Nullable Proxy proxy;

    HttpClientImpl(GekHttpClient.Builder builder) {
        this.connectTimeout = builder.getConnectTimeout();
        this.readTimeout = builder.getReadTimeout();
        this.chunkSize = builder.getChunkSize();
        this.proxy = builder.getProxy();
    }

    @Override
    public GekHttpResponse request(GekHttpRequest request) {
        try {
            return doRequest(request);
        } catch (GekHttpException e) {
            throw e;
        } catch (Exception e) {
            throw new GekHttpException(e);
        }
    }

    private GekHttpResponse doRequest(GekHttpRequest request) throws Exception {
        URL url = request.getUrl();
        if (url == null) {
            throw new GekHttpException("Null url.");
        }
        HttpURLConnection connection;
        if (proxy == null) {
            URLConnection urlConnection = url.openConnection();
            if (!(urlConnection instanceof HttpURLConnection)) {
                throw new GekHttpException("Not a http url: " + urlConnection);
            }
            connection = Jie.as(urlConnection);
        } else {
            URLConnection urlConnection = url.openConnection(proxy);
            if (!(urlConnection instanceof HttpURLConnection)) {
                throw new GekHttpException("Not a http url: " + urlConnection);
            }
            connection = Jie.as(urlConnection);
        }
        connection.setDoInput(true);
        connection.setRequestMethod(request.getMethod());
        GekHttpHeaders headers = request.getHeaders();
        if (headers != null) {
            headers.asMap().forEach((k, v) -> {
                for (String o : v) {
                    connection.setRequestProperty(k, String.valueOf(o));
                }
            });
        }
        if (connectTimeout != null) {
            connection.setConnectTimeout((int) connectTimeout.toMillis());
        }
        if (readTimeout != null) {
            connection.setReadTimeout((int) readTimeout.toMillis());
        }
        Object requestBody = request.getBody();
        if (requestBody != null) {
            InputStream bodyStream = prepareWriting(connection, requestBody);
            connection.setDoOutput(true);
            OutputStream out = connection.getOutputStream();
            IOKit.readTo(bodyStream, out);
            out.flush();
            out.close();
        }
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        GekHttpHeaders responseHeaders = GekHttpHeaders.from(connection.getHeaderFields());
        InputStream connectInput = connection.getInputStream();
        if (connectInput == null) {
            connection.disconnect();
        }
        InputStream responseBody = connectInput == null ? null : new InputWithConnection(connectInput, connection);
        return new GekHttpResponse() {

            @Override
            public int getStatusCode() {
                return responseCode;
            }

            @Override
            public String getStatusReason() {
                return responseMessage;
            }

            @Override
            public GekHttpHeaders getHeaders() {
                return responseHeaders;
            }

            @Override
            public @Nullable InputStream getBody() {
                return responseBody;
            }
        };
    }

    private InputStream prepareWriting(HttpURLConnection connection, Object requestBody) {
        if (requestBody instanceof InputStream) {
            connection.setChunkedStreamingMode(chunkSize);
            return (InputStream) requestBody;
        } else if (requestBody instanceof ByteBuffer) {
            connection.setFixedLengthStreamingMode(((ByteBuffer) requestBody).remaining());
            return IOKit.newInputStream((ByteBuffer) requestBody);
        } else {
            throw new GekHttpException("Invalid body type: " + requestBody.getClass());
        }
    }

    private static final class InputWithConnection extends InputStream {
        private final InputStream source;
        private final HttpURLConnection connection;

        private InputWithConnection(InputStream source, HttpURLConnection connection) {
            try {
                this.source = source;
            } catch (Exception e) {
                throw new GekHttpException(e);
            }
            this.connection = connection;
        }

        @Override
        public int read() throws IOException {
            return source.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return source.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return source.read(b, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            return source.skip(n);
        }

        @Override
        public int available() throws IOException {
            return source.available();
        }

        @Override
        public void close() throws IOException {
            source.close();
            connection.disconnect();
        }

        @Override
        public void mark(int readlimit) {
            source.mark(readlimit);
        }

        @Override
        public void reset() throws IOException {
            source.reset();
        }

        @Override
        public boolean markSupported() {
            return source.markSupported();
        }
    }
}
