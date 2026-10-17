package com.pd4ml.pdf.optimizer.example.usecases;

import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.security.PdfEncryptor;
import com.pd4ml.pdf.cos.writer.COSDocumentWriter;
import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.optimizer.PdfOptimizeException;
import com.pd4ml.pdf.optimizer.PdfOptimizer;

import java.nio.charset.Charset;

/** 06 -- Every {@link PdfOptimizeException} failure mode worth knowing by name. */
public final class Ex06_ErrorHandlingPatterns {

    private static final Charset LATIN1 = Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        try {
            // Not PDF syntax at all: recovery finds no "n g obj" headers, so there's
            // nothing to walk from -- reported explicitly rather than silently producing
            // an empty document.
            PdfOptimizer.optimize("not a PDF at all".getBytes(LATIN1));
        } catch (PdfOptimizeException e) {
            System.out.println("no recoverable content: " + e.getMessage());
        }

        COSDocument doc = COSParser.load(SamplePdf.onePage());
        PdfEncryptor.encrypt(doc, new PdfEncryptor.Options().userPassword("open").ownerPassword("owner-secret"));
        byte[] encrypted = COSDocumentWriter.write(doc);

        try {
            PdfOptimizer.optimize(encrypted, new PdfOptimizer.Options().password("wrong-password"));
        } catch (PdfOptimizeException e) {
            System.out.println("wrong password: " + e.getMessage());
        }
    }
}
