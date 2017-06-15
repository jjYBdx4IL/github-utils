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
package com.github.jjYBdx4IL.utils.awt;

//CHECKSTYLE:OFF
import com.github.jjYBdx4IL.utils.awt.Quantize;
import com.github.jjYBdx4IL.test.FileUtil;
import com.github.jjYBdx4IL.test.GraphicsResource;
import com.github.jjYBdx4IL.test.InteractiveTestBase;
import com.github.jjYBdx4IL.utils.awt.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class QuantizeTest extends InteractiveTestBase {

    private final static Logger log = Logger.getLogger(QuantizeTest.class.getName());
    private final static File tempDir = FileUtil.createMavenTestDir(QuantizeTest.class);

    @Before
    public void before() {
        FileUtil.provideCleanDirectory(tempDir);
    }

    @Test
    public void testQuantize() throws InterruptedException, InvocationTargetException, IOException {
        openWindow();

        BufferedImage image = GraphicsResource.OPENIMAJ_TESTRES_AESTHETICODE.loadImage().getSubimage(400, 400, 400, 400);
        append(image);

        boolean dither = true;
        int colorPaletteSize = 80;
        int max_cols = 3;
        max_cols = Math.min(max_cols, colorPaletteSize);

        // create some random color palette
        //int[] colorPalette = Quantize.createRandomColorPalette(colorPaletteSize);
        int[] colorPalette = Quantize.createGrayScalePalette(20);
        //int[] colorPalette = ImageUtils.deducePalette(image, 256);
        log.debug(Arrays.toString(colorPalette));

        Quantize.ReductionStrategy reductionStrategy = Quantize.ReductionStrategy.AVERAGE_DISTANCE;


        int width = image.getWidth();
        int height = image.getHeight();
        int pixels[] = Quantize.getPixels(image);
        log.debug(String.format("pixels=%d, w=%d, h=%d", pixels.length, width, height));
        long startTime = System.currentTimeMillis();
        int[] palette = Quantize.quantize(pixels, width, height, colorPalette, max_cols, dither, reductionStrategy);
        long duration = System.currentTimeMillis() - startTime;
        log.info(String.format("quantiziation performance: %d kpix/sec", pixels.length / duration));
        log.debug(Arrays.toString(palette));

        BufferedImage reduced = ImageUtils.deepCopy(image);
        reduced.setRGB(0,0,width,height,pixels,0,width);
        append(reduced, String.format("quantiziation performance: %d kpix/sec", pixels.length / duration));

        saveWindowAsImage("testQuantize");
        waitForWindowClosing();
    }

}
