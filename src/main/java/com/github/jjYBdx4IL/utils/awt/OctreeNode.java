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
/**
 *
 * @author Github jjYBdx4IL Projects
 */
class OctreeNode {

    private int l = 0;
    private long c = 0;
    private long r = 0;
    private long g = 0;
    private long b = 0;
    private boolean isLeaf = true;
    private OctreeNode p = null;
    private OctreeNode[] childs = null;

    /**
     * Constructs a root node.
     */
    OctreeNode() {
        this(null, 0);
    }

    OctreeNode(OctreeNode parent, int level) {
        l = level;
        p = parent;
        if (level < 8) {
            childs = new OctreeNode[]{null, null, null, null, null, null, null, null};
        }
    }

    private int computeChildIndex(int pixel) {
        return (pixel >> l) & 0x01 | (pixel >> (l + 7)) & 0x02 | (pixel >> (l + 14)) & 0x04;
    }

    public void sink(int pixel) {
        if (l < 8) {
            int idx = computeChildIndex(pixel);
            if (childs[idx] == null) {
                childs[idx] = new OctreeNode(this, l + 1);
            }
            childs[idx].sink(pixel);
            isLeaf = false;
            return;
        }
        c++;
        r += (pixel >> 16) & 0xFF;
        g += (pixel >> 8) & 0xFF;
        b += (pixel) & 0xFF;
    }

    public int getRGB() {
        assert ((r / c) & ~0xFF) == 0;
        assert ((g / c) & ~0xFF) == 0;
        assert ((b / c) & ~0xFF) == 0;
        return (int) (((r / c) << 16) | ((g / c) << 8) | (b / c) | 0xFF000000);
    }

    public int mergeChilds() {
        if (childs == null) {
            return 0;
        }
        int countRemoved = 0;
        for (int i = 0; i < childs.length; i++) {
            OctreeNode ch = childs[i];
            if (ch == null) {
                continue;
            }
            countRemoved += ch.mergeChilds();
            c += ch.c;
            r += ch.r;
            g += ch.g;
            b += ch.b;
            childs[i] = null;
            countRemoved++;
        }
        isLeaf = true;
        return countRemoved;
    }

    public void traverse(OctreeVisitor visitor) {
        if (childs != null) {
            for (OctreeNode child : childs) {
                if (child != null) {
                    child.traverse(visitor);
                }
            }
        }
        visitor.visit(this);
    }

    public int transform(int pixel) {
        if (isLeaf) {
            return getRGB();
        }
        return childs[computeChildIndex(pixel)].transform(pixel);
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean tree) {
        StringBuilder sb = new StringBuilder();
        toString(sb, tree);
        return sb.toString();
    }

    private void toString(StringBuilder sb, boolean tree) {
        sb.append(OctreeNode.class.getSimpleName()).append(" [")
                .append("rgb=(").append(r).append(",").append(g).append(",").append(b)
                .append("), c=").append(c).append(", l=").append(l).append(", isLeaf=").append(isLeaf())
                .append("]");
        if (!tree) {
            return;
        }
        sb.append(System.lineSeparator());
        if (childs == null) {
            return;
        }
        for (int i = 0; i < childs.length; i++) {
            OctreeNode child = childs[i];
            if (child != null) {
                sb.append(getIndent(l)).append("#").append(i).append(": ");
                child.toString(sb, true);
            }
        }
    }

    public OctreeStatsVisitor computeStats() {
        OctreeStatsVisitor visitor = new OctreeStatsVisitor();
        traverse(visitor);
        return visitor;
    }

    /**
     * @return the l
     */
    public int getLevel() {
        return l;
    }

    /**
     * @return the c
     */
    public long getCount() {
        return c;
    }

    /**
     * @return the isLeaf
     */
    public boolean isLeaf() {
        return isLeaf;
    }

    /**
     * @return the p
     */
    public OctreeNode getParentNode() {
        return p;
    }

    /**
     * @return the childs
     */
    public OctreeNode[] getChilds() {
        return childs;
    }

    private static String indentSource = null;
    private static final int spacesPerIndentLevel = 2;

    private static String getIndent(int indentLevel) {
        if (indentSource == null) {
            StringBuilder sb = new StringBuilder(spacesPerIndentLevel * 8);
            for (int i = 0; i < 8 * spacesPerIndentLevel; i++) {
                sb.append(" ");
            }
            indentSource = sb.toString();
        }
        return indentSource.substring(0, indentLevel * spacesPerIndentLevel);
    }
}
