package com.github.jjYBdx4IL.utils.awt;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 *
 * @author jjYBdx4IL
 */
public class ImageUtils {

    private ImageUtils() {}
            
    /**
     * Quality, bicubic scaling. Width/height ratio not preserved.
     * 
     * @param sbi the image to be scaled
     * @param dWidth desired target width
     * @param dHeight desired target height
     * @return the scaled image
     */
    public static BufferedImage scale(BufferedImage sbi, int dWidth, int dHeight) {
        BufferedImage dbi = null;
        if (sbi != null) {
            dbi = new BufferedImage(dWidth, dHeight, sbi.getType());
            Graphics2D g = dbi.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            AffineTransform at = AffineTransform.getScaleInstance(1d * dWidth / sbi.getWidth(), 1d * dHeight / sbi.getHeight());
            g.drawRenderedImage(sbi, at);
        }
        return dbi;
    }
}
