package space.sunqian.common.net.http;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.Fs;
import space.sunqian.common.base.math.MathKit;
import space.sunqian.common.io.IOOperator;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.util.List;
import java.util.Map;

enum HttpCallerServiceImpl implements HttpCallerService {
    INST;

    @Override
    public @Nonnull HttpCaller newCaller(int bufSize, @Nonnull Proxy proxy) throws HttpNetException {
        return new CallerImpl(bufSize, proxy);
    }

    private static final class CallerImpl implements HttpCaller {

        private final @Nonnull IOOperator io;
        private final @Nonnull Proxy proxy;

        CallerImpl(int bufSize, @Nonnull Proxy proxy) throws HttpNetException {
            this.io = Fs.uncheck(() -> IOOperator.get(bufSize), HttpNetException::new);
            this.proxy = proxy;
        }

        @Override
        public @Nonnull HttpResp request(@Nonnull HttpReq req) throws HttpNetException {
            return Fs.uncheck(() -> request0(req), HttpNetException::new);
        }

        private @Nonnull HttpResp request0(@Nonnull HttpReq req) throws Exception {
            HttpURLConnection connection = (HttpURLConnection) req.url().openConnection(proxy);
            connection.setConnectTimeout(MathKit.intValue(req.timeout().toMillis()));
            connection.setReadTimeout(MathKit.intValue(req.timeout().toMillis()));
            req.headers().forEach((k, list) -> {
                for (String s : list) {
                    connection.addRequestProperty(k, s);
                }
            });
            // connection.setDoInput(true);
            connection.setRequestMethod(req.method());
            HttpReq.Body body = req.body();
            if (body != null) {
                InputStream in = body.toInputStream();
                int firstByte = in.read();
                if (firstByte != -1) {
                    connection.setDoOutput(true);
                    OutputStream out = connection.getOutputStream();
                    out.write(firstByte);
                    io.readTo(in, out);
                }
            }
            int respCode = connection.getResponseCode();
            String respMsg = connection.getResponseMessage();
            String firstLine = Fs.nonnull(connection.getHeaderField(null), "");
            String protocol = firstLine.substring(0, Math.max(firstLine.indexOf(' '), 0));
            Map<String, List<String>> respHeaders = connection.getHeaderFields();
            String respContentType = connection.getContentType();
            InputStream errBody = connection.getErrorStream();
            InputStream respBody = errBody != null ? errBody : connection.getInputStream();
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
}
