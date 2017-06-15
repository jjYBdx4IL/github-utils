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
package com.github.jjYBdx4IL.utils.ci.jenkins;

//CHECKSTYLE:OFF
import com.github.jjYBdx4IL.test.AdHocHttpServer;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class DependencyGraphTest {

    public static AdHocHttpServer server;
    @Before
    public void beforeTest() throws Exception {
        server = new AdHocHttpServer();
        server.getHandler().setDefaultResponseCodeOK();
    }
    @After
    public void afterTest() throws Exception {
        server.close();
    }

    @Test
    public void testParseStream() throws Exception {
        String content = null;
        try (InputStream is = getClass().getResourceAsStream("jenkinsDep.json")) {
            content = IOUtils.toString(is);
        }
        server.addStaticContent(DependencyGraph.URL_SUFFIX, new AdHocHttpServer.StaticResponse(content));
        DependencyGraph dg = new DependencyGraph(server.computeServerURL(""));
        List<String> queuedJobs = dg.queue(new String[]{"a", "b"}, null, false);
        assertEquals(10, queuedJobs.size());
    }

}
