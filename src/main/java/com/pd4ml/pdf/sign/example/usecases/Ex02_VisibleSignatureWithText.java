package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.examples.support.SelfSignedPki;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.SigningOptions;
import com.pd4ml.pdf.sign.VisibleSignatureOptions;

import java.io.ByteArrayInputStream;

/** 02 -- A visible signature widget with wrapped text only, using the built-in Helvetica metrics (no image). */
public final class Ex02_VisibleSignatureWithText {

    public static void main(String[] args) throws Exception {
        SelfSignedPki.Identity generated = SelfSignedPki.generate("Jane Doe");
        byte[] p12 = generated.toPkcs12("signer", "changeit".toCharArray());
        CertificateUtils.SigningIdentity identity =
                CertificateUtils.loadFromPkcs12(new ByteArrayInputStream(p12), "changeit".toCharArray(), "signer");

        VisibleSignatureOptions visible = new VisibleSignatureOptions()
                .setPage(0)
                .setRectangle(36, 36, 220, 70)
                .setVisibleText("Digitally signed by Jane Doe\nReason: Approved");

        SigningOptions options = new SigningOptions()
                .setReason("Approved")
                .setSignerName("Jane Doe")
                .setVisibleSignatureOptions(visible);

        byte[] signedPdf = new PdfSigner().sign(SamplePdf.onePage(), identity, options);
        System.out.println("Signed with a visible text-only widget on page 1: " + signedPdf.length + " bytes.");
    }
}
