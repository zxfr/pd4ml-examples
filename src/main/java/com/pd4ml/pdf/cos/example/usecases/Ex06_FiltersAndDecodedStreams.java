package com.pd4ml.pdf.cos.example.usecases;

import com.pd4ml.pdf.cos.COSStream;

import java.nio.charset.Charset;
import java.util.zip.Deflater;

/**
 * 06 -- {@code COSStream.getDecodedBytes()} applies the {@code /Filter}
 * chain automatically; {@code isFullyDecodable()} tells you in advance
 * whether it can (an unsupported filter, typically an image codec, stops
 * decoding rather than throwing -- it's meant to be handed to an image
 * decoder directly).
 */
public final class Ex06_FiltersAndDecodedStreams {

    private static final Charset LATIN1 = Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        String text = "BT /F1 12 Tf 72 120 Td (Flate-compressed content) Tj ET";
        byte[] compressed = deflate(text.getBytes(LATIN1));

        COSStream stream = new COSStream();
        stream.setName("Filter", "FlateDecode");
        stream.setRawData(compressed);

        System.out.println("Fully decodable: " + stream.isFullyDecodable());
        byte[] decoded = stream.getDecodedBytes();
        System.out.println("Decoded: " + new String(decoded, LATIN1));

        // An unsupported filter (an image codec) is reported up front, not discovered via an exception.
        COSStream imageLike = new COSStream();
        imageLike.setName("Filter", "DCTDecode");
        imageLike.setRawData(new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}); // JPEG SOI marker, not full image data
        System.out.println("DCTDecode fully decodable: " + imageLike.isFullyDecodable());
    }

    private static byte[] deflate(byte[] input) {
        Deflater deflater = new Deflater();
        deflater.setInput(input);
        deflater.finish();
        byte[] buffer = new byte[Math.max(64, input.length * 2)];
        int len = deflater.deflate(buffer);
        deflater.end();
        byte[] result = new byte[len];
        System.arraycopy(buffer, 0, result, 0, len);
        return result;
    }
}
