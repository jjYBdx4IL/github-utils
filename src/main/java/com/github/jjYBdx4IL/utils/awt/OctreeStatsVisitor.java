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
public class OctreeStatsVisitor implements OctreeVisitor {

    private OctreeNode minCountNode = null;
    private OctreeNode maxCountNode = null;
    private long numLeafs = 0;
    private long numNodes = 0;

    @Override
    public void visit(OctreeNode leaf) {
        numNodes++;
        if (!leaf.isLeaf()) {
            return;
        }
        if (getMinCountNode() == null || leaf.getCount() < getMinCountNode().getCount()) {
            minCountNode = leaf;
        }
        if (getMaxCountNode() == null || leaf.getCount() > getMaxCountNode().getCount()) {
            maxCountNode = leaf;
        }
        numLeafs++;
    }

    /**
     * @return the minCountNode
     */
    public OctreeNode getMinCountNode() {
        return minCountNode;
    }

    /**
     * @return the maxCountNode
     */
    public OctreeNode getMaxCountNode() {
        return maxCountNode;
    }

    /**
     * @return the numLeafs
     */
    public long getNumLeafs() {
        return numLeafs;
    }

    /**
     * @return the numNodes
     */
    public long getNumNodes() {
        return numNodes;
    }
}
