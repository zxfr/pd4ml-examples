package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.examples.support.SelfSignedPki;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.SigningOptions;

import java.io.ByteArrayInputStream;

/** 01 -- The minimal call: a PKCS#12 key, an invisible signature. Start here. */
public final class Ex01_InvisibleSigningBasic {

    public static void main(String[] args) throws Exception {
        SelfSignedPki.Identity generated = SelfSignedPki.generate("Jane Doe");
        byte[] p12 = generated.toPkcs12("signer", "changeit".toCharArray());
        CertificateUtils.SigningIdentity identity =
                CertificateUtils.loadFromPkcs12(new ByteArrayInputStream(p12), "changeit".toCharArray(), "signer");

        byte[] inputPdf = SamplePdf.onePage();
        SigningOptions options = new SigningOptions()
                .setReason("Approved")
                .setSignerName("Jane Doe");

        byte[] signedPdf = new PdfSigner().sign(inputPdf, identity, options);
        System.out.println("Signed " + inputPdf.length + " -> " + signedPdf.length + " bytes, invisibly, as \"Jane Doe\".");
    }
}
