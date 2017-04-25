/*
 * Copyright (C) 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.utils.net;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
@SuppressWarnings("deprecation")
public class PEMParser extends org.bouncycastle.openssl.PEMParser {

    private static final Logger log = LoggerFactory.getLogger(PEMParser.class);

    /**
     * Strip PEM comments.
     *
     * @param reader
     * @return a {@link java.io.Reader} that is backed by a byte array
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static Reader stripComments(Reader reader) throws UnsupportedEncodingException, IOException {
        BufferedReader br = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        boolean readingPemContent = false;
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            if (line.indexOf("-----BEGIN") > -1) {
                if (readingPemContent) {
                    throw new IllegalStateException("PEM content start marker inside PEM content");
                }
                readingPemContent = true;
            }
            if (readingPemContent) {
                if (log.isDebugEnabled()) {
                    log.debug("accepted: " + line);
                }
                sb.append(line);
                sb.append("\n");
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("skipped:  " + line);
                }
            }
            if (line.indexOf("-----END") > -1) {
                if (!readingPemContent) {
                    throw new IllegalStateException("PEM content end marker outside PEM content");
                }
                readingPemContent = false;
            }
        }
        if (readingPemContent) {
            throw new IllegalStateException("PEM content not properly terminated.");
        }
        return new InputStreamReader(new ByteArrayInputStream(sb.toString().getBytes("ASCII")), "ASCII");
    }

    /**
     * Strips PEM comments from the input but is otherwise identical to
     * {@link org.bouncycastle.openssl.PEMReader#PEMReader(Reader)}.
     *
     * @param reader
     * @throws IOException
     */
    public PEMParser(Reader reader) throws IOException {
        super(stripComments(reader));
    }
}