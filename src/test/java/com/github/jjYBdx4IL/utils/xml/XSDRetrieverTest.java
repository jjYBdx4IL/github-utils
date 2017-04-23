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
package com.github.jjYBdx4IL.utils.xml;

/*
 * #%L
 * Shared Package
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.github.jjYBdx4IL.test.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.dom4j.DocumentException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class XSDRetrieverTest {

    private static File tempDir = FileUtil.createMavenTestDir(XSDRetrieverTest.class);

    @Before
    public void beforeTest() {
        FileUtil.provideCleanDirectory(tempDir);
    }

    @Ignore
    @Test
    public void testRetrieve() throws IOException {
        XSDRetriever.retrieve("SDMXQuery.xsd", "https://sdw-wsrest.ecb.europa.eu/vocabulary/sdmx/2_1", tempDir.getAbsolutePath());
    }

    @Test
    public void testGetIncludeImportSchemaLocations() throws CompressorException, DocumentException, IOException {
        try (CompressorInputStream input = new CompressorStreamFactory()
                .createCompressorInputStream(getClass().getResourceAsStream("SDMXMessage.xsd.xz"))) {
            List<String> list = XSDRetriever.getIncludeImportSchemaLocations(input);
            assertEquals("SDMXMessageFooter.xsd", list.get(0));
            assertEquals(10, list.size());
        }
        try (CompressorInputStream input = new CompressorStreamFactory()
                .createCompressorInputStream(getClass().getResourceAsStream("SDMXStructure.xsd.xz"))) {
            List<String> list = XSDRetriever.getIncludeImportSchemaLocations(input);
            assertEquals("SDMXStructureBase.xsd", list.get(0));
            assertEquals("SDMXStructureStructureSet.xsd", list.get(15));
            assertEquals(16, list.size());
        }
    }
}
