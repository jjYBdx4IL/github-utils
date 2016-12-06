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
package com.github.jjYBdx4IL.utils.fma;

import com.github.jjYBdx4IL.utils.env.CI;
import com.github.jjYBdx4IL.utils.env.Surefire;

import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author jjYBdx4IL
 */
public class FMAClientTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(FMAClientTest.class);
    
    @Test
    public void testSearch() throws IOException {
        FMASearchResult result = FMAClient.search("Moule graine", true);
        assertNotNull(result);
        assertEquals(1, result.aTracks.size());
        FMATrack track = result.aTracks.get(0);
        LOG.info(track.toString());
        assertEquals("Moule graine", track.track_title);
    }

    @Test
    public void testLongSearch() throws IOException {
        Assume.assumeTrue(Surefire.isSingleTextExecution());

        FMASearchResult result = FMAClient.search(null, true);
        assertNotNull(result);
        assertTrue(result.aTracks.size() > 10000);
        LOG.info(result.aTracks.get(0).toString());
        LOG.info("results returned: " + result.aTracks.size());
    }

    @Test
    public void testGetDownloadUrl() throws IOException {
        Assume.assumeFalse(CI.isPublic());
        
        FMAClient client = new FMAClient();
        FMATrack track = client.getTrack(25148);
        String dlUrl = FMAClient.parseDownloadUrl(track.track_url);
        assertNotNull(dlUrl);
        LOG.info("dlUrl: " + dlUrl);
    }

    @Test
    public void testGetTrack() throws IOException {
        Assume.assumeFalse(CI.isPublic());

        FMATrack track = new FMAClient().getTrack(25148);
        assertEquals(25148, track.track_id);
        LOG.info(track.toString());
    }
}
