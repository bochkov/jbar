package com.sergeybochkov.jbar.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

}
