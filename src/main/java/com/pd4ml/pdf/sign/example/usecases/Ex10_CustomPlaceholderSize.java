package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.examples.support.SelfSignedPki;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.PdfSigningException;
import com.pd4ml.pdf.sign.SigningOptions;

import java.io.ByteArrayInputStream;

/**
 * 10 -- Sizing the {@code /Contents} placeholder for a long certificate
 * chain and/or an RFC 3161 timestamp, and the precise error that results
 * when it's too small.
 */
public final class Ex10_CustomPlaceholderSize {

    public static void main(String[] args) throws Exception {
        SelfSignedPki.Identity generated = SelfSignedPki.generate("Jane Doe");
        byte[] p12 = generated.toPkcs12("signer", "changeit".toCharArray());
        CertificateUtils.SigningIdentity identity =
                CertificateUtils.loadFromPkcs12(new ByteArrayInputStream(p12), "changeit".toCharArray(), "signer");

        try {
            SigningOptions tooSmall = new SigningOptions().setSignerName("Jane Doe").setSignaturePlaceholderSize(64);
            new PdfSigner().sign(SamplePdf.onePage(), identity, tooSmall);
            System.out.println("Unexpectedly succeeded with a 64-byte placeholder.");
        } catch (PdfSigningException e) {
            System.out.println("With a too-small placeholder: " + e.getMessage());
        }

        SigningOptions bigEnough = new SigningOptions().setSignerName("Jane Doe").setSignaturePlaceholderSize(32768);
        byte[] signedPdf = new PdfSigner().sign(SamplePdf.onePage(), identity, bigEnough);
        System.out.println("With a 32768-byte placeholder: signed, " + signedPdf.length + " bytes.");
    }
}
