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
import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * See: <a href="http://www.cubic.org/docs/octree.htm">http://www.cubic.org/docs/octree.htm</a>.
 * 
 * @author Github jjYBdx4IL Projects
 */
public class OctreeQuant {

    private static final Logger LOG = LoggerFactory.getLogger(OctreeQuant.class);

    static class LeafCountVisitor implements OctreeVisitor {

        int leafCount = 0;

        @Override
        public void visit(OctreeNode leaf) {
            if (!leaf.isLeaf()) {
                return;
            }
            //log.info("visit: " + leaf);
            leafCount++;
        }
    }

    public static int[] reduce(OctreeNode root, int numColors) {
        long nColors;
        do {
            OctreeStatsVisitor visitor = root.computeStats();
            nColors = visitor.getNumLeafs();
            if (nColors > numColors) {
                visitor.getMinCountNode().getParentNode().mergeChilds();
            }
        } while (nColors > numColors);
        LOG.info(root.toString(true));
        final int[] result = new int[(int) nColors];
        root.traverse(new OctreeVisitor() {
            int i = 0;

            @Override
            public void visit(OctreeNode leaf) {
                if (!leaf.isLeaf()) {
                    return;
                }
                //log.info("visit: " + leaf);
                result[i] = leaf.getRGB();
                i++;
            }
        });
        Arrays.sort(result);
        return result;
    }

    public static OctreeNode build(BufferedImage img) {
        int[] pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
        OctreeNode root = new OctreeNode();
        for (int pixel : pixels) {
            root.sink(pixel);
        }
        return root;
    }

    public static int countLeafs(OctreeNode node) {
        LeafCountVisitor visitor = new LeafCountVisitor();
        node.traverse(visitor);
        return visitor.leafCount;
    }

    public static void simpleReplaceColors(BufferedImage img, OctreeNode root) {
        int[] pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = root.transform(pixels[i]);
        }
        img.setRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
    }
}
