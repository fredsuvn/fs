package xyz.sunqian.common.net.http;

import xyz.sunqian.common.base.chars.JieChars;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Http utilities, based on {@link GekHttpClient#defaultClient()}.
 *
 * @author fredsuvn
 */
public class GekHttp {

    /**
     * Requests with given request info.
     *
     * @param request request info
     * @return response
     */
    public static GekHttpResponse request(GekHttpRequest request) {
        return GekHttpClient.defaultClient().request(request);
    }

    /**
     * Requests given url and headers with GET method.
     *
     * @param url     given url
     * @param headers headers
     * @return response
     */
    public static GekHttpResponse get(String url, GekHttpHeaders headers) {
        return GekHttpClient.defaultClient().request(
            GekHttpRequest.newBuilder()
                .url(url)
                .method("GET")
                .headers(headers)
                .build()
        );
    }

    /**
     * Requests given url and query string with GET method.
     *
     * @param url         given url
     * @param queryString given query string
     * @return response
     */
    public static GekHttpResponse get(String url, Map<String, String> queryString) {
        return GekHttpClient.defaultClient().request(
            GekHttpRequest.newBuilder()
                .url(url, queryString)
                .method("GET")
                .build()
        );
    }

    /**
     * Requests given url, headers and query string with GET method.
     *
     * @param url         given url
     * @param headers     headers
     * @param queryString given query string
     * @return response
     */
    public static GekHttpResponse get(String url, GekHttpHeaders headers, Map<String, String> queryString) {
        return GekHttpClient.defaultClient().request(
            GekHttpRequest.newBuilder()
                .url(url, queryString)
                .method("GET")
                .headers(headers)
                .build()
        );
    }

    /**
     * Requests given url and headers with POST method.
     *
     * @param url     given url
     * @param headers headers
     * @return response
     */
    public static GekHttpResponse post(String url, GekHttpHeaders headers) {
        return GekHttpClient.defaultClient().request(
            GekHttpRequest.newBuilder()
                .url(url)
                .method("POST")
                .headers(headers)
                .build()
        );
    }

    /**
     * Requests given url and body with GET method.
     *
     * @param url  given url
     * @param body given body
     * @return response
     */
    public static GekHttpResponse post(String url, InputStream body) {
        return GekHttpClient.defaultClient().request(
            GekHttpRequest.newBuilder()
                .url(url)
                .method("POST")
                .body(body)
                .build()
        );
    }

    /**
     * Requests given url and body with GET method.
     *
     * @param url  given url
     * @param body given body
     * @return response
     */
    public static GekHttpResponse post(String url, ByteBuffer body) {
        return GekHttpClient.defaultClient().request(
            GekHttpRequest.newBuilder()
                .url(url)
                .method("POST")
                .body(body)
                .build()
        );
    }

    /**
     * Requests given url, headers and body with GET method.
     *
     * @param url     given url
     * @param headers headers
     * @param body    given body
     * @return response
     */
    public static GekHttpResponse post(String url, GekHttpHeaders headers, InputStream body) {
        return GekHttpClient.defaultClient().request(
            GekHttpRequest.newBuilder()
                .url(url)
                .method("POST")
                .headers(headers)
                .body(body)
                .build()
        );
    }

    /**
     * Requests given url, headers and body with GET method.
     *
     * @param url     given url
     * @param headers headers
     * @param body    given body
     * @return response
     */
    public static GekHttpResponse post(String url, GekHttpHeaders headers, ByteBuffer body) {
        return GekHttpClient.defaultClient().request(
            GekHttpRequest.newBuilder()
                .url(url)
                .method("POST")
                .headers(headers)
                .body(body)
                .build()
        );
    }

    /**
     * Encodes given form string (for application/x-www-form-urlencoded) with {@link URLEncoder#encode(String, String)}.
     * Using {@link JieChars#defaultCharset()}.
     *
     * @param formString given form string
     * @return encoded string
     */
    public static String encodeForm(String formString) {
        return encodeForm(formString, JieChars.defaultCharset());
    }

    /**
     * Encodes given form string (for application/x-www-form-urlencoded) with
     * {@link URLEncoder#encode(String, String)}.
     *
     * @param formString given form string
     * @param charset    charset
     * @return encoded string
     */
    public static String encodeForm(String formString, Charset charset) {
        try {
            return URLEncoder.encode(formString, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new GekHttpException(e);
        }
    }

    /**
     * Encodes given query string (for url after '?') with {@link URLEncoder#encode(String, String)} and replace '+' to
     * '%20'. Using {@link JieChars#defaultCharset()}.
     *
     * @param queryString given query string
     * @return encoded string
     */
    public static String encodeQuery(String queryString) {
        return encodeQuery(queryString, JieChars.defaultCharset());
    }

    /**
     * Encodes given query string (for url after '?') with {@link URLEncoder#encode(String, String)} and replace '+' to
     * '%20'.
     *
     * @param queryString given query string
     * @param charset     charset
     * @return encoded string
     */
    public static String encodeQuery(String queryString, Charset charset) {
        return encodeForm(queryString, charset).replaceAll("\\+", "%20");
    }

    /**
     * Decodes given form or query string with {@link URLDecoder#decode(String, String)}. Using
     * {@link JieChars#defaultCharset()}.
     *
     * @param encoded given form or query string
     * @return decoded string
     */
    public static String decodeFormOrQuery(String encoded) {
        return decodeFormOrQuery(encoded, JieChars.defaultCharset());
    }

    /**
     * Decodes given form or query string with {@link URLDecoder#decode(String, String)}.
     *
     * @param encoded given form or query string
     * @param charset charset
     * @return decoded string
     */
    public static String decodeFormOrQuery(String encoded, Charset charset) {
        try {
            return URLDecoder.decode(encoded, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new GekHttpException(e);
        }
    }
}
