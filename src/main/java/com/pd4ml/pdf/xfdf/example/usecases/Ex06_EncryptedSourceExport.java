package com.pd4ml.pdf.xfdf.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.security.PdfEncryptor;
import com.pd4ml.pdf.cos.writer.COSDocumentWriter;
import com.pd4ml.pdf.xfdf.XfdfDocument;
import com.pd4ml.pdf.xfdf.XfdfException;
import com.pd4ml.pdf.xfdf.XfdfExporter;
import com.pd4ml.pdf.xfdf.XfdfReader;

/**
 * 06 -- Exporting from an encrypted PDF: pass its password to
 * {@link XfdfExporter#export(byte[], String)} (or the matching {@code
 * Options}) and the pd4ml COS reader decrypts it transparently while
 * annotations/fields are read -- no special-casing needed. A wrong or
 * missing password fails clearly instead of silently returning nothing.
 */
public final class Ex06_EncryptedSourceExport {

    public static void main(String[] args) throws Exception {
        COSDocument toEncrypt = COSParser.load(SamplePdf.onePageWithAnnotations());
        PdfEncryptor.encrypt(toEncrypt, new PdfEncryptor.Options().userPassword("open"));
        byte[] encrypted = COSDocumentWriter.write(toEncrypt);

        XfdfDocument xfdf = XfdfReader.read(XfdfExporter.export(encrypted, "open"));
        System.out.println("Exported from encrypted PDF: " + xfdf.getAnnotations().size() + " annotation(s).");

        try {
            XfdfExporter.export(encrypted, "wrong-password");
        } catch (XfdfException e) {
            System.out.println("Wrong password: " + e.getMessage());
        }
    }
}
