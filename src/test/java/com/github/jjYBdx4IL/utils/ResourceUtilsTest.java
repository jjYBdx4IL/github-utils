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
package com.github.jjYBdx4IL.utils;

//CHECKSTYLE:OFF
import com.sun.jna.Platform;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;

import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ResourceUtilsTest {

    @Test
    public void testLoadLibrary() {
        Assume.assumeTrue("assume running on platform with gnu compiler suite", Platform.isLinux() || Platform.isGNU());

        assertEquals(2, ResourceUtilsJNITestHelper.inc(1));
    }

    @Test
    public void testLoadLibraryMultipleTimes() {
        Assume.assumeTrue("assume running on platform with gnu compiler suite", Platform.isLinux() || Platform.isGNU());

        ResourceUtils.loadLibrary(ResourceUtilsJNITestHelper.libName);
        ResourceUtils.loadLibrary(ResourceUtilsJNITestHelper.libName);
    }

    @Test(expected = RuntimeException.class)
    public void testLoadLibraryNotFound() {
        ResourceUtils.loadLibrary("ajsdlkjaksd3838js");
    }

    @Test
    public void testExtractResourceFromClasspath() throws Exception {
        File extracted = ResourceUtils.extractResource("/log4j.xml");
        assertTrue(extracted.exists());
        String input = null;
        try (InputStream is = new FileInputStream(extracted)) {
            input = IOUtils.toString(is, "UTF-8");
        }
        assertTrue(extracted.delete());
        assertTrue(input.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
    }

    @Test
    public void testExtractResourceFromJar() throws Exception {
        File extracted = ResourceUtils.extractResource("/org/openimaj/image/data/35smm_original.jpg");
        assertTrue(extracted.exists());
        BufferedImage image = ImageIO.read(extracted);
        assertTrue(extracted.delete());
        assertNotNull(image);
        assertEquals(300, image.getWidth());
        assertEquals(128, image.getHeight());
    }

    @Test(expected = IOException.class)
    public void testExtractResourceNotFound() throws Exception {
        ResourceUtils.extractResource("/lakjdlkasjdasd8f78sdf");
    }

}
