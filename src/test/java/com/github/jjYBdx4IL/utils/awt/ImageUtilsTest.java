/*
 * Copyright 2017 Github jjYBdx4IL Projects.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.utils.awt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author work
 */
public class ImageUtilsTest {

    @Test
    public void testScale() {
        BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.red);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        
        BufferedImage result = ImageUtils.scale(image, 2, 5);
        
        assertEquals(2, result.getWidth());
        assertEquals(5, result.getHeight());
        assertEquals(BufferedImage.TYPE_3BYTE_BGR, result.getType());
        
        for (int x = 0; x < result.getWidth(); x++) {
            for (int y = 0; y < result.getHeight(); y++) {
                assertEquals(Color.red, new Color(result.getRGB(x, y)));
            }
        }
    }
    
}
