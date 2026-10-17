package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.security.PdfEncryptor;
import com.pd4ml.pdf.cos.writer.COSDocumentWriter;
import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.examples.support.SelfSignedPki;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.PdfSigningException;
import com.pd4ml.pdf.sign.SigningOptions;

import java.io.ByteArrayInputStream;

/** 15 -- Every {@link PdfSigningException} failure mode worth knowing by name, and how to recognize it. */
public final class Ex15_ErrorHandlingPatterns {

    public static void main(String[] args) throws Exception {
        SelfSignedPki.Identity generated = SelfSignedPki.generate("Jane Doe");
        byte[] p12 = generated.toPkcs12("signer", "changeit".toCharArray());
        CertificateUtils.SigningIdentity identity =
                CertificateUtils.loadFromPkcs12(new ByteArrayInputStream(p12), "changeit".toCharArray(), "signer");
        PdfSigner signer = new PdfSigner();

        try {
            signer.sign(SamplePdf.onePage(), null, new SigningOptions());
        } catch (PdfSigningException e) {
            System.out.println("null identity: " + e.getMessage());
        }

        try {
            signer.sign(SamplePdf.onePage(), identity, new SigningOptions().setSignaturePlaceholderSize(16));
        } catch (PdfSigningException e) {
            System.out.println("undersized placeholder: " + e.getMessage());
        }

        try {
            byte[] certified = signer.sign(SamplePdf.onePage(), identity, new SigningOptions()
                    .setSignerName("Jane Doe")
                    .setCertificationLevel(SigningOptions.CertificationLevel.NO_CHANGES_ALLOWED));
            signer.sign(certified, identity, new SigningOptions()
                    .setSignerName("Jane Doe")
                    .setFieldName("Signature2")
                    .setCertificationLevel(SigningOptions.CertificationLevel.FORM_FILLING_ALLOWED));
        } catch (PdfSigningException e) {
            System.out.println("double certification: " + e.getMessage());
        }

        try {
            COSDocument plain = COSParser.load(SamplePdf.onePage());
            PdfEncryptor.encrypt(plain, new PdfEncryptor.Options().userPassword("open"));
            byte[] encrypted = COSDocumentWriter.write(plain);
            signer.sign(encrypted, identity, new SigningOptions());
        } catch (PdfSigningException e) {
            System.out.println("encrypted input: " + e.getMessage());
        }
    }
}
