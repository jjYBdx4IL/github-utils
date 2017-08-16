/*
 * Copyright © 2014 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.utils.io;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

public class IoUtils {

    /**
     * similar to {@link IOUtils#toString(URL,String)}, but allows to define the
     * accepted content type. The charset used to decode the remote's reply is taken from the reply headers.
     * 
     * @param url
     *            the remote service location
     * @param acceptHeader
     *            ie. "text/plain; charset=ASCII" or "text/html" etc.
     * @return the url's content
     * @throws IOException
     *             on I/O error, ie. remote does not indicate any charset
     */
    public static String toString(URL url, String acceptHeader) throws IOException {
        return toString(url, (Charset) null, acceptHeader);
    }

    /**
     * similar to {@link IOUtils#toString(URL,String)}, but allows to define the
     * accepted content type.
     * 
     * @param url
     *            the remote service location
     * @param charset
     *            expected charset returned by remote service, used if headers returned by remote do not indicate
     *            anything
     * @param acceptHeader
     *            ie. "text/plain; charset=ASCII" or "text/html" etc.
     * @return the url's content
     * @throws IOException
     *             on I/O error
     */
    public static String toString(URL url, String charset, String acceptHeader) throws IOException {
        try {
            return toString(url, charset != null ? Charset.forName(charset) : null, acceptHeader);
        } catch (IllegalCharsetNameException | UnsupportedCharsetException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * similar to {@link IOUtils#toString(URL,Charset)}, but allows to define
     * the accepted content type.
     * 
     * @param url
     *            the remote service location
     * @param charset
     *            expected charset returned by remote service, used if headers returned by remote do not indicate
     *            anything
     * @param acceptHeader
     *            ie. "text/plain; charset=ASCII" or "text/html" etc.
     * @return the url's content
     * @throws IOException
     *             on I/O error
     */
    public static String toString(URL url, Charset charset, String acceptHeader) throws IOException {
        if (acceptHeader == null) {
            throw new IllegalArgumentException("acceptHeader must not be null");
        }
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Accept", acceptHeader);
        try (InputStream is = urlConnection.getInputStream()) {
            Charset expectedCharset = getCharset(urlConnection);
            if (expectedCharset == null) {
                expectedCharset = charset;
            }
            if (expectedCharset == null) {
                throw new IOException("failed to determine charset for remote's reply");
            }
            return IOUtils.toString(is, expectedCharset);
        } finally {
            urlConnection.disconnect();
        }
    }

    private static Charset getCharset(URLConnection connection) throws IOException {
        String contentType = connection.getContentType();
        String[] values = contentType.split(";"); // values.length should be 2
        String charset = "";

        for (String value : values) {
            value = value.trim();

            if (value.toLowerCase().startsWith("charset=")) {
                try {
                    return Charset.forName(value.substring("charset=".length()));
                } catch (IllegalCharsetNameException | UnsupportedCharsetException ex) {
                    throw new IOException(ex);
                }
            }
        }

        return null;
    }
}
