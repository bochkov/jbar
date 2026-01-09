package com.sergeybochkov.jbar.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public final class BarcodeGen {

    private final String data;

    public byte[] generate() throws IOException {
        Code128Writer writer = new Code128Writer();
        Map<EncodeHintType, Object> hints = Map.of();
        BitMatrix matrix = writer.encode(data, BarcodeFormat.CODE_128, 90, 40, hints);
        return ImgBytes.asBytes(
                MatrixToImageWriter.toBufferedImage(matrix)
        );
    }

}
