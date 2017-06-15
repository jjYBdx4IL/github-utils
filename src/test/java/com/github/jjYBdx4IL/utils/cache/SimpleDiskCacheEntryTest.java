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
package com.github.jjYBdx4IL.utils.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

//CHECKSTYLE:OFF
import com.github.jjYBdx4IL.junit.rules.SysPropsRestorerRule;
import com.github.jjYBdx4IL.test.AdHocHttpServer;
import com.github.jjYBdx4IL.test.FileUtil;
import com.github.jjYBdx4IL.utils.cache.SimpleDiskCacheEntry.UpdateMode;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jjYBdx4IL
 */
public class SimpleDiskCacheEntryTest {

    public static AdHocHttpServer server;
    private static final File cacheDir = FileUtil.createMavenTestDir(SimpleDiskCacheEntryTest.class);

    @ClassRule
    public static TestRule restoreSysPropsRule = new SysPropsRestorerRule();

    @BeforeClass
    public static void beforeClass() {
        System.setProperty(SimpleDiskCacheEntry.PROPNAME_CACHE_DIR, cacheDir.getAbsolutePath());
    }

    @Before
    public void beforeTest() throws Exception {
        server = new AdHocHttpServer();
        FileUtils.deleteDirectory(cacheDir);
    }

    @After
    public void afterTest() throws Exception {
        server.close();
        if (cacheDir.exists()) {
            cacheDir.delete();
        }
    }

    @Test
    public void testServerError() throws Exception {
        URL url = add("/testServerError", "A", HttpServletResponse.SC_NOT_FOUND);
        try {
            retrieve(url, UpdateMode.NEVER);
            fail();
        } catch (IOException ex) {
        }

        url = add("/testServerError", "B");
        assertEquals("B", retrieve(url, UpdateMode.NEVER));
    }

    @Test
    public void testErrorRecovery() throws Exception {
        URL url = add("/testErrorRecovery", "A");
        assertEquals("A", retrieve(url, UpdateMode.NEVER));

        corruptCacheFiles();

        url = add("/testErrorRecovery", "B");
        assertEquals("B", retrieve(url, UpdateMode.NEVER));
    }

    @Test
    public void testErrorRecoveryWrongURL() throws Exception {
        URL url = add("/testErrorRecovery", "A");
        assertEquals("A", retrieve(url, UpdateMode.NEVER));

        corruptCacheFilesWrongURL();

        url = add("/testErrorRecovery", "B");
        assertEquals("B", retrieve(url, UpdateMode.NEVER));
    }

    @Test
    public void testErrorRecoveryWrongClass() throws Exception {
        URL url = add("/testErrorRecovery", "A");
        assertEquals("A", retrieve(url, UpdateMode.NEVER));

        corruptCacheFilesWrongClass();

        url = add("/testErrorRecovery", "B");
        assertEquals("B", retrieve(url, UpdateMode.NEVER));
    }

    @Test
    public void testUpdateModeNever() throws Exception {
        URL url = add("/test", "A");
        assertEquals("A", retrieve(url, UpdateMode.NEVER));

        url = add("/test", "B");
        assertEquals("A", retrieve(url, UpdateMode.NEVER));
    }

    @Test
    public void testUpdateModeDailyURLChanged() throws Exception {
        URL url = add("/test", "A");
        assertEquals("A", retrieveSameFile(url, UpdateMode.DAILY));

        // cache file gets removed when URL changes
        url = add("/test2", "C");
        assertEquals("C", retrieveSameFile(url, UpdateMode.DAILY));

        url = add("/test2", "E");
        assertEquals("C", retrieveSameFile(url, UpdateMode.DAILY));
    }

    @Test
    public void testUpdateModeNeverURLChanged() throws Exception {
        URL url = add("/test", "A");
        assertEquals("A", retrieveSameFile(url, UpdateMode.NEVER));

        // cache file gets removed when URL changes
        url = add("/test2", "C");
        assertEquals("C", retrieveSameFile(url, UpdateMode.NEVER));

        url = add("/test2", "E");
        assertEquals("C", retrieveSameFile(url, UpdateMode.NEVER));
    }

    @Test
    public void testUpdateModeAlways() throws Exception {
        URL url = add("/test", "A");
        assertEquals("A", retrieve(url, UpdateMode.ALWAYS));

        url = add("/test", "B");
        assertEquals("B", retrieve(url, UpdateMode.ALWAYS));
    }

    @Test
    public void testUpdateModeAlwaysWithFallback() throws Exception {
        URL url = add("/test", "A");
        assertEquals("A", retrieve(url, UpdateMode.ALWAYS));

        // no error -> no fallback required
        url = add("/test", "B");
        assertEquals("B", retrieveFallback(url, UpdateMode.ALWAYS));

        // error -> fallback required
        url = add("/test", "C", HttpServletResponse.SC_NOT_FOUND);
        assertEquals("B", retrieveFallback(url, UpdateMode.ALWAYS));
    }

    private void corruptCacheFiles() throws FileNotFoundException, IOException {
        byte[] ba = new byte[1024];
        for (File f : cacheDir.listFiles()) {
            try (OutputStream os = new FileOutputStream(f)) {
                IOUtils.write(ba, os);
            }
        }
    }

    private void corruptCacheFilesWrongClass() throws FileNotFoundException, IOException {
        byte[] ba;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(new Integer(123));
            }
            ba = baos.toByteArray();
        }

        for (File f : cacheDir.listFiles()) {
            try (OutputStream os = new FileOutputStream(f)) {
                IOUtils.write(ba, os);
            }
        }
    }

    private void corruptCacheFilesWrongURL() throws FileNotFoundException, IOException {
        byte[] ba;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(new SimpleDiskCacheEntryHeader(new URL("http:////random/things")));
            }
            ba = baos.toByteArray();
        }

        for (File f : cacheDir.listFiles()) {
            try (OutputStream os = new FileOutputStream(f)) {
                IOUtils.write(ba, os);
                IOUtils.write("lkjlkajsd".getBytes(), os);
            }
        }
    }

    private URL add(String relPath, String content) throws MalformedURLException {
        return server.addStaticContent(relPath, new AdHocHttpServer.StaticResponse(content));
    }

    private URL add(String relPath, String content, int sc) throws MalformedURLException {
        return server.addStaticContent(relPath, new AdHocHttpServer.StaticResponse(content, sc));
    }

    @SuppressWarnings("deprecation")
    private String retrieveSameFile(URL url, UpdateMode updateMode) throws MalformedURLException, IOException {
        try (InputStream is = new SimpleDiskCacheEntry(url, new File(cacheDir, "test"), updateMode)
                .getInputStream(false)) {
            return IOUtils.toString(is);
        }
    }

    @SuppressWarnings("deprecation")
    private String retrieve(URL url, UpdateMode updateMode) throws MalformedURLException, IOException {
        try (InputStream is = new SimpleDiskCacheEntry(url, updateMode).getInputStream(false)) {
            return IOUtils.toString(is);
        }
    }

    @SuppressWarnings("deprecation")
    private String retrieveFallback(URL url, UpdateMode updateMode) throws MalformedURLException, IOException {
        try (InputStream is = new SimpleDiskCacheEntry(url, updateMode).getInputStream(true)) {
            return IOUtils.toString(is);
        }
    }

}
