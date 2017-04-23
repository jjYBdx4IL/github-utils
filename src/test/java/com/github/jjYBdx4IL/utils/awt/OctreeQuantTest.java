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
package com.github.jjYBdx4IL.utils.awt;

/*
 * #%L
 * Shared Package
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
import com.github.jjYBdx4IL.utils.awt.OctreeVisitor;
import com.github.jjYBdx4IL.utils.awt.OctreeNode;
import com.github.jjYBdx4IL.utils.awt.OctreeQuant;
import com.github.jjYBdx4IL.test.InteractiveTestBase;
import com.github.jjYBdx4IL.test.GraphicsResource;
import com.github.jjYBdx4IL.utils.awt.ImageUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class OctreeQuantTest extends InteractiveTestBase {

    private final static Logger log = Logger.getLogger(OctreeQuantTest.class.getName());

    @Test
    public void testBuild() {
        OctreeNode root = OctreeQuant.build(createImg1());
        System.out.println(root);
    }

    @Test
    public void testTraverse() {
        OctreeNode root = OctreeQuant.build(createImg1());
        final AtomicInteger count = new AtomicInteger(0);
        root.traverse(new OctreeVisitor() {

            @Override
            public void visit(OctreeNode leaf) {
                if (!leaf.isLeaf()) {
                    return;
                }
                count.incrementAndGet();
            }
        });
        assertEquals(3, count.get());
    }

    @Test
    public void testReduceNoOp() {
        OctreeNode root = OctreeQuant.build(createImg1());
        int[] pal = OctreeQuant.reduce(root, 3);
        assertEquals(3, pal.length);
        Arrays.sort(pal);
        assertEquals(Color.BLUE.getRGB(), pal[0]);
        assertEquals(Color.GREEN.getRGB(), pal[1]);
        assertEquals(Color.RED.getRGB(), pal[2]);

        final AtomicInteger count = new AtomicInteger(0);
        root.traverse(new OctreeVisitor() {

            @Override
            public void visit(OctreeNode leaf) {
                if (!leaf.isLeaf()) {
                    return;
                }
                count.incrementAndGet();
            }
        });
        assertEquals(3, count.get());
    }

    @Test
    public void testReduce() {
        OctreeNode root = OctreeQuant.build(createImg1());
        int[] pal = OctreeQuant.reduce(root, 2);
        assertTrue(1 <= pal.length && pal.length <= 2);

        final AtomicInteger leafCount = new AtomicInteger(0);
        final AtomicInteger pixelCount = new AtomicInteger(0);
        root.traverse(new OctreeVisitor() {

            @Override
            public void visit(OctreeNode leaf) {
                if (!leaf.isLeaf()) {
                    return;
                }
                leafCount.incrementAndGet();
                pixelCount.addAndGet((int) leaf.getCount());
            }
        });
        //assertEquals(1, leafCount.get());
        assertEquals(3, pixelCount.get());
    }

    @Test
    public void testReduceImage() throws InvocationTargetException, InterruptedException {
        openWindow();

        BufferedImage img = GraphicsResource.OPENIMAJ_TESTRES_AESTHETICODE.loadImage().getSubimage(400, 400, 400, 400);
        append(img, "input");

        int[] pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());

        long start = System.currentTimeMillis();
        OctreeNode root = OctreeQuant.build(img);
        log.info(String.format("build performance = %d kpix/sec", pixels.length / (System.currentTimeMillis() - start)));

        long nColors = root.computeStats().getNumLeafs();
        int targetColors = 256;
        start = System.currentTimeMillis();
        int[] pal = OctreeQuant.reduce(root, targetColors);
        log.info(String.format("reduce performance = %d colors/sec", (nColors-pal.length) * 1000 / (System.currentTimeMillis() - start)));
        assertTrue(1 <= pal.length && pal.length <= targetColors);

        log.info("resulting palette: " + Arrays.toString(pal));

        start = System.currentTimeMillis();
        BufferedImage replaced1 = ImageUtils.deepCopy(img);
        ImageUtils.replaceColors(replaced1, pal);
        log.info(String.format("replace performance = %d kpix/sec", pixels.length / (System.currentTimeMillis() - start)));
        append(replaced1, "replace with nearest palette color");

        BufferedImage replaced2 = ImageUtils.deepCopy(img);
        OctreeQuant.simpleReplaceColors(replaced2, root);
        log.info(String.format("simple replace performance = %d kpix/sec", pixels.length / (System.currentTimeMillis() - start)));
        append(replaced2, "simple replace using reduced octree");

        // display palette colors
        BufferedImage pimg = new BufferedImage(img.getWidth(), 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D pg = (Graphics2D) pimg.getGraphics();
        final int w = pimg.getWidth();
        final int h = pimg.getHeight();
        final int n = pal.length;
        for (int i=0; i<pal.length; i++) {
            pg.setColor(new Color(pal[i]));
            pg.fillRect(i*w/n, 0, w/n+1, h);
        }
        append(pimg, "palette");

        saveWindowAsImage("testQuantize");
        waitForWindowClosing();
    }

    @SuppressWarnings("unused")
	@Test
    public void testReduceImage2() {
        BufferedImage img = GraphicsResource.OPENIMAJ_TESTRES_AESTHETICODE.loadImage().getSubimage(400, 400, 3, 3);
        int[] pal = OctreeQuant.reduce(OctreeQuant.build(img), 4);

    }

    @Test
    public void testReduceImage3() {
        BufferedImage img = createImg2();
        OctreeNode root = OctreeQuant.build(img);
        int[] pal = OctreeQuant.reduce(root, 2);
        ImageUtils.replaceColors(img, pal);
        assertEquals(Color.WHITE.getRGB(), img.getRGB(0, 0));
        assertEquals(Color.WHITE.getRGB(), img.getRGB(1, 0));
        assertEquals(Color.WHITE.getRGB(), img.getRGB(2, 0));
        assertEquals(Color.WHITE.getRGB(), img.getRGB(0, 1));
        assertEquals(Color.BLUE.getRGB(), img.getRGB(1, 1));
        assertEquals(Color.WHITE.getRGB(), img.getRGB(2, 1));
        assertEquals(Color.WHITE.getRGB(), img.getRGB(0, 2));
        assertEquals(Color.WHITE.getRGB(), img.getRGB(1, 2));
        assertEquals(Color.WHITE.getRGB(), img.getRGB(2, 2));
    }

    @Test
    public void testReduceImage4() {
        BufferedImage img = createImg2();
        OctreeNode root = OctreeQuant.build(img);
        int[] pal = OctreeQuant.reduce(root, 1);
        ImageUtils.replaceColors(img, pal);
        int c = new Color(255*8/9,255*8/9,255*9/9,255).getRGB();
        assertEquals(c, img.getRGB(0, 0));
        assertEquals(c, img.getRGB(1, 0));
        assertEquals(c, img.getRGB(2, 0));
        assertEquals(c, img.getRGB(0, 1));
        assertEquals(c, img.getRGB(1, 1));
        assertEquals(c, img.getRGB(2, 1));
        assertEquals(c, img.getRGB(0, 2));
        assertEquals(c, img.getRGB(1, 2));
        assertEquals(c, img.getRGB(2, 2));
    }

    @Test
    public void testCountLeafs() {
        OctreeNode root = OctreeQuant.build(createImg1());
        assertEquals(3, OctreeQuant.countLeafs(root));
    }

    protected BufferedImage createImg1() {
        BufferedImage img = new BufferedImage(3, 1, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, Color.RED.getRGB());
        img.setRGB(1, 0, Color.GREEN.getRGB());
        img.setRGB(2, 0, Color.BLUE.getRGB());
        return img;
    }

    protected BufferedImage createImg2() {
        BufferedImage img = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, Color.WHITE.getRGB());
        img.setRGB(1, 0, Color.WHITE.getRGB());
        img.setRGB(2, 0, Color.WHITE.getRGB());
        img.setRGB(0, 1, Color.WHITE.getRGB());
        img.setRGB(1, 1, Color.BLUE.getRGB());
        img.setRGB(2, 1, Color.WHITE.getRGB());
        img.setRGB(0, 2, Color.WHITE.getRGB());
        img.setRGB(1, 2, Color.WHITE.getRGB());
        img.setRGB(2, 2, Color.WHITE.getRGB());
        return img;
    }
}
