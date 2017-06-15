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
package com.github.jjYBdx4IL.utils.fma;

//CHECKSTYLE:OFF
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FMASearchResultTest {

    private static final Logger LOG = LoggerFactory.getLogger(FMASearchResultTest.class);

    @Test
    public void testMerge() throws IOException {
        FMASearchResult result1 = FMAClient.search("Moule graine", true);
        FMASearchResult result2 = FMAClient.search("Moule graine", true);

        assertEquals(1, result1.size());
        assertEquals(1, result2.size());

        result1.merge(result2);
        assertEquals(2, result1.size());
    }

    @Test
    public void testPostprocess() throws IOException {
        FMASearchResult result1 = FMAClient.search("A New Day in a New Sector", true);
        FMASearchResult result2 = FMAClient.search("A New Day in a New Sector", true);
        FMASearchResult result3 = FMAClient.search("Moule graine", true);

        assertEquals(1, result1.size());
        assertEquals(1, result2.size());
        assertEquals(1, result3.size());

        result1.merge(result2);
        result1.merge(result3);
        assertEquals(3, result1.size());

        result1.postprocess();
        
        assertEquals(2, result1.size());
        assertEquals("Moule graine", result1.aTracks.get(0).track_title);
        assertEquals("A New Day in a New Sector", result1.aTracks.get(1).track_title);
        LOG.info(result1.aTracks.get(0).toString());
        LOG.info(result3.aTracks.get(0).toString());
    }

}
