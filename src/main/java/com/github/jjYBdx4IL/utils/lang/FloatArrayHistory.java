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
package com.github.jjYBdx4IL.utils.lang;

import java.util.Locale;

/**
 *
 * @author jjYBdx4IL
 */
public class FloatArrayHistory {

    private final float[] h;
    private int offset;
    private boolean complete = false;

    public FloatArrayHistory(int length) {
        h = new float[length];
    }

    public void add(float f) {
        h[offset] = f;
        offset++;
        if (offset == h.length) {
            offset = 0;
            complete = true;
        }
    }

    public float get(int i) {
        if (i >= h.length) {
            throw new ArrayIndexOutOfBoundsException(i);
        }
        int j = offset + i;
        if (j >= h.length) {
            j -= h.length;
        }
        return h[j];
    }

    /**
     *
     * @param start
     * @param dst
     * @param dstOffset
     * @param len
     */
    public void copy(int start, float[] dst, int dstOffset, int len) {
        if (start + len > h.length || len < 0 || start < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }

        int i = offset + start;
        if (i >= h.length) {
            i -= h.length;
        }
        int _len = len;
        if (i + _len > h.length) {
            _len = h.length - i;
        }
        System.arraycopy(h, i, dst, 0, _len);
        if (_len == len) {
            return;
        }
        System.arraycopy(h, 0, dst, _len, len - _len);
    }

    public boolean isFullyPopulated() {
        return complete;
    }

    public int length() {
        return h.length;
    }

    /**
     * @param start inclusive
     * @param end exclusive
     * @return
     */
    public String toString(int start, int end) {
        StringBuilder sb = new StringBuilder(9 * (end - start));
        for (int i = start; i < end; i++) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(String.format(Locale.ROOT, "%f", get(i)));
        }
        return sb.toString();
    }

    /**
     * Returns as a {@link String} all elements starting at {@literal start}.
     *
     * @param start
     * @return
     */
    public String toString(int start) {
        return toString(start, h.length);
    }
}
