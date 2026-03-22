package com.blackaby.Frontend;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Centralises loading of packaged desktop UI artwork.
 */
public final class AppAssets {

    private static final String LOGO_NO_BG_PATH = "/Images/LogoNoBG.png";
    private static final String LOGO_NO_BG_128_PATH = "/Images/LogoNoBG128.png";
    private static final String LOGO_BG_PATH = "/Images/LogoBG.png";

    private AppAssets() {
    }

    /**
     * Returns the multi-resolution icon set used for application windows.
     *
     * @return ordered icon images for the frame chrome
     */
    public static List<Image> WindowIcons() {
        BufferedImage baseIcon = LoadBufferedImage(LOGO_NO_BG_128_PATH);
        if (baseIcon == null) {
            return List.of();
        }
        return List.of(
                ScaleImage(baseIcon, 16, 16),
                ScaleImage(baseIcon, 24, 24),
                ScaleImage(baseIcon, 32, 32),
                ScaleImage(baseIcon, 48, 48),
                baseIcon);
    }

    /**
     * Creates a scaled logo icon from the transparent artwork.
     *
     * @param size target size in pixels
     * @return scaled icon or {@code null} if the resource could not be loaded
     */
    public static ImageIcon HeaderLogoIcon(int size) {
        return CreateScaledIcon(LOGO_NO_BG_PATH, size, size);
    }

    /**
     * Creates a scaled logo icon from the boxed artwork.
     *
     * @param size target size in pixels
     * @return scaled icon or {@code null} if the resource could not be loaded
     */
    public static ImageIcon AboutLogoIcon(int size) {
        return CreateScaledIcon(LOGO_BG_PATH, size, size);
    }

    private static ImageIcon CreateScaledIcon(String resourcePath, int width, int height) {
        BufferedImage image = LoadBufferedImage(resourcePath);
        if (image == null) {
            return null;
        }
        return new ImageIcon(ScaleImage(image, width, height));
    }

    private static BufferedImage LoadBufferedImage(String resourcePath) {
        try (InputStream stream = AppAssets.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                return null;
            }
            return ImageIO.read(stream);
        } catch (IOException exception) {
            return null;
        }
    }

    private static BufferedImage ScaleImage(BufferedImage source, int width, int height) {
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = scaled.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.drawImage(source.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        graphics.dispose();
        return scaled;
    }
}
