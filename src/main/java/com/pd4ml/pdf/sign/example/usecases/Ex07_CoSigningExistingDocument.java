package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.examples.support.SelfSignedPki;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.SigningOptions;

import java.io.ByteArrayInputStream;

/**
 * 07 -- Two independent signers applying their own signatures to the same
 * (uncertified) document -- each just needs its own field name.
 */
public final class Ex07_CoSigningExistingDocument {

    public static void main(String[] args) throws Exception {
        SelfSignedPki.Identity alice = SelfSignedPki.generate("Alice");
        SelfSignedPki.Identity bob = SelfSignedPki.generate("Bob");
        CertificateUtils.SigningIdentity aliceIdentity = CertificateUtils.loadFromPkcs12(
                new ByteArrayInputStream(alice.toPkcs12("alice", "pw".toCharArray())), "pw".toCharArray(), "alice");
        CertificateUtils.SigningIdentity bobIdentity = CertificateUtils.loadFromPkcs12(
                new ByteArrayInputStream(bob.toPkcs12("bob", "pw".toCharArray())), "pw".toCharArray(), "bob");

        PdfSigner signer = new PdfSigner();
        byte[] onceSigned = signer.sign(SamplePdf.onePage(), aliceIdentity,
                new SigningOptions().setSignerName("Alice").setFieldName("Signature1"));
        byte[] twiceSigned = signer.sign(onceSigned, bobIdentity,
                new SigningOptions().setSignerName("Bob").setFieldName("Signature2"));

        System.out.println("Two independent signatures applied: " + onceSigned.length + " -> " + twiceSigned.length
                + " bytes. See Ex18_ListSignatureFields to enumerate them.");
    }
}
