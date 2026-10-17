package com.pd4ml.pdf.optimizer.example.usecases;

import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.security.PdfEncryptor;
import com.pd4ml.pdf.cos.util.PageTree;
import com.pd4ml.pdf.cos.writer.COSDocumentWriter;
import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.optimizer.PdfOptimizeException;
import com.pd4ml.pdf.optimizer.PdfOptimizeResult;
import com.pd4ml.pdf.optimizer.PdfOptimizer;

/** 04 -- Optimizing an encrypted input needs its password; the output is plain by default (not re-encrypted). */
public final class Ex04_EncryptedInputRequiresPassword {

    public static void main(String[] args) throws Exception {
        COSDocument toEncrypt = COSParser.load(SamplePdf.nPages(2));
        PdfEncryptor.encrypt(toEncrypt, new PdfEncryptor.Options().userPassword("open").ownerPassword("owner-secret"));
        byte[] encrypted = COSDocumentWriter.write(toEncrypt);

        try {
            PdfOptimizer.optimize(encrypted);
        } catch (PdfOptimizeException e) {
            System.out.println("No password given: " + e.getMessage());
        }

        PdfOptimizer.Options options = new PdfOptimizer.Options().password("open");
        PdfOptimizeResult result = PdfOptimizer.optimize(encrypted, options);
        COSDocument optimized = COSParser.load(result.getOptimizedPdf());
        System.out.println("With the right password: isEncrypted=" + optimized.isEncrypted()
                + ", pages=" + PageTree.countPages(optimized.getCatalog()));
    }
}
