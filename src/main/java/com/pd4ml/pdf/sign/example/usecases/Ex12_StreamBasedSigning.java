package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.examples.support.SelfSignedPki;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.SigningOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;

/** 12 -- The three equivalent call shapes: File-to-File, InputStream/OutputStream, and byte[]-to-byte[]. */
public final class Ex12_StreamBasedSigning {

    public static void main(String[] args) throws Exception {
        SelfSignedPki.Identity generated = SelfSignedPki.generate("Jane Doe");
        byte[] p12 = generated.toPkcs12("signer", "changeit".toCharArray());
        CertificateUtils.SigningIdentity identity =
                CertificateUtils.loadFromPkcs12(new ByteArrayInputStream(p12), "changeit".toCharArray(), "signer");

        PdfSigner signer = new PdfSigner();
        SigningOptions options = new SigningOptions().setSignerName("Jane Doe");

        // byte[] in, byte[] out
        byte[] signedBytes = signer.sign(SamplePdf.onePage(), identity, options);
        System.out.println("byte[]  -> byte[] : " + signedBytes.length + " bytes.");

        // InputStream in, OutputStream out (neither closed by sign())
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        signer.sign(new ByteArrayInputStream(SamplePdf.onePage()), out, identity, options);
        System.out.println("Stream  -> Stream : " + out.size() + " bytes.");

        // File in, File out (output may equal input)
        File inputFile = File.createTempFile("pd4ml-sign-example-in", ".pdf");
        File outputFile = File.createTempFile("pd4ml-sign-example-out", ".pdf");
        inputFile.deleteOnExit();
        outputFile.deleteOnExit();
        Files.write(inputFile.toPath(), SamplePdf.onePage());
        signer.sign(inputFile, outputFile, identity, options);
        System.out.println("File    -> File   : " + outputFile.length() + " bytes, written to " + outputFile);
    }
}
