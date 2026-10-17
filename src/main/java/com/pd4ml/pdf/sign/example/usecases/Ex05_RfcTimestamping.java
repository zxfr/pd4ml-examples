package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.examples.support.SelfSignedPki;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.PdfSigningException;
import com.pd4ml.pdf.sign.SigningOptions;

import java.io.ByteArrayInputStream;

/**
 * 05 -- An RFC 3161 trusted timestamp from a public TSA, with optional Basic
 * Auth. This is a live network call to a third-party service: it succeeds
 * in a normal environment with outbound HTTP access, but a
 * firewalled/offline environment sees {@link PdfSigningException} instead
 * (reported below, not thrown out of {@code main}).
 */
public final class Ex05_RfcTimestamping {

    public static void main(String[] args) throws Exception {
        SelfSignedPki.Identity generated = SelfSignedPki.generate("Jane Doe");
        byte[] p12 = generated.toPkcs12("signer", "changeit".toCharArray());
        CertificateUtils.SigningIdentity identity =
                CertificateUtils.loadFromPkcs12(new ByteArrayInputStream(p12), "changeit".toCharArray(), "signer");

        SigningOptions options = new SigningOptions()
                .setSignerName("Jane Doe")
                .setTsaUrl("http://timestamp.digicert.com");
        // options.setTsaCredentials("user", "pass"); // if your TSA requires Basic Auth

        try {
            byte[] signedPdf = new PdfSigner().sign(SamplePdf.onePage(), identity, options);
            System.out.println("Timestamped signature applied: " + signedPdf.length + " bytes.");
        } catch (PdfSigningException e) {
            System.out.println("Timestamping failed (no/blocked network access in this environment?): " + e.getMessage());
        }
    }
}
