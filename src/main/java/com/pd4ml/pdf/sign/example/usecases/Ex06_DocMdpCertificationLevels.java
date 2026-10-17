package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.examples.support.SelfSignedPki;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.SigningOptions;

import java.io.ByteArrayInputStream;

/**
 * 06 -- PDF "Certify" at all three DocMDP levels (P=1/2/3). Certification is
 * only valid on the FIRST signature of a document, so each level is
 * demonstrated on its own fresh copy rather than stacked on one document.
 */
public final class Ex06_DocMdpCertificationLevels {

    public static void main(String[] args) throws Exception {
        SelfSignedPki.Identity generated = SelfSignedPki.generate("Jane Doe");
        byte[] p12 = generated.toPkcs12("signer", "changeit".toCharArray());
        CertificateUtils.SigningIdentity identity =
                CertificateUtils.loadFromPkcs12(new ByteArrayInputStream(p12), "changeit".toCharArray(), "signer");

        for (SigningOptions.CertificationLevel level : SigningOptions.CertificationLevel.values()) {
            SigningOptions options = new SigningOptions().setSignerName("Jane Doe").setCertificationLevel(level);
            byte[] signedPdf = new PdfSigner().sign(SamplePdf.onePage(), identity, options);
            System.out.println(level + ": " + signedPdf.length + " bytes.");
        }
    }
}
