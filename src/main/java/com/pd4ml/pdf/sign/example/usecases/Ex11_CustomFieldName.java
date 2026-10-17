package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.examples.support.SelfSignedPki;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.SigningOptions;

import java.io.ByteArrayInputStream;

/** 11 -- Naming the AcroForm signature field explicitly, instead of the default "Signature1". */
public final class Ex11_CustomFieldName {

    public static void main(String[] args) throws Exception {
        SelfSignedPki.Identity generated = SelfSignedPki.generate("Jane Doe");
        byte[] p12 = generated.toPkcs12("signer", "changeit".toCharArray());
        CertificateUtils.SigningIdentity identity =
                CertificateUtils.loadFromPkcs12(new ByteArrayInputStream(p12), "changeit".toCharArray(), "signer");

        SigningOptions options = new SigningOptions().setSignerName("Jane Doe").setFieldName("ApprovalSignature");
        byte[] signedPdf = new PdfSigner().sign(SamplePdf.onePage(), identity, options);
        System.out.println("Signed with field name \"ApprovalSignature\": " + signedPdf.length + " bytes. "
                + "See Ex18_ListSignatureFields to confirm.");
    }
}
