package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.pdf.cos.COSArray;
import com.pd4ml.pdf.cos.COSBase;
import com.pd4ml.pdf.cos.COSDictionary;
import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.examples.support.SelfSignedPki;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.SigningOptions;

import java.io.ByteArrayInputStream;

/** 18 -- Programmatically enumerating signature fields and their metadata (name, signed/unsigned, /Reason, /M). */
public final class Ex18_ListSignatureFields {

    public static void main(String[] args) throws Exception {
        SelfSignedPki.Identity alice = SelfSignedPki.generate("Alice");
        SelfSignedPki.Identity bob = SelfSignedPki.generate("Bob");
        CertificateUtils.SigningIdentity aliceIdentity = CertificateUtils.loadFromPkcs12(
                new ByteArrayInputStream(alice.toPkcs12("alice", "pw".toCharArray())), "pw".toCharArray(), "alice");
        CertificateUtils.SigningIdentity bobIdentity = CertificateUtils.loadFromPkcs12(
                new ByteArrayInputStream(bob.toPkcs12("bob", "pw".toCharArray())), "pw".toCharArray(), "bob");

        PdfSigner signer = new PdfSigner();
        byte[] signedOnce = signer.sign(SamplePdf.onePage(), aliceIdentity,
                new SigningOptions().setSignerName("Alice").setReason("First approval").setFieldName("Signature1"));
        byte[] signedTwice = signer.sign(signedOnce, bobIdentity,
                new SigningOptions().setSignerName("Bob").setReason("Second approval").setFieldName("Signature2"));

        COSDocument document = COSParser.load(signedTwice);
        COSDictionary acroForm = document.getCatalog().getDictionary("AcroForm");
        COSArray fields = acroForm.getArray("Fields");
        for (COSBase entry : fields) {
            COSDictionary field = (COSDictionary) entry.resolve();
            String name = field.getString("T");
            COSBase vRaw = field.getItem("V");
            if (vRaw == null) {
                System.out.println(name + ": unsigned");
                continue;
            }
            COSDictionary sigDict = (COSDictionary) vRaw.resolve();
            System.out.println(name + ": signed by \"" + sigDict.getString("Name") + "\", reason=\""
                    + sigDict.getString("Reason") + "\", date=" + sigDict.getString("M"));
        }
    }
}
