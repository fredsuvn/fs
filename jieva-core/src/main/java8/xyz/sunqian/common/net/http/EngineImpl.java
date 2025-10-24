package xyz.sunqian.common.net.http;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Kit;
import xyz.sunqian.common.base.math.MathKit;
import xyz.sunqian.common.io.IOOperator;
import xyz.sunqian.common.net.NetException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.Map;

final class EngineImpl implements HttpClientEngine {

    private final @Nonnull IOOperator io;

    EngineImpl(int bufSize) throws IllegalArgumentException {
        this.io = IOOperator.get(bufSize);
    }

    @Override
    public @Nonnull HttpResp request(
        @Nonnull HttpReq req,
        @Nonnull Duration connectTimeout,
        @Nonnull Duration readTimeout,
        @Nonnull Proxy proxy
    ) throws NetException {
        return Kit.uncheck(() -> request0(req, connectTimeout, readTimeout, proxy), NetException::new);
    }

    private @Nonnull HttpResp request0(
        @Nonnull HttpReq req,
        @Nonnull Duration connectTimeout,
        @Nonnull Duration readTimeout,
        @Nonnull Proxy proxy
    ) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) req.url().openConnection(proxy);
        connection.setRequestMethod(req.method());
        connection.setConnectTimeout(MathKit.intValue(connectTimeout.toMillis()));
        connection.setReadTimeout(MathKit.intValue(readTimeout.toMillis()));
        req.headers().forEach((k, list) -> {
            for (String s : list) {
                connection.addRequestProperty(k, s);
            }
        });
        connection.setDoInput(true);
        InputStream in = req.body();
        int firstByte = in.read();
        if (firstByte != -1) {
            connection.setDoOutput(true);
            OutputStream out = connection.getOutputStream();
            out.write(firstByte);
            io.readTo(in, out);
        }
        int respCode = connection.getResponseCode();
        String respMsg = connection.getResponseMessage();
        String firstLine = Kit.nonnull(connection.getHeaderField(null), "");
        String protocol = firstLine.substring(0, Math.max(firstLine.indexOf(' '), 0));
        Map<String, List<String>> respHeaders = connection.getHeaderFields();
        String respContentType = connection.getContentType();
        InputStream respBody = connection.getInputStream();
        return new HttpResp() {

            @Override
            public @Nonnull String protocolVersion() {
                return protocol;
            }

            @Override
            public @Nonnull String statusCode() {
                return Integer.toString(respCode);
            }

            @Override
            public @Nonnull String statusText() {
                return respMsg;
            }

            @Override
            public @Nonnull Map<String, List<String>> headers() {
                return respHeaders;
            }

            @Override
            public @Nonnull InputStream body() {
                return respBody;
            }

            @Override
            public @Nullable String contentType() {
                return respContentType;
            }
        };
    }
}
