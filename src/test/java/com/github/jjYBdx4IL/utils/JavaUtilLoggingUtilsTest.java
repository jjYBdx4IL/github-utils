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
package com.github.jjYBdx4IL.utils;

import com.github.jjYBdx4IL.test.AdHocHttpServer;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;

import net.schmizz.sshj.common.IOUtils;

import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class JavaUtilLoggingUtilsTest {

    @Test
    public void test() throws Exception {
        JavaUtilLoggingUtils.setJavaNetURLConsoleLoggingLevel(Level.FINEST);

        try (AdHocHttpServer server = new AdHocHttpServer()) {
            server.addStaticContent("/", new AdHocHttpServer.StaticResponse(""));
            URL url = server.computeServerURL("/");
            try (InputStream is = url.openStream()) {
                IOUtils.readFully(is);
            }
        }
    }

}
