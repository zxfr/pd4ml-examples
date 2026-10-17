package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.examples.support.SelfSignedPki;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.SigningOptions;

import java.io.ByteArrayInputStream;

/**
 * 04 -- Choosing SHA-256 / SHA-384 / SHA-512; the actual JCA signature
 * algorithm string (the "...withRSA" / "...withECDSA" suffix) is derived
 * automatically from the certificate's own key type.
 */
public final class Ex04_DigestAlgorithmChoice {

    public static void main(String[] args) throws Exception {
        SelfSignedPki.Identity generated = SelfSignedPki.generate("Jane Doe");
        byte[] p12 = generated.toPkcs12("signer", "changeit".toCharArray());
        CertificateUtils.SigningIdentity identity =
                CertificateUtils.loadFromPkcs12(new ByteArrayInputStream(p12), "changeit".toCharArray(), "signer");

        for (SigningOptions.DigestAlgorithm digest : SigningOptions.DigestAlgorithm.values()) {
            SigningOptions options = new SigningOptions().setSignerName("Jane Doe").setDigestAlgorithm(digest);
            byte[] signedPdf = new PdfSigner().sign(SamplePdf.onePage(), identity, options);
            System.out.println(digest + ": signed, " + signedPdf.length + " bytes.");
        }
    }
}
