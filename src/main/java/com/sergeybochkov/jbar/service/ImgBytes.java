package com.sergeybochkov.jbar.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public final class ImgBytes {

    public static byte[] asBytes(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        return asBytes(img);
    }

    public static byte[] asBytes(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", out);
            out.flush();
            return out.toByteArray();
        }
    }

    private ImgBytes() {
    }
}
