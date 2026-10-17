package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.examples.support.SelfSignedPki;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.SigningOptions;
import com.pd4ml.pdf.sign.VisibleSignatureOptions;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/** 03 -- A visible signature widget with a stamp image (PNG/JPEG) plus a text caption. */
public final class Ex03_VisibleSignatureWithImage {

    public static void main(String[] args) throws Exception {
        SelfSignedPki.Identity generated = SelfSignedPki.generate("Jane Doe");
        byte[] p12 = generated.toPkcs12("signer", "changeit".toCharArray());
        CertificateUtils.SigningIdentity identity =
                CertificateUtils.loadFromPkcs12(new ByteArrayInputStream(p12), "changeit".toCharArray(), "signer");

        byte[] pngBytes = tinyStampImage(); // stand-in for a real company seal or scanned signature
        VisibleSignatureOptions visible = new VisibleSignatureOptions()
                .setPage(0)
                .setRectangle(36, 36, 150, 60)
                .setSignatureImage(new ByteArrayInputStream(pngBytes))
                .setVisibleText("Jane Doe");

        SigningOptions options = new SigningOptions()
                .setSignerName("Jane Doe")
                .setVisibleSignatureOptions(visible);

        byte[] signedPdf = new PdfSigner().sign(SamplePdf.onePage(), identity, options);
        System.out.println("Signed with an image + caption widget: " + signedPdf.length + " bytes.");
    }

    /** A small red square with "JD" on it, real PNG bytes -- generated rather than hand-crafted, so it's always valid. */
    private static byte[] tinyStampImage() throws Exception {
        BufferedImage image = new BufferedImage(64, 32, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(0xB0, 0x20, 0x20));
        g.fillRect(0, 0, 64, 32);
        g.setColor(Color.WHITE);
        g.drawString("JD", 22, 20);
        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        return out.toByteArray();
    }
}
